package com.digiventure.ventnote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "note_table")
data class NoteModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at") val updatedAt: Date = Date(System.currentTimeMillis()),
    @ColumnInfo(name = "deleted_at") val deletedAt: Date = Date(System.currentTimeMillis()),
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
)