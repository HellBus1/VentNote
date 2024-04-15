package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.commons.ErrorMessage
import com.digiventure.ventnote.config.NoteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile

class GoogleDriveRepository @Inject constructor(
    private val service: GoogleDriveService,
    private val database: NoteDatabase
) {
    suspend fun uploadDatabaseFile(databaseFile: File, fileName: String): Flow<Result<Boolean>> =
        flow {
            database.close()
            service.uploadDatabaseFile(databaseFile, fileName)
            emit(Result.success(true))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_UPLOAD_DATABASE)))
        }

    suspend fun restoreDatabaseFile(databaseFile: File, fileId: String): Flow<Result<Boolean>> =
        flow {
            database.close()
            service.readFile(databaseFile, fileId)
            emit(Result.success(true))
        }.catch {
            emit(Result.failure(RuntimeException(ErrorMessage.FAILED_UPLOAD_DATABASE)))
        }

    suspend fun getBackupFileList(): Flow<Result<List<DriveFile>>> =
        flow {
            val result = service.queryFiles()?.files?.toList() ?: emptyList()
            emit(Result.success(result))
        }.catch {
            emit(Result.failure<List<com.google.api.services.drive.model.File>>(RuntimeException(ErrorMessage.FAILED_GET_LIST_BACKUP_FILE)))
        }
}