# VentNote Technical Documentation

Welcome to the comprehensive technical documentation for **VentNote**, an intuitive, modern note management Android application built with Jetpack Compose, Kotlin Coroutines & Flow, Android Jetpack, and Google Material 3 Design principles.

---

## 📚 Documentation Index

The documentation follows the [Diátaxis Framework](https://diataxis.fr/) and is partitioned into architectural explanations, in-depth feature specifications with complete Mermaid diagrams, technical references, and a rigorous QA test matrix:

### 1. Architecture & System Design
* **[System Overview](architecture/system_overview.md)**: High-level architectural pattern (Clean Architecture + MVVM + MVI Unidirectional State Flow), technology stack, presentation & domain data flow pipeline, and dependency injection graph (Hilt).
* **[Database & Persistence](architecture/database_and_persistence.md)**: Room SQLite database schema (`NoteModel`, `TagModel`, `NoteTagCrossRef`), M:N relationship modeling with `@Relation` and `@Junction`, composite indices, cascading constraints, and DataStore key-value store.

### 2. Feature Specifications & Flows
* **[Note Management](features/note_management.md)**: Home screen mechanics, Staggered Grid vs. Linear List view toggle, multi-attribute sorting (`title`, `created_at`, `updated_at` in `ASC`/`DESC`), dynamic search filtering, and multi-select batch deletion.
* **[Note Editor & Rich Text](features/note_editor.md)**: Note creation & editing workflows, markdown formatting toolbar, title validation & empty title fallback (`"Untitled"`), and note tag assignment.
* **[Note Pinning](features/note_pinning.md)**: Pin priority mechanics, SQL sorting precedence (`is_pinned DESC`), card-level quick toggling, and persistence.
* **[Tags & Category System](features/tag_category_system.md)**: Tag Manager lifecycle, 12-color curated palette, case-insensitive duplicate prevention, home screen TagChipBar filter, and the hard constraint of maximum 3 tags per note.
* **[Google Drive Backup & Restore](features/google_drive_backup_restore.md)**: OAuth2 Google Sign-In, hidden `appDataFolder` storage, dual-format JSON restore (v1 wrapper vs. v0 legacy array), timestamp preservation guarantee, and automatic cleanup of older snapshots.
* **[App Customization & Theming](features/app_customization_and_theming.md)**: Material 3 Dynamic Color, 5 custom color palettes, Dark/Light mode switching, and Navigation Drawer structure.
* **[Launcher App Widget](features/app_widget.md)**: Android AppWidget provider, RemoteViews factory, and reactive refresh triggers via `WidgetRefresher`.

### 3. Manual QA Testing & Verification
* **[Comprehensive QA Test Matrix](manual_testing/qa_flow_matrix.md)**: Production-grade manual test matrix spanning 10 feature areas with Test IDs, preconditions, step-by-step procedures, expected outcomes, and edge-case verifications.

### 4. Technical Reference
* **[Data Contracts & Schemas](reference/data_contracts_and_schemas.md)**: Exact JSON payload contracts, Room entity definitions, Room DAO method signatures, and automated UI test tag mappings (`TestTags.kt`).

---

## 🏛 Quick Technology Summary

| Layer | Technologies & Libraries |
| :--- | :--- |
| **Language & Concurrency** | Kotlin 1.8+, Coroutines, StateFlow, SharedFlow, LiveData |
| **UI & Presentation** | Jetpack Compose (BOM), Material 3, Navigation Compose, Glance/RemoteViews |
| **Architecture** | Clean Architecture, MVVM with unidirectional data flow (UDF) |
| **Dependency Injection** | Dagger Hilt 2.44+ |
| **Local Persistence** | Room Database (SQLite), AndroidX DataStore Preferences |
| **Cloud Synchronization** | Google Drive REST API v3, Google Play Services Auth (OAuth2) |
| **Rich Text & Formatting** | Markdown Parser, Compose AnnotatedString, RichTextState |
| **Testing** | AndroidX Compose Test, Espresso, JUnit 4, Hilt Android Test |

---

## 🚀 Navigation Guide by Persona

* **For Android Engineers & Contributors:** Start with [System Overview](architecture/system_overview.md) and [Database & Persistence](architecture/database_and_persistence.md), then review [Data Contracts](reference/data_contracts_and_schemas.md).
* **For QA Engineers & Testers:** Go directly to the [Comprehensive QA Test Matrix](manual_testing/qa_flow_matrix.md) and check specific feature flows in [Features](features/note_management.md).
* **For Product Managers & Designers:** Explore the feature specifications in `features/` to understand user interaction states, validation constraints, and workflow diagrams.
