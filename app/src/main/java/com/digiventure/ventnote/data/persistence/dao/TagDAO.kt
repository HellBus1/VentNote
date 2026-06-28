package com.digiventure.ventnote.data.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDAO {
    @Query("SELECT * FROM tag_table ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagModel>>

    @Query("SELECT * FROM tag_table ORDER BY name ASC")
    suspend fun getAllTagsSync(): List<TagModel>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTag(tag: TagModel): Long

    @Update
    suspend fun updateTag(tag: TagModel): Int

    @Delete
    suspend fun deleteTag(tag: TagModel): Int

    /**
     * Get all tags associated with a specific note.
     */
    @Transaction
    @Query(
        "SELECT t.* FROM tag_table t " +
        "INNER JOIN note_tag_table nt ON t.id = nt.tagId " +
        "WHERE nt.noteId = :noteId"
    )
    suspend fun getTagsForNote(noteId: Int): List<TagModel>

    /**
     * Insert a note-tag cross-reference, ignoring duplicates.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTagCrossRef(crossRef: NoteTagCrossRef)

    /**
     * Insert multiple note-tag cross-references at once.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNoteTagCrossRefs(crossRefs: List<NoteTagCrossRef>)

    /**
     * Remove a single tag association from a note.
     */
    @Query("DELETE FROM note_tag_table WHERE noteId = :noteId AND tagId = :tagId")
    suspend fun deleteNoteTagCrossRef(noteId: Int, tagId: Int)

    /**
     * Remove all tag associations for a specific note (used on note delete).
     */
    @Query("DELETE FROM note_tag_table WHERE noteId = :noteId")
    suspend fun deleteAllTagsForNote(noteId: Int)

    /**
     * Replace all tags for a note — removes existing associations and inserts fresh ones.
     */
    @Transaction
    suspend fun setTagsForNote(noteId: Int, tagIds: List<Int>) {
        deleteAllTagsForNote(noteId)
        val refs = tagIds.map { NoteTagCrossRef(noteId = noteId, tagId = it) }
        insertNoteTagCrossRefs(refs)
    }

    /**
     * Get all NoteTagCrossRef rows reactively.
     */
    @Query("SELECT * FROM note_tag_table")
    fun getAllNoteTagCrossRefsFlow(): Flow<List<NoteTagCrossRef>>

    /**
     * Get all NoteTagCrossRef rows (used for backup serialization).
     */
    @Query("SELECT * FROM note_tag_table")
    suspend fun getAllNoteTagCrossRefs(): List<NoteTagCrossRef>
}
