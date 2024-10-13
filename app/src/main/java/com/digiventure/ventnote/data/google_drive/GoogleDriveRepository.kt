package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.commons.ErrorMessage
import com.digiventure.ventnote.data.persistence.NoteModel
import com.google.api.services.drive.Drive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile

class GoogleDriveRepository @Inject constructor(
    private val service: GoogleDriveService,
) {
    fun uploadDatabaseFile(notes: List<NoteModel>, fileName: String, drive: Drive?): Flow<Result<Boolean>> =
        flow {
            supervisorScope {
                service.uploadDatabaseFile(notes, fileName, drive)
                emit(Result.success(true))
            }
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_UPLOAD_DATABASE_FILE)))
        }

    fun restoreDatabaseFile(fileId: String, drive: Drive?): Flow<Result<Boolean>> =
        flow {
            supervisorScope {
                service.readFile(fileId, drive)
                emit(Result.success(true))
            }
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_RESTORE_DATABASE_FILE)))
        }

    fun getBackupFileList(drive: Drive?): Flow<Result<List<DriveFile>>> =
        flow {
            val result = service.queryFiles(drive)?.files?.toList() ?: emptyList()
            emit(Result.success(result))
        }.catch {
            emit(Result.failure<List<com.google.api.services.drive.model.File>>(RuntimeException(ErrorMessage.FAILED_GET_LIST_BACKUP_FILE)))
        }

    fun deleteFile(fileId: String, drive: Drive?) : Flow<Result<String>> =
        flow {
            val result = service.deleteFile(fileId, drive)
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_DELETE_DATABASE_FILE)))
        }
}