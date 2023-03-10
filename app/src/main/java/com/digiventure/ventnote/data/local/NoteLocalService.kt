package com.digiventure.ventnote.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val dao: NoteDAO
) {
    suspend fun getNoteList(): Flow<Result<List<NoteModel>>> =
        dao.getNotes().map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException("Failed to get list of notes")))
        }

    suspend fun deleteNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        flow {
            val result = (dao.deleteNotes(*notes) == notes.size)
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException("Failed to delete list of notes")))
        }
}