package com.digiventure.ventnote.navigation

sealed class Route(val routeName: String) {
    data object NotesPage: Route(routeName = "notes_page")
    data object NoteDetailPage: Route(routeName = "note_detail_page")
    data object NoteCreationPage: Route(routeName = "note_creation_page")
    data object SharePreviewPage: Route(routeName = "share_preview_page")
    data object BackupPage: Route(routeName = "backup_page")
}