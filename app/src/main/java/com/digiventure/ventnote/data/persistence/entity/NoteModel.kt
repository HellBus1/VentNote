package com.digiventure.ventnote.data.persistence

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(
    tableName = "note_table",
    indices = [
        Index(value = ["title"]),
        Index(value = ["created_at"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_pinned"])
    ]
)
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,

    @ColumnInfo(name = "note")
    @SerializedName("note")
    val note: String,

    @ColumnInfo(name = "created_at")
    @SerializedName("createdAt", alternate = ["created_at"])
    var createdAt: Date = Date(System.currentTimeMillis()),

    @ColumnInfo(name = "updated_at")
    @SerializedName("updatedAt", alternate = ["updated_at"])
    var updatedAt: Date = Date(System.currentTimeMillis()),

    @ColumnInfo(name = "is_pinned")
    @SerializedName("isPinned", alternate = ["is_pinned"])
    val isPinned: Boolean = false,
): Parcelable