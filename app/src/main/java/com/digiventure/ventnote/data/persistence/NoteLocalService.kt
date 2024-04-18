package com.digiventure.ventnote.data.persistence

import com.digiventure.ventnote.commons.ErrorMessage
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val proxy: DatabaseProxy
) {
    suspend fun getNoteList(sortBy: String, order: String): Flow<Result<List<NoteModel>>> {
        return proxy.getObject().dao().getNotes(sortBy, order).map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_GET_NOTE_LIST_ROOM)))
        }
    }

    suspend fun deleteNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        flow {
            val result = (proxy.getObject().dao().deleteNotes(*notes) == notes.size)
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_DELETE_ROOM)))
        }

    suspend fun getNoteDetail(id: Int): Flow<Result<NoteModel>> {
        return proxy.getObject().dao().getNoteDetail(id).map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_GET_NOTE_DETAIL_ROOM)))
        }
    }

    suspend fun updateNoteList(note: NoteModel): Flow<Result<Boolean>> =
        flow {
            val result = proxy.getObject().dao().updateWithTimestamp(note) >= 1
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_UPDATE_NOTE_ROOM)))
        }

    suspend fun insertNote(note: NoteModel): Flow<Result<Boolean>> =
        flow {
            val result = proxy.getObject().dao().insertWithTimestamp(note) != -1L
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_INSERT_NOTE_ROOM)))
        }
}