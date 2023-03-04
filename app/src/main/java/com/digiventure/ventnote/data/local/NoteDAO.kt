package com.digiventure.ventnote.data.local

import androidx.room.Dao
import androidx.room.Query
import com.digiventure.ventnote.data.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note_table")
    suspend fun getNotes(): List<NoteModel>
}