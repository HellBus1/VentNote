package com.digiventure.ventnote.data.persistence

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val service: NoteLocalService
) {
    fun getNoteList(sortBy: String, order: String): Flow<Result<List<NoteModel>>> =
        service.getNoteList(sortBy, order).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: listOf())
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    fun deleteNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        service.deleteNoteList(*notes).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    fun getNoteDetail(id: Int): Flow<Result<NoteModel>> =
        service.getNoteDetail(id).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: NoteModel(1, "", ""))
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    fun updateNoteList(note: NoteModel): Flow<Result<Boolean>> =
        service.updateNoteList(note).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    fun insertNote(note: NoteModel): Flow<Result<Boolean>> =
        service.insertNote(note).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }
}