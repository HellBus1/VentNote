package com.digiventure.ventnote.navigation

sealed class Route(val routeName: String) {
    object NotesPage: Route(routeName = "notes_page")
    object NoteDetailPage: Route(routeName = "note_detail_page")
    object NoteCreationPage: Route(routeName = "note_creation_page")
    object HelpPage: Route(routeName = "help_page")
    object AboutPage: Route(routeName = "about_page")
}