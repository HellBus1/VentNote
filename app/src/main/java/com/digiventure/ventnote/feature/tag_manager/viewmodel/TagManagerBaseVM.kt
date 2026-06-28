package com.digiventure.ventnote.feature.tag_manager.viewmodel

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digiventure.ventnote.data.persistence.TagModel

interface TagManagerBaseVM {
    /** All tags */
    val tagList: LiveData<Result<List<TagModel>>>

    /** Loading state */
    val loader: MutableLiveData<Boolean>

    /** Load / observe all tags */
    fun observeTags()

    /** Insert a new tag */
    suspend fun insertTag(tag: TagModel): Result<Long>

    /** Update an existing tag */
    suspend fun updateTag(tag: TagModel): Result<Boolean>

    /** Delete a tag */
    suspend fun deleteTag(tag: TagModel): Result<Boolean>
}
