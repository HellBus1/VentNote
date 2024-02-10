package com.digiventure.ventnote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

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
    fun updateNote(note: NoteModel): Int

    fun updateWithTimestamp(note: NoteModel): Int {
        return updateNote(note.apply{
            updatedAt = Date(System.currentTimeMillis())
        })
    }

    @Delete
    fun deleteNotes(vararg notes: NoteModel): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteModel): Long

    fun insertWithTimestamp(note: NoteModel): Long {
        return insertNote(note.apply{
            createdAt = Date(System.currentTimeMillis())
            updatedAt = Date(System.currentTimeMillis())
        })
    }
}