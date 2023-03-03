package com.digiventure.ventnote.navigation

sealed class Route(val routeName: String) {
    object NotesPage: Route(routeName = "notes_page")
}