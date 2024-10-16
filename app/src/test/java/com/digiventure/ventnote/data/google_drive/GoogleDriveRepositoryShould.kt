package com.digiventure.ventnote.data.google_drive

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.persistence.NoteModel
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GoogleDriveRepositoryShould: BaseUnitTest() {
    private val service: GoogleDriveService = mock()
    private val noteList: List<NoteModel> = listOf()
    private val fileName: String = "backup.json"
    private val fileId: String = "1"
    private val drive: Drive = mock()
    private val driveFileList: FileList = FileList()
    private val exception: Exception = RuntimeException("Failed to process")

    private lateinit var repository: GoogleDriveRepository

    @Before
    fun setup() {
        repository = GoogleDriveRepository(service)
    }

    @Test
    fun emitsResultSuccess_whenUploadDatabaseIsSuccess() = runTest {
        val file = File()
        whenever(service.uploadDatabaseFile(noteList, fileName, drive)).thenReturn(
            Result.success(file)
        )

        val actualResult = repository.uploadDatabaseFile(noteList, fileName, drive).first()

        verify(service, times(1)).uploadDatabaseFile(noteList, fileName, drive)
        assertEquals(Result.success(file), actualResult)
    }

    @Test
    fun emitsResultFailure_whenUploadDatabaseIsThrowingException() = runTest {
        whenever(service.uploadDatabaseFile(noteList, fileName, drive)).thenReturn(
            Result.failure(exception)
        )

        val actualResult = repository.uploadDatabaseFile(noteList, fileName, drive).first()

        verify(service, times(1)).uploadDatabaseFile(noteList, fileName, drive)
        val expectedResultMessage = Result.failure<RuntimeException>(exception).exceptionOrNull()?.message
        val actualResultMessage = actualResult.exceptionOrNull()?.message
        assertEquals(expectedResultMessage, actualResultMessage)
    }

    @Test
    fun emitsResultSuccess_whenRestoreDatabaseIsSuccess() = runTest {
        whenever(service.readFile(fileId, drive)).thenReturn(
            Result.success(Unit)
        )

        val actualResult = repository.restoreDatabaseFile(fileId, drive).first()

        verify(service, times(1)).readFile(fileId, drive)
        assertEquals(Result.success(Unit), actualResult)
    }

    @Test
    fun emitsResultFailure_whenRestoreDatabaseIsThrowingException() = runTest {
        whenever(service.readFile(fileId, drive)).thenReturn(
            Result.failure(exception)
        )

        val actualResult = repository.restoreDatabaseFile(fileId, drive).first()

        verify(service, times(1)).readFile(fileId, drive)
        val expectedResultMessage = Result.failure<RuntimeException>(exception).exceptionOrNull()?.message
        val actualResultMessage = actualResult.exceptionOrNull()?.message
        assertEquals(expectedResultMessage, actualResultMessage)
    }

    @Test
    fun emitsResultSuccessWithThreeListOfBackupFile_whenGetBackupFileListIsSuccess() = runTest {
        driveFileList.setFiles(listOf(File(), File(), File()))
        whenever(service.queryFiles(drive)).thenReturn(
            Result.success(driveFileList)
        )

        val actualResult = repository.getBackupFileList(drive).first()

        verify(service, times(1)).queryFiles(drive)
        assertEquals(3, actualResult.getOrNull()?.size)
    }

    @Test
    fun emitsResultFailure_whenGetBackupFileListIsThrowingException() = runTest {
        whenever(service.queryFiles(drive)).thenReturn(
            Result.failure(exception)
        )

        val actualResult = repository.getBackupFileList(drive).first()

        verify(service, times(1)).queryFiles(drive)
        val expectedResultMessage = Result.failure<RuntimeException>(exception).exceptionOrNull()?.message
        val actualResultMessage = actualResult.exceptionOrNull()?.message
        assertEquals(expectedResultMessage, actualResultMessage)
    }

    @Test
    fun emitsResultSuccess_whenDeleteFileIsSuccess() = runTest {
        whenever(service.deleteFile(fileId, drive)).thenReturn(
            Result.success(null)
        )

        val actualResult = repository.deleteFile(fileId, drive).first()

        verify(service, times(1)).deleteFile(fileId, drive)
        assertEquals(Result.success(null), actualResult)
    }

    @Test
    fun emitsResultFailure_whenDeleteFileIsThrowingException() = runTest {
        whenever(service.deleteFile(fileId, drive)).thenReturn(
            Result.failure(exception)
        )

        val actualResult = repository.deleteFile(fileId, drive).first()

        verify(service, times(1)).deleteFile(fileId, drive)
        val expectedResultMessage = Result.failure<RuntimeException>(exception).exceptionOrNull()?.message
        val actualResultMessage = actualResult.exceptionOrNull()?.message
        assertEquals(expectedResultMessage, actualResultMessage)
    }
}