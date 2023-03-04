package com.digiventure.ventnote.data.local

import com.digiventure.ventnote.data.NoteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val dao: NoteDAO
) {
    suspend fun getNoteList(): Flow<Result<List<NoteModel>>> =
        flow {
            emit(Result.success(dao.getNotes()))
        }.catch {
            emit(Result.failure(RuntimeException("Failed to get list of notes")))
        }
}