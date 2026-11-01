package com.digiventure.ventnote.data.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "note_tag_table",
    primaryKeys = ["noteId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteModel::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagModel::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tagId"])]
)
data class NoteTagCrossRef(
    @SerializedName("noteId", alternate = ["note_id"])
    val noteId: Int,

    @SerializedName("tagId", alternate = ["tag_id"])
    val tagId: Int
)
