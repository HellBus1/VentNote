package com.digiventure.ventnote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note_table")
    fun getNotes(): Flow<List<NoteModel>>

    @Delete
    fun deleteNotes(vararg notes: NoteModel): Int
}