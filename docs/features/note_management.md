# Feature Specification: Note Management

The Note Management feature serves as the primary dashboard of VentNote (`NotesPage.kt`). It provides a fluid, responsive interface for viewing, organizing, searching, sorting, and batch-managing notes.

---

## 1. Feature Architecture & State Machine

```mermaid
stateDiagram-v2
    [*] --> NormalBrowsing

    state NormalBrowsing {
        [*] --> Idle
        Idle --> SearchActive : Click Search Bar / Type Query
        SearchActive --> Idle : Clear Search Query
        Idle --> FilterActive : Tap Category Tag Chip
        FilterActive --> Idle : Tap "All" Chip
        Idle --> BottomSheetOpen : Tap Sort & Filter Icon
        BottomSheetOpen --> Idle : Dismiss / Select Sort Option
        Idle --> ViewModeChanged : Tap List/Grid Icon
    }

    NormalBrowsing --> MarkingMode : Long-press Note Card
    
    state MarkingMode {
        [*] --> SingleSelected
        SingleSelected --> MultiSelected : Tap Additional Notes
        MultiSelected --> AllSelected : Tap "Select All"
        AllSelected --> NoneSelected : Tap "Unselect All"
        NoneSelected --> MultiSelected : Tap Notes
        MultiSelected --> ConfirmationDialog : Tap Delete Icon
        ConfirmationDialog --> Deleting : Tap Confirm
        ConfirmationDialog --> MultiSelected : Tap Dismiss
        Deleting --> [*]
    }

    MarkingMode --> NormalBrowsing : Tap Close (X) / Back Pressed / Deletion Finished
```

---

## 2. Key Capabilities & Mechanics

### 2.1 Dual View Layout: List vs. Staggered Grid
Users can toggle between a traditional single-column Linear List and a dynamic multi-column Staggered Grid:
* **List View (`Constants.VIEW_MODE_LIST`):** Displays notes in a single vertical column (`LazyColumn`). Note preview text is capped at 4 lines.
* **Staggered Grid View (`Constants.VIEW_MODE_STAGGERED`):** Displays notes in an adaptive grid (`LazyVerticalStaggeredGrid` with `StaggeredGridCells.Adaptive(minSize = 160.dp)`). Note preview text expands up to 8 lines, accommodating variable note lengths organically.
* **Persistence:** The chosen view mode is saved instantly to AndroidX DataStore (`NOTE_VIEW_MODE`), surviving app restarts.

```mermaid
flowchart LR
    User[User Clicks View Mode Icon] --> VM[NotesPageVM.setNoteViewMode]
    VM --> State[Update noteViewMode MutableState]
    VM --> DataStore[Persist to NoteDataStore Async]
    State --> Recompose[Instant UI Recomposition: LazyColumn <--> LazyVerticalStaggeredGrid]
```

---

### 2.2 Live Search Filtering
The search bar resides in the collapsible top app bar:
* Typing into the search field updates `NotesPageVM.searchedTitleText`.
* The list filters instantaneously in-memory on both `title` and `note` (body text), performing case-insensitive matching.
* If a category tag chip is simultaneously active, the search scope is strictly constrained to the notes inside that tag.

---

### 2.3 Sort & Filter System
A dedicated bottom sheet allows users to sort notes across three dimensions in either ascending or descending direction:

| Sort Attribute | Ascending (`ASC`) | Descending (`DESC`) |
| :--- | :--- | :--- |
| **Updated Date** (`updated_at`) | Oldest modified first | **Most recently modified first (Default)** |
| **Created Date** (`created_at`) | Oldest created first | Newest created first |
| **Title** (`title`) | Alphabetical (A → Z) | Reverse Alphabetical (Z → A) |

> [!NOTE]
> Pinned notes are always displayed at the very top of the list, regardless of which sort criteria or ordering direction is active (see [Note Pinning](note_pinning.md)).

---

### 2.4 Multi-Selection & Batch Deletion (Marking Mode)

Marking mode allows power users to manage multiple notes in a single transaction:

```mermaid
flowchart TD
    Start[User Long-Presses Note Card] --> EnableMarking[isMarking.value = true]
    EnableMarking --> AddToSelected[Add Note to markedNoteList]
    EnableMarking --> SwapAppBar[Top AppBar transforms into Selection Toolbar]
    
    SwapAppBar --> ActionChoice{User Action}
    ActionChoice -- Tap Note Card --> ToggleSelection[Toggle Note in markedNoteList]
    ActionChoice -- Dropdown: Select All --> SelectAll[Add all visible notes to markedNoteList]
    ActionChoice -- Dropdown: Unselect All --> UnselectAll[Clear markedNoteList]
    ActionChoice -- Close Button (X) --> Exit[isMarking.value = false; clear markedNoteList]
    
    ActionChoice -- Tap Delete Icon --> ConfirmDialog[Show Confirmation Dialog]
    ConfirmDialog -- Tap Dismiss --> Return[Return to Marking Mode]
    ConfirmDialog -- Tap Confirm --> ExecuteDelete[Call NotesPageVM.deleteNoteList]
    
    ExecuteDelete --> RoomDelete[NoteDAO.deleteNotes in SQLite]
    RoomDelete --> CascadeCrossRef[Cascade removes rows in note_tag_table]
    CascadeCrossRef --> RefreshList[Trigger observeNotes & Refresh Widget]
    RefreshList --> Exit
```

#### Detailed Marking Mode Features:
1. **Visual Feedback:** Selected cards display a highlighted checkmark icon in their header.
2. **Dynamic Counter:** The top app bar updates live with `"N selected"` as items are toggled.
3. **Dropdown Menu Actions:**
   * **Select All:** Adds all visible notes to the selection set.
   * **Unselect All:** Empties the selection set without exiting marking mode.
4. **Safety Confirmation:** Deleting selected notes triggers a modal confirmation dialog to prevent accidental data loss.
5. **Dismissal & Back Handler:** Pressing the device hardware back button or tapping the `"X"` icon cleanly exits marking mode and clears selections.
