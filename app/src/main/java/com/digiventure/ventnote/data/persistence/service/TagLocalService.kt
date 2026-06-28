package com.digiventure.ventnote.data.persistence

import com.digiventure.ventnote.module.proxy.DatabaseProxy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagLocalService @Inject constructor(
    private val proxy: DatabaseProxy
) {
    fun getAllTags(): Flow<Result<List<TagModel>>> {
        return proxy.tagDao().getAllTags().map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException("Failed to get tags")))
        }
    }

    fun insertTag(tag: TagModel): Flow<Result<Long>> = flow {
        val result = proxy.tagDao().insertTag(tag)
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(RuntimeException("Failed to insert tag")))
    }

    fun updateTag(tag: TagModel): Flow<Result<Boolean>> = flow {
        val result = proxy.tagDao().updateTag(tag) >= 1
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(RuntimeException("Failed to update tag")))
    }

    fun deleteTag(tag: TagModel): Flow<Result<Boolean>> = flow {
        val result = proxy.tagDao().deleteTag(tag) >= 1
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(RuntimeException("Failed to delete tag")))
    }

    fun getTagsForNote(noteId: Int): Flow<Result<List<TagModel>>> = flow {
        val result = proxy.tagDao().getTagsForNote(noteId)
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(RuntimeException("Failed to get tags for note")))
    }

    fun setTagsForNote(noteId: Int, tagIds: List<Int>): Flow<Result<Boolean>> = flow {
        proxy.tagDao().setTagsForNote(noteId, tagIds)
        emit(Result.success(true))
    }.catch {
        emit(Result.failure(RuntimeException("Failed to set tags for note")))
    }
}
