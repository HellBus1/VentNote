package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagModel

/**
 * Wrapper used for serializing/deserializing the entire app database to/from JSON for Google Drive backup.
 *
 * Versioning:
 * - version 0 (legacy): the old format that contained only a plain List<NoteModel> (no wrapper object).
 * - version 1 (current): this wrapper containing notes, tags, and noteTags.
 *
 * Backward-compat: when restoring an old backup (version 0 / plain array),
 * the caller should try [BackupPayload] first and fall back to [List<NoteModel>] deserialization.
 * Missing [tags] or [noteTags] fields default to empty lists via Gson's null-safe handling.
 */
data class BackupPayload(
    val version: Int = 1,
    val notes: List<NoteModel> = emptyList(),
    val tags: List<TagModel> = emptyList(),
    val noteTags: List<NoteTagCrossRef> = emptyList()
)
