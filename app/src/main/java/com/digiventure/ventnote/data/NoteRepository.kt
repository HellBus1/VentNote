package com.digiventure.ventnote.data

import com.digiventure.ventnote.data.google_api.GoogleAPIService
import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val service: NoteLocalService,
    private val googleAPIService: GoogleAPIService
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

    suspend fun updateNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        service.updateNoteList(*notes).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    suspend fun insertNote(note: NoteModel): Flow<Result<Boolean>> =
        service.insertNote(note).map {
            if (it.isSuccess) {
                Result.success(it.getOrNull() ?: false)
            } else {
                Result.failure(it.exceptionOrNull()!!)
            }
        }

    suspend fun uploadDBtoDrive(credential: GoogleAccountCredential) {
        googleAPIService.uploadDBtoDrive(credential)
    }
}