package com.digiventure.ventnote.data

import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val service: NoteLocalService
) {
    suspend fun getNoteList(): Flow<Result<List<NoteModel>>> =
        service.getNoteList().map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: listOf())
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    suspend fun deleteNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        service.deleteNoteList(*notes).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    suspend fun getNoteDetail(id: Int): Flow<Result<NoteModel>> =
        service.getNoteDetail(id).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: NoteModel(1, "", ""))
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }
}