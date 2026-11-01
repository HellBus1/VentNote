# Manual QA Testing Flow Matrix

This document provides a comprehensive, production-grade manual QA testing matrix for **VentNote**. It is designed for QA engineers and developers to systematically execute end-to-end verification across every app feature and edge case.

---

## 📋 Test Execution Summary

| Module | Feature Area | Total Cases | P0 (Critical) | P1 (High) | P2 (Medium) |
| :---: | :--- | :---: | :---: | :---: | :---: |
| **01** | Note Creation & Rich Text | 5 | 3 | 2 | 0 |
| **02** | Note Viewing & Detail | 3 | 2 | 1 | 0 |
| **03** | Note Editing & Discard Safeguards | 4 | 2 | 2 | 0 |
| **04** | Note Pinning & Priority Sorting | 4 | 3 | 1 | 0 |
| **05** | Search, Sort & Ordering | 5 | 2 | 2 | 1 |
| **06** | Marking Mode & Batch Deletion | 5 | 3 | 2 | 0 |
| **07** | Tag & Category Management | 7 | 4 | 2 | 1 |
| **08** | Google Drive Backup & Restore | 6 | 4 | 2 | 0 |
| **09** | Layout, Theming & Adaptive UI | 5 | 2 | 2 | 1 |
| **10** | Launcher App Widget | 3 | 1 | 2 | 0 |
| **TOTAL**| | **47** | **26** | **18** | **3** |

---

## 🧪 Detailed QA Test Matrix

### Module 01: Note Creation & Rich Text

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-NC-01** | P0 | Create Standard Note with Title & Body | App is open on `NotesPage` | 1. Tap `+` FAB.<br>2. Enter Title: `"Weekly Groceries"`.<br>3. Enter Body: `"Milk, Bread, Butter"`.<br>4. Tap Checkmark/Save button. | 1. Screen pops back to `NotesPage`.<br>2. Success snackbar appears.<br>3. Card displays title `"Weekly Groceries"` and body snippet. | Verify date is set to current time. | [ ] |
| **TC-NC-02** | P0 | Create Note with Empty Title ("Untitled" Fallback) | App is open on `NotesPage` | 1. Tap `+` FAB.<br>2. Leave Title field completely empty.<br>3. Enter Body: `"Spontaneous idea without title"`.<br>4. Tap Save. | 1. Save succeeds without error.<br>2. Card on `NotesPage` displays fallback title `"Untitled"` in de-emphasized typography. | Verifies empty title flexibility without validation blocking. | [ ] |
| **TC-NC-03** | P0 | Validate Empty Body Rejection | Note creation screen open | 1. Enter Title: `"Empty Note Test"`.<br>2. Leave Body field completely empty.<br>3. Tap Save. | 1. Save is blocked.<br>2. Modal dialog appears: *"Note body cannot be empty"*. | Ensures blank notes cannot pollute the database. | [ ] |
| **TC-NC-04** | P1 | Rich Text Formatting in Note Editor | Note creation screen open | 1. Type `"Formatted text"`.<br>2. Select text and tap **Bold** icon.<br>3. Add bulleted list and strikethrough text.<br>4. Tap Save. | 1. Text is correctly saved with Markdown syntax.<br>2. Note card parses and displays styled text preview. | Test undo/redo and cursor positioning with formatting. | [ ] |
| **TC-NC-05** | P1 | Assign Tags During Note Creation | At least 2 tags exist in app | 1. Tap `+` FAB.<br>2. Tap **"Add Tags"** chip.<br>3. Select 2 tags in `TagPickerBottomSheet`.<br>4. Tap outside sheet to dismiss.<br>5. Fill body and tap Save. | 1. Selected tags appear as colored chips under title in editor.<br>2. Note card on `NotesPage` renders the corresponding colored tag chips. | Tags must persist immediately on insert. | [ ] |

---

### Module 02: Note Viewing & Detail

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-NV-01** | P0 | View Existing Note Detail | Note exists on `NotesPage` | 1. Tap on the note card. | 1. Navigates smoothly to `NoteDetailPage`.<br>2. Renders exact title, formatted markdown body, and creation date. | Verifies non-editing read-only presentation. | [ ] |
| **TC-NV-02** | P0 | View "Untitled" Note Detail | An untitled note exists | 1. Tap the untitled note card. | 1. Opens `NoteDetailPage`.<br>2. Title field is blank (allows adding title), body is intact. | Check that placeholder shows *"Title"* hint. | [ ] |
| **TC-NV-03** | P1 | Share Note Preview | Note exists | 1. Open note detail.<br>2. Tap Share icon in bottom app bar.<br>3. Select share format (Text / Image preview). | 1. Opens `SharePreviewPage`.<br>2. Android system share sheet is summoned with rendered note content. | Test sharing with long markdown text. | [ ] |

---

### Module 03: Note Editing & Discard Safeguards

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-NE-01** | P0 | Edit Title, Body & Update Note | Note exists | 1. Open note detail.<br>2. Tap Edit icon.<br>3. Edit title and append new paragraphs.<br>4. Tap Save. | 1. Note updates successfully in SQLite.<br>2. `updatedAt` date changes to current time.<br>3. Snackbar *"Note updated"* appears. | Verify `createdAt` remains unchanged. | [ ] |
| **TC-NE-02** | P0 | Discard Edits via Cancel Dialog | Note detail in edit mode | 1. Modify the title and body text.<br>2. Tap Cancel icon on bottom bar.<br>3. In discard confirmation dialog, tap **Confirm**. | 1. Edit mode exits.<br>2. All changes are discarded.<br>3. Fields revert to their original database values. | Verifies state restoration. | [ ] |
| **TC-NE-03** | P1 | Dismiss Cancel Dialog (Continue Editing) | Note detail in edit mode | 1. Modify text.<br>2. Tap Cancel icon.<br>3. In dialog, tap **Dismiss**. | 1. Dialog closes.<br>2. Editor remains active with uncommitted modifications preserved. | User flow continuity check. | [ ] |
| **TC-NE-04** | P1 | Add/Remove Tags in Edit Mode | Note has 1 tag attached | 1. Enter edit mode.<br>2. Tap `"X"` on existing tag chip to remove it.<br>3. Tap `"Add Tags"`, select 2 different tags.<br>4. Tap Save. | 1. Junction table updates.<br>2. The note now reflects only the new 2 tags. | Verify `deleteAllTagsForNote` atomic swap. | [ ] |

---

### Module 04: Note Pinning & Priority Sorting

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-NP-01** | P0 | Pin Note from Card Icon | Note is unpinned | 1. On `NotesPage`, locate unpinned note at middle/bottom.<br>2. Tap the Pin icon on the top-right of the card. | 1. Spring micro-animation plays.<br>2. Pin icon fills with primary theme color.<br>3. Note immediately shifts to the very top of the list. | Database `is_pinned` column updates to `true`. | [ ] |
| **TC-NP-02** | P0 | Unpin Note | Note is pinned at top | 1. Tap the filled Pin icon on the pinned note. | 1. Pin icon switches to outline.<br>2. Note relocates down to its normal chronological position. | Database `is_pinned` column updates to `false`. | [ ] |
| **TC-NP-03** | P0 | Pin Priority Overrides All Sort Orders | Multiple pinned & unpinned notes exist | 1. Open Sort bottom sheet.<br>2. Select *"Title (A-Z)"*.<br>3. Select *"Created Date (Oldest first)"*. | 1. Pinned notes remain grouped at the top under all sorting criteria.<br>2. Within the pinned group, notes follow the selected sort rule. | Verifies `ORDER BY is_pinned DESC` precedence. | [ ] |
| **TC-NP-04** | P1 | Pin Persistence Across App Restart | Note is pinned | 1. Pin a note.<br>2. Force-stop VentNote in Android Settings.<br>3. Relaunch VentNote. | 1. Note remains pinned at the top. | Verifies Room database persistence. | [ ] |

---

### Module 05: Search, Sort & Ordering

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-SS-01** | P0 | Real-Time Search by Title | Notes exist with distinct titles | 1. Tap search bar in top app bar.<br>2. Type query matching note title (e.g., `"Budget"`). | 1. List filters instantaneously.<br>2. Only notes with titles containing `"Budget"` (case-insensitive) are shown. | Search as you type responsiveness. | [ ] |
| **TC-SS-02** | P0 | Real-Time Search by Body Content | Note with unique body text | 1. Type query matching text in note body but NOT title (e.g., `"Groceries"`). | 1. Matching note appears in search results. | Search covers both title and body. | [ ] |
| **TC-SS-03** | P1 | Sort by Updated Date (Newest vs Oldest) | Multiple notes exist | 1. Open Sort sheet.<br>2. Toggle between Descending and Ascending. | 1. Descending shows most recently modified first.<br>2. Ascending shows oldest modified first. | Pinned notes still pinned at top. | [ ] |
| **TC-SS-04** | P1 | Sort by Title (Alphabetical) | Multiple notes exist | 1. Open Sort sheet.<br>2. Select Title Ascending (A-Z).<br>3. Select Title Descending (Z-A). | 1. Notes reorder alphabetically by title string. | Untitled notes sort according to empty string sorting. | [ ] |
| **TC-SS-05** | P2 | Search Cleared State | Search is active | 1. Tap `"X"` in search bar. | 1. Search text clears.<br>2. Full note list is restored instantly. | Fast reset verification. | [ ] |

---

### Module 06: Marking Mode & Batch Deletion

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-MM-01** | P0 | Enter Marking Mode via Long-Press | `NotesPage` with notes | 1. Long-press any note card. | 1. Haptic feedback triggers.<br>2. Top app bar transforms into selection mode.<br>3. Selected note displays checkmark icon.<br>4. Counter shows `"1 selected"`. | Normal click navigation disabled during marking. | [ ] |
| **TC-MM-02** | P0 | Multi-Select and Toggle Cards | In marking mode | 1. Tap 2 additional note cards.<br>2. Tap 1 already-selected note card. | 1. Counter updates to `"3 selected"`.<br>2. Deselected note unchecks and counter decreases to `"2 selected"`. | Responsive counter verification. | [ ] |
| **TC-MM-03** | P0 | Batch Delete with Confirmation | 3 notes selected | 1. Tap Delete (trash) icon in top bar.<br>2. In confirmation dialog, tap **Confirm**. | 1. Loading spinner displays briefly.<br>2. The 3 notes are permanently removed from SQLite.<br>3. Exits marking mode; list refreshes. | Cross-references cascade deleted. | [ ] |
| **TC-MM-04** | P1 | Select All / Unselect All from Menu | In marking mode | 1. Tap three-dot menu in selection bar.<br>2. Tap **"Select All"**.<br>3. Tap **"Unselect All"**. | 1. "Select All" checks all visible notes; counter matches total count.<br>2. "Unselect All" clears all checks; counter shows `"0 selected"`. | Test with filtered list active. | [ ] |
| **TC-MM-05** | P1 | Exit Marking Mode via Back / Close | In marking mode | 1. Tap `"X"` close button or Android hardware back button. | 1. Marking mode terminates.<br>2. All selections are cleared.<br>3. Standard top app bar returns. | Zero unwanted mutations. | [ ] |

---

### Module 07: Tag & Category Management

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-TM-01** | P0 | Create New Tag with Custom Color | Nav drawer open | 1. Tap **"Tags"** in drawer.<br>2. Tap `+` FAB.<br>3. Enter name: `"Health"`.<br>4. Select Green palette circle.<br>5. Tap Save. | 1. Tag `"Health"` is inserted into `tag_table`.<br>2. Appears in Tag Manager list and home screen `TagChipBar`. | Verify color hex matches selection. | [ ] |
| **TC-TM-02** | P0 | Duplicate Tag Name Prevention (Case-Insensitive) | Tag `"Work"` exists | 1. Tap `+` FAB in Tag Manager.<br>2. Enter name: `"work"` (lowercase).<br>3. Tap Save. | 1. Insertion is rejected.<br>2. Error message: *"Tag name already exists"*. | Prevents duplicate categories. | [ ] |
| **TC-TM-03** | P0 | Enforce Maximum 3 Tags per Note | Note creation or edit open; >3 tags exist in app | 1. Open `TagPickerBottomSheet`.<br>2. Select 3 tags (counter shows `"3/3"`).<br>3. Attempt to tap a 4th unselected tag. | 1. 4th tag is disabled and cannot be selected.<br>2. Deselecting 1 tag re-enables selection. | Strict hard constraint verification. | [ ] |
| **TC-TM-04** | P0 | Filter Notes by Tag Chip on Home Screen | Notes with various tags exist | 1. In `TagChipBar` on `NotesPage`, tap `"Health"` chip. | 1. Chip highlights with active styling.<br>2. List displays only notes tagged with `"Health"`.<br>3. Tapping `"All"` restores complete list. | SQL JOIN query verification. | [ ] |
| **TC-TM-05** | P1 | Edit Tag Name & Color | Tag exists | 1. In Tag Manager, tap Edit icon next to tag.<br>2. Change name from `"Health"` to `"Fitness"` and select Blue.<br>3. Tap Save. | 1. Tag updates in SQLite.<br>2. All notes tagged with this tag immediately reflect `"Fitness"` in blue. | Reactive Flow combine verification. | [ ] |
| **TC-TM-06** | P1 | Delete Tag & Verify Note Preservation (Cascade) | Tag `"Fitness"` assigned to 2 notes | 1. In Tag Manager, tap Delete icon on `"Fitness"`.<br>2. Confirm deletion dialog. | 1. Tag is deleted from `tag_table`.<br>2. The 2 notes **remain intact** in `note_table` without being deleted.<br>3. Removed from `TagChipBar`. | SQLite `ON DELETE CASCADE` test. | [ ] |
| **TC-TM-07** | P2 | Validate Empty Tag Name Rejection | Tag Manager open | 1. Open Create Tag dialog.<br>2. Leave name empty and tap Save. | 1. Save is blocked by validation. | Blank tag prevention. | [ ] |

---

### Module 08: Google Drive Cloud Backup & Restore

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-GD-01** | P0 | Google Sign-In with Drive Scope | Google Play Services installed | 1. Open Nav Drawer -> Tap **"Backup"**.<br>2. Tap **"Sign in with Google"**.<br>3. Select Google account. | 1. OAuth consent completes.<br>2. User email and avatar display in `BackupPage`.<br>3. Backup action button becomes enabled. | `DRIVE_APPDATA` scope granted. | [ ] |
| **TC-GD-02** | P0 | Create Cloud Backup (Version 1 Schema) | User is signed in; notes and tags exist | 1. Tap **"Backup Now"**.<br>2. Observe sync status. | 1. Progress spinner appears.<br>2. Database serialized into `BackupPayload` JSON.<br>3. File uploaded to `appDataFolder`.<br>4. Backup file appears in list with current timestamp. | Verify JSON structure contains notes, tags, noteTags. | [ ] |
| **TC-GD-03** | P0 | Restore Backup & Verify Timestamp Preservation | Fresh install or cleared data | 1. Sign in to Google.<br>2. Select existing backup from list.<br>3. Tap **"Restore"**.<br>4. Return to `NotesPage`. | 1. All notes, tags, and category associations are restored.<br>2. **Original creation and edit timestamps are retained exactly**.<br>3. Launcher widget updates. | Crucial: Timestamps must NOT be reset to current time. | [ ] |
| **TC-GD-04** | P1 | Restore Legacy Version 0 Backup (Backward Compatibility) | Legacy backup JSON in Drive | 1. Select legacy backup file (plain array format).<br>2. Tap Restore. | 1. Legacy notes are restored successfully.<br>2. Notes default to uncategorized (empty tags).<br>3. Zero crashes or JsonSyntaxExceptions. | Verifies fallback parsing in `GoogleDriveService`. | [ ] |
| **TC-GD-05** | P1 | Old Backup Pruning / Cleanup | Multiple backups created | 1. Create a 3rd backup in succession. | 1. Old redundant backup files are cleanly purged from Drive to save quota. | Seamless cleanup verification. | [ ] |
| **TC-GD-06** | P1 | Network Offline Error Handling during Sync | Device in Airplane Mode | 1. Enable Airplane Mode.<br>2. Attempt Backup or Restore. | 1. Error state displays cleanly with error message.<br>2. App does not crash or corrupt local SQLite. | Network resilience check. | [ ] |

---

### Module 09: Layout, Theming & Adaptive UI

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-TH-01** | P0 | Toggle View Mode (List vs Staggered Grid) | `NotesPage` with notes | 1. Tap layout toggle icon in top bar.<br>2. Observe layout change.<br>3. Relaunch app. | 1. Transitions between 1-column `LazyColumn` and 2-column `LazyVerticalStaggeredGrid`.<br>2. Choice persists across restart via DataStore. | Check card spacing and visual balance. | [ ] |
| **TC-TH-02** | P0 | Toggle Dark Mode / Light Mode | Nav drawer open | 1. Toggle Theme Mode switch in drawer. | 1. UI instantly recomposes into Dark or Light Material 3 color tokens.<br>2. Contrast remains crisp and legible in both modes. | OLED black background check in dark mode. | [ ] |
| **TC-TH-03** | P1 | Switch Material 3 Color Palettes | Nav drawer open | 1. Tap each of the 4 color circles (Purple, Crimson, Green, Blue). | 1. Primary, container, FAB, and accent colors switch immediately across all screens.<br>2. Choice persists in DataStore. | Test consistency on buttons, chips, and sliders. | [ ] |
| **TC-TH-04** | P1 | Adaptive Screen Width on Tablets / Large Screens | Tablet emulator or landscape mode | 1. Rotate device to landscape or run on tablet.<br>2. Open Nav Drawer.<br>3. Observe notes grid. | 1. Nav Drawer clamps cleanly at `320.dp` instead of over-stretching.<br>2. Staggered grid scales to 3+ columns responsively. | Verifies `widthIn(max = 320.dp)` constraints. | [ ] |
| **TC-TH-05** | P2 | In-App Update Check | Nav drawer open | 1. Tap **"Check for updates"** tile. | 1. Triggers Play Core in-app update check or notifies up-to-date status. | Non-blocking execution. | [ ] |

---

### Module 10: Launcher App Widget

| Test ID | Pri | Scenario | Preconditions | Step-by-Step Execution | Expected Result | Edge Cases / Notes | Status |
| :--- | :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-WG-01** | P0 | Add Widget to Home Screen & View Notes | Android home screen | 1. Long-press home screen -> Widgets -> VentNote.<br>2. Place widget on home screen. | 1. Widget displays scrollable list of recent notes.<br>2. Displays titles, snippets, and dates. | Layout fits standard 4x2 or 4x3 dimensions. | [ ] |
| **TC-WG-02** | P1 | Deep-Link from Widget to Note Detail | Widget placed | 1. Tap any note row inside the widget. | 1. VentNote launches directly into `NoteDetailPage` for that specific note. | PendingIntent collection template test. | [ ] |
| **TC-WG-03** | P1 | Reactive Widget Invalidation | Widget placed | 1. Add a note or edit a note in the app.<br>2. Return to Android home screen. | 1. Widget immediately reflects the newly added/edited note without manual refresh. | Verifies `WidgetRefresher.refresh(app)`. | [ ] |
