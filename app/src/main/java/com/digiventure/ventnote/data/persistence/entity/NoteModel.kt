package com.digiventure.ventnote.data.persistence

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(
    tableName = "note_table",
    indices = [
        Index(value = ["title"]),
        Index(value = ["created_at"]),
        Index(value = ["updated_at"])
    ]
)
data class NoteModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "created_at") var createdAt: Date = Date(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at") var updatedAt: Date = Date(System.currentTimeMillis()),
): Parcelable