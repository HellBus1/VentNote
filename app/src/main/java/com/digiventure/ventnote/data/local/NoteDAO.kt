package com.digiventure.ventnote.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note_table")
    fun getNotes(): Flow<List<NoteModel>>

    @Query("SELECT * FROM note_table")
    fun getPlainNotes(): List<NoteModel>

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNoteDetail(id: Int): Flow<NoteModel>

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getPlainNoteDetail(id: Int): NoteModel

    @Update
    fun updateNote(vararg notes: NoteModel): Int

    @Delete
    fun deleteNotes(vararg notes: NoteModel): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteModel): Long
}