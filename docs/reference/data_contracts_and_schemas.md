# Technical Reference: Data Contracts & Schemas

This document contains exact specifications for serialized data formats, Room DAO query signatures, and UI testing semantics.

---

## 1. Google Drive Backup Data Contracts

### 1.1 Version 1 Schema (Current Full Workspace Envelope)

```typescript
interface BackupPayloadV1 {
  /** Schema format version identifier (currently 1) */
  version: 1;
  
  /** Array of all note entities in the database */
  notes: Array<{
    id: number;
    title: string;
    note: string;
    createdAt: number; // Unix epoch millisecond timestamp
    updatedAt: number; // Unix epoch millisecond timestamp
    isPinned: boolean;
  }>;
  
  /** Array of all user tags */
  tags: Array<{
    id: number;
    name: string;
    colorHex: string; // Hex color string, e.g. "#EF9A9A"
  }>;
  
  /** Relational junction links between notes and tags */
  noteTags: Array<{
    noteId: number;
    tagId: number;
  }>;
}
```

#### JSON Sample:
```json
{
  "version": 1,
  "notes": [
    {
      "id": 1,
      "title": "Meeting Agenda",
      "note": "Review Q3 deliverables and roadmap.",
      "createdAt": 1782627000000,
      "updatedAt": 1782627500000,
      "isPinned": true
    }
  ],
  "tags": [
    {
      "id": 10,
      "name": "Work",
      "colorHex": "#90CAF9"
    }
  ],
  "noteTags": [
    {
      "noteId": 1,
      "tagId": 10
    }
  ]
}
```

---

### 1.2 Version 0 Schema (Legacy Plain Array)

```typescript
type BackupPayloadV0 = Array<{
  id: number;
  title: string;
  note: string;
  createdAt: number;
  updatedAt: number;
}>;
```

---

## 2. Room DAO Contracts & Signatures

### 2.1 NoteDAO ([`NoteDAO.kt`](file:///Users/syubbanfakhriya/Desktop/Repository/side-project/VentNote/app/src/main/java/com/digiventure/ventnote/data/persistence/dao/NoteDAO.kt))

| Method Signature | Return Type | Description |
| :--- | :--- | :--- |
| `getNotes(sortBy: String, orderBy: String)` | `Flow<List<NoteModel>>` | Reactive stream of all notes, sorted by `is_pinned DESC` followed by dynamic criteria. |
| `getNotesByTag(tagId: Int, sortBy: String, orderBy: String)` | `Flow<List<NoteModel>>` | Reactive stream of notes associated with `tagId` via `note_tag_table`. |
| `getNoteDetail(id: Int)` | `Flow<NoteModel>` | Reactive single note observer for `NoteDetailPage`. |
| `getPlainNoteDetail(id: Int)` | `NoteModel` | Synchronous note lookup for widget or background tasks. |
| `getSyncNotes()` | `List<NoteModel>` | Synchronous list of all notes ordered by `created_at DESC` (used by AppWidgetFactory). |
| `insertNote(note: NoteModel)` | `Long` | Inserts note with `OnConflictStrategy.REPLACE`. |
| `insertWithTimestamp(note: NoteModel)` | `Long` | Sets current timestamp for `createdAt` and `updatedAt` before inserting. |
| `updateNote(note: NoteModel)` | `Int` | Updates note fields. |
| `updateWithTimestamp(note: NoteModel)` | `Int` | Updates `updatedAt` to current timestamp before saving. |
| `deleteNotes(vararg notes: NoteModel)` | `Int` | Deletes one or more note entities. Triggers cascading foreign key deletes. |
| `upsertNotes(notes: List<NoteModel>)` | `Unit` | Batch inserts or updates notes retaining their existing timestamps (used in restore). |
| `setPinned(noteId: Int, isPinned: Boolean)` | `Unit` | Updates pin status directly in SQLite. |

---

### 2.2 TagDAO ([`TagDAO.kt`](file:///Users/syubbanfakhriya/Desktop/Repository/side-project/VentNote/app/src/main/java/com/digiventure/ventnote/data/persistence/dao/TagDAO.kt))

| Method Signature | Return Type | Description |
| :--- | :--- | :--- |
| `getAllTags()` | `Flow<List<TagModel>>` | Reactive stream of all tags ordered by name alphabetically. |
| `getAllTagsSync()` | `List<TagModel>` | Synchronous fetch of all tags. |
| `insertTag(tag: TagModel)` | `Long` | Inserts tag with `OnConflictStrategy.ABORT`. |
| `updateTag(tag: TagModel)` | `Int` | Updates tag name and color. |
| `deleteTag(tag: TagModel)` | `Int` | Deletes tag. Automatically cascades to delete matching rows in `note_tag_table`. |
| `getTagsForNote(noteId: Int)` | `List<TagModel>` | Queries all tags linked to `noteId`. |
| `setTagsForNote(noteId: Int, tagIds: List<Int>)` | `Unit` | Transactionally wipes existing tags for `noteId` and inserts new cross-references. |
| `getAllNoteTagCrossRefsFlow()` | `Flow<List<NoteTagCrossRef>>` | Reactive stream of all junction rows. |
| `getAllNoteTagCrossRefs()` | `List<NoteTagCrossRef>` | Fetches all junction rows for backup serialization. |

---

## 3. UI Test Tags Dictionary ([`TestTags.kt`](file:///Users/syubbanfakhriya/Desktop/Repository/side-project/VentNote/app/src/main/java/com/digiventure/ventnote/commons/TestTags.kt))

| Constant | Tag Value | UI Component / Screen |
| :--- | :--- | :--- |
| `NOTES_PAGE` | `"notes_feature"` | Main dashboard scaffold root |
| `NOTE_DETAIL_PAGE` | `"note_detail_page"` | Note detail / editor screen root |
| `NOTE_CREATION_PAGE` | `"note_creation_page"` | Note creation screen root |
| `TAG_MANAGER_PAGE` | `"tag_manager_page"` | Tag Manager screen root |
| `TOP_APPBAR` | `"top_appbar"` | Collapsible top application bar |
| `TOP_APPBAR_TEXT_FIELD`| `"top_appbar_text_field"` | Live search text input field |
| `ADD_NOTE_FAB` | `"add_note_fab"` | Floating action button to create note |
| `PIN_ICON_BUTTON` | `"pin_icon_button"` | Note card pin toggle button (appended with `_${note.id}`) |
| `BOTTOM_SHEET` | `"bottom_sheet"` | Sort and filter modal bottom sheet |
| `SORT_ICON_BUTTON` | `"sort_icon_button"` | Top bar action button to open sort sheet |
| `DELETE_ICON_BUTTON` | `"delete_icon_button"` | Top bar action button to trigger batch delete |
| `CONFIRMATION_DIALOG`| `"confirmation_dialog"` | Safety confirmation dialog |
| `CONFIRM_BUTTON` | `"confirm_button"` | Positive action button inside dialogs |
| `DISMISS_BUTTON` | `"dismiss_button"` | Negative action button inside dialogs |
| `NAV_DRAWER` | `"nav_drawer"` | Modal navigation drawer sheet |
| `BACKUP_TILE` | `"backup_tile"` | Drawer navigation tile to open Google Drive backup |
| `TAGS_TILE` | `"tags_tile"` | Drawer navigation tile to open Tag Manager |
