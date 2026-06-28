package com.digiventure.ventnote.data.persistence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val service: TagLocalService
) {
    fun getAllTags(): Flow<Result<List<TagModel>>> =
        service.getAllTags().map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: emptyList())
            else Result.failure(it.exceptionOrNull()!!)
        }

    fun insertTag(tag: TagModel): Flow<Result<Long>> =
        service.insertTag(tag).map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: -1L)
            else Result.failure(it.exceptionOrNull()!!)
        }

    fun updateTag(tag: TagModel): Flow<Result<Boolean>> =
        service.updateTag(tag).map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: false)
            else Result.failure(it.exceptionOrNull()!!)
        }

    fun deleteTag(tag: TagModel): Flow<Result<Boolean>> =
        service.deleteTag(tag).map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: false)
            else Result.failure(it.exceptionOrNull()!!)
        }

    fun getTagsForNote(noteId: Int): Flow<Result<List<TagModel>>> =
        service.getTagsForNote(noteId).map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: emptyList())
            else Result.failure(it.exceptionOrNull()!!)
        }

    fun setTagsForNote(noteId: Int, tagIds: List<Int>): Flow<Result<Boolean>> =
        service.setTagsForNote(noteId, tagIds).map {
            if (it.isSuccess) Result.success(it.getOrNull() ?: false)
            else Result.failure(it.exceptionOrNull()!!)
        }
}
