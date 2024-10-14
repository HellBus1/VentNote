package com.digiventure.ventnote.commons

object Constants {
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val TITLE = "title"
    const val DESCENDING = "DESC"
    const val ASCENDING = "ASC"
    const val GLOBAL_PREFERENCE = "GLOBAL_PREFERENCE"
    const val COLOR_SCHEME = "COLOR_SCHEME"
    const val COLOR_PALLET = "COLOR_PALLET"
    const val BACKUP_FILE_NAME = "backup"
    const val EMPTY_STRING = ""
}

object ColorPalletName {
    const val CRIMSON = "CRIMSON"
    const val PURPLE = "PURPLE"
    const val CADMIUM_GREEN = "CADMIUM_GREEN"
    const val COBALT_BLUE = "COBALT_BLUE"
}

object ColorSchemeName {
    const val DARK_MODE = "DARK_MODE"
    const val LIGHT_MODE = "LIGHT_MODE"
}

object ErrorMessage {
    const val FAILED_GET_NOTE_LIST_ROOM = "Failed to get list of notes"
    const val FAILED_DELETE_ROOM = "Failed to delete list of notes"
    const val FAILED_GET_NOTE_DETAIL_ROOM = "Failed to get note detail"
    const val FAILED_UPDATE_NOTE_ROOM = "Failed to update list of notes"
    const val FAILED_INSERT_NOTE_ROOM = "Failed to insert list of notes"

    const val FAILED_UPLOAD_DATABASE_FILE = "Failed to upload backup file"
    const val FAILED_RESTORE_DATABASE_FILE = "Failed to restore backup file"
    const val FAILED_GET_LIST_BACKUP_FILE = "Failed to get backup files"
    const val FAILED_DELETE_DATABASE_FILE = "Failed to delete file"
}