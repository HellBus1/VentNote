package com.digiventure.ventnote.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date
import com.digiventure.ventnote.data.persistence.NoteWithTags

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note_table ORDER BY " +
            "        is_pinned DESC, " +
            "        CASE WHEN :sortBy = 'title' AND :orderBy = 'ASC' THEN title END ASC, " +
            "        CASE WHEN :sortBy = 'title' AND :orderBy = 'DESC' THEN title END DESC, " +
            "        CASE WHEN :sortBy = 'created_at' AND :orderBy = 'ASC' THEN created_at END ASC, " +
            "        CASE WHEN :sortBy = 'created_at' AND :orderBy = 'DESC' THEN created_at END DESC, " +
            "        CASE WHEN :sortBy = 'updated_at' AND :orderBy = 'ASC' THEN updated_at END ASC," +
            "        CASE WHEN :sortBy = 'updated_at' AND :orderBy = 'DESC' THEN updated_at END DESC")
    fun getNotes(sortBy: String, orderBy: String): Flow<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY created_at DESC")
    fun getSyncNotes(): List<NoteModel>

    /**
     * Get all notes filtered by a specific tag, respecting sort/order preferences.
     */
    @Transaction
    @Query(
        "SELECT note_table.* FROM note_table " +
        "INNER JOIN note_tag_table ON note_table.id = note_tag_table.noteId " +
        "WHERE note_tag_table.tagId = :tagId " +
        "ORDER BY " +
        "is_pinned DESC, " +
        "CASE WHEN :sortBy = 'title' AND :orderBy = 'ASC' THEN title END ASC, " +
        "CASE WHEN :sortBy = 'title' AND :orderBy = 'DESC' THEN title END DESC, " +
        "CASE WHEN :sortBy = 'created_at' AND :orderBy = 'ASC' THEN created_at END ASC, " +
        "CASE WHEN :sortBy = 'created_at' AND :orderBy = 'DESC' THEN created_at END DESC, " +
        "CASE WHEN :sortBy = 'updated_at' AND :orderBy = 'ASC' THEN updated_at END ASC, " +
        "CASE WHEN :sortBy = 'updated_at' AND :orderBy = 'DESC' THEN updated_at END DESC"
    )
    fun getNotesByTag(tagId: Int, sortBy: String, orderBy: String): Flow<List<NoteModel>>

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

    @Transaction
    fun insertWithTimestamp(note: NoteModel): Long {
        return insertNote(note.apply{
            createdAt = Date(System.currentTimeMillis())
            updatedAt = Date(System.currentTimeMillis())
        })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertNotes(notes: List<NoteModel>)

    @Transaction
    fun upsertNotesWithTimestamp(notes: List<NoteModel>) {
        val currentTimestamp = Date(System.currentTimeMillis())
        notes.forEach { note ->
            note.createdAt = currentTimestamp
            note.updatedAt = currentTimestamp
        }
        upsertNotes(notes)
    }

    @Query("UPDATE note_table SET is_pinned = :isPinned WHERE id = :noteId")
    fun setPinned(noteId: Int, isPinned: Boolean)
}