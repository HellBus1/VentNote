package com.digiventure.ventnote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note_table")
    fun getNotes(): Flow<List<NoteModel>>

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNoteDetail(id: Int): Flow<NoteModel>

    @Update
    fun updateNote(vararg notes: NoteModel): Int

    @Delete
    fun deleteNotes(vararg notes: NoteModel): Int
}