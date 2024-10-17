package com.digiventure.ventnote.data.google_drive

import com.digiventure.ventnote.commons.ErrorMessage
import com.digiventure.ventnote.data.persistence.NoteModel
import com.google.api.services.drive.Drive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.google.api.services.drive.model.File as DriveFile

class GoogleDriveRepository @Inject constructor(
    private val service: GoogleDriveService,
) {
    fun uploadDatabaseFile(notes: List<NoteModel>, fileName: String, drive: Drive?): Flow<Result<DriveFile?>> = flow {
        val result = service.uploadDatabaseFile(notes, fileName, drive)
        emit(result)
    }.catch { e ->
        emit(Result.failure(RuntimeException(ErrorMessage.FAILED_UPLOAD_DATABASE_FILE, e)))
    }

    fun restoreDatabaseFile(fileId: String, drive: Drive?): Flow<Result<Unit>> = flow {
        val result = service.readFile(fileId, drive)
        emit(result)
    }.catch { e ->
        emit(Result.failure(RuntimeException(ErrorMessage.FAILED_RESTORE_DATABASE_FILE, e)))
    }

    fun getBackupFileList(drive: Drive?): Flow<Result<List<DriveFile>>> = flow {
        val result = service.queryFiles(drive)
        val transformedResult = result.map { fileList ->
            fileList?.files?.toList() ?: emptyList()
        }
        emit(transformedResult)
    }.catch { e ->
        emit(Result.failure(RuntimeException(ErrorMessage.FAILED_GET_LIST_BACKUP_FILE, e)))
    }

    fun deleteFile(fileId: String, drive: Drive?): Flow<Result<Void?>> = flow {
        val result = service.deleteFile(fileId, drive)
        emit(result)
    }.catch { e ->
        emit(Result.failure(RuntimeException(ErrorMessage.FAILED_DELETE_DATABASE_FILE, e)))
    }
}