package com.digiventure.ventnote

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.google_api.FileInfo
import com.digiventure.ventnote.data.google_api.GoogleAPIHelperLayer
import com.digiventure.ventnote.data.google_api.GoogleAPIService
import com.digiventure.ventnote.data.google_api.GoogleApiHelper
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import java.io.File
import com.google.api.services.drive.model.File as DriveFile

class GoogleAPIServiceShould: BaseUnitTest() {
    private var googleApiHelper: GoogleApiHelper = mock()
    private var googleApiHelperLayer: GoogleAPIHelperLayer = mock()
    private var googleAccountCredential: GoogleAccountCredential = mock()

    private val fileInfo1 = FileInfo(mock(File::class.java), "note_database", "database")
    private val fileInfo2 = FileInfo(mock(File::class.java), "note_database-shm", "database-shm")
    private val fileInfo3 = FileInfo(mock(File::class.java), "note_database-wal", "database-wal")
    private val fileList = listOf(fileInfo1, fileInfo2, fileInfo3)

    private val files: FileList = mock()

    private val file1: DriveFile = mock()
    private val file2: DriveFile = mock()
    private val file3: DriveFile = mock()

    private val dummyFiles = listOf(file1, file2, file3)

    private val backupFolderId = "VentNoteDataBackup"

    private val exception1 = Exception("Failed to instantiate GoogleApiHelper")
    private val exception2 = Exception("API Helper error")

    private lateinit var service: GoogleAPIService

    @Before
    fun setup() {
        service = GoogleAPIService(fileList, googleApiHelperLayer)
    }

    /**
     * Test suite for sync db
     * */
    @Test
    fun verifySyncDBFromDriveIsCallNecessaryMethodFromHelper() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()

        service.syncDBFromDrive(googleAccountCredential)

        verify(googleApiHelperLayer, times(1)).getGoogleAPIHelperInstance(googleAccountCredential)
        verify(googleApiHelper, times(1)).getOrCreateBackupFolderId(backupFolderId)
        verify(googleApiHelper, times(1)).getListOfFileInsideAFolder(backupFolderId)
    }

    @Test
    fun verifySyncDBFromDriveDownloadFileWhenDriveFilesExist() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()

        service.syncDBFromDrive(googleAccountCredential)

        // Delete local file if remote file exist
        fileList.forEach { fileInfo ->
            if (fileInfo.file.exists()) {
                verify(fileInfo.file).delete()
            }
        }

        // Download remote file if exist
        dummyFiles.forEach { file ->
            if (fileList.any { it.name == file.name }) {
                verify(googleApiHelper).downloadFile(file.id, any())
            }
        }
    }

    @Test
    fun throwErrorWheneverPartOfSyncDBProcessFailed() = runTest {
        whenever(googleApiHelperLayer.getGoogleAPIHelperInstance(googleAccountCredential)).thenAnswer { throw exception1 }

        try {
            service.syncDBFromDrive(googleAccountCredential)
        } catch (e: Exception) {
            assertEquals(exception1.message, e.message)
        }
    }

    @Test
    fun assertSyncDBFromDriveNotDownloadFileWhenDriveFilesNotExist() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()

        val databaseFiles = emptyList<FileInfo>()

        databaseFiles.forEach { fileInfo ->
            whenever(fileInfo.file.exists()).thenReturn(false)
        }

        service.syncDBFromDrive(googleAccountCredential)

        // Assert to not delete local file if remote file is not exist
        databaseFiles.forEach { fileInfo ->
            assertFalse(fileInfo.file.exists())
            verify(fileInfo.file, never()).delete()
        }

        // Assert to not download remote file if not exist
        dummyFiles.forEach { file ->
            if (databaseFiles.any { it.name == file.name }) {
                verify(googleApiHelper).downloadFile(file.id, any())
            } else {
                verify(googleApiHelper, never()).downloadFile(any(), any())
            }
        }
    }

    /**
     * Test suite for upload db
     * */
    @Test
    fun verifyUploadDBToDriveIsCallNecessaryMethodFromHelper() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()

        service.uploadDBtoDrive(googleAccountCredential)

        verify(googleApiHelperLayer, times(1)).getGoogleAPIHelperInstance(googleAccountCredential)
        verify(googleApiHelper, times(1)).getOrCreateBackupFolderId(backupFolderId)
    }

    @Test
    fun verifyUpdateOrUploadDBToDriveWhenLocalFileExist() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()
        stubSuccessfulUploadDBCase()

        whenever(fileInfo1.file.exists()).thenReturn(true)

        service = GoogleAPIService(listOf(fileInfo1), googleApiHelperLayer)

        // This condition met when file from drive is exist and local file is exist
        whenever(googleApiHelper.getExistingFileInDrive(eq(backupFolderId), anyString())).thenReturn(file1)
        service.uploadDBtoDrive(googleAccountCredential)
        verify(googleApiHelper, times(1)).getExistingFileInDrive(backupFolderId, fileInfo1.name)
        verify(googleApiHelper, times(1)).updateDatabaseFileInDrive(file1, fileInfo1.file)


        // This condition met when file from drive is not exist and local file is exist
        whenever(googleApiHelper.getExistingFileInDrive(eq(backupFolderId), anyString())).thenReturn(null)
        service.uploadDBtoDrive(googleAccountCredential)
        verify(googleApiHelper, times(2)).getExistingFileInDrive(backupFolderId, fileInfo1.name)
        verify(googleApiHelper, times(1)).uploadDatabaseFileToDrive(backupFolderId, fileInfo1.file, fileInfo1.name)
    }

    @Test
    fun verifyNoUpdateOrUploadDBToDriveWhenLocalFileNotExist() = runTest {
        stubDriveFileCreation()
        stubSuccessfulDBCase()

        service = GoogleAPIService(listOf(fileInfo1), googleApiHelperLayer)

        whenever(fileInfo1.file.exists()).thenReturn(false)

        whenever(googleApiHelper.getExistingFileInDrive(eq(backupFolderId), anyString())).thenReturn(file1)

        service.uploadDBtoDrive(googleAccountCredential)

        // This condition met when local file is not exist
        verify(googleApiHelper, times(1)).getExistingFileInDrive(backupFolderId, fileInfo1.name)
        verify(googleApiHelper, never()).updateDatabaseFileInDrive(file1, fileInfo1.file)
        verify(googleApiHelper, never()).uploadDatabaseFileToDrive(backupFolderId, fileInfo1.file, fileInfo1.name)
    }

    @Test
    fun throwErrorWheneverPartOfUploadDBProcessFailed() = runTest {
        whenever(googleApiHelperLayer.getGoogleAPIHelperInstance(any())).thenAnswer { throw exception2 }

        try {
            service.uploadDBtoDrive(googleAccountCredential)
        } catch (e: Exception) {
            assertEquals(exception2.message, e.message)
        }
    }

    private fun stubSuccessfulDBCase() = runBlocking {
        whenever(googleApiHelperLayer.getGoogleAPIHelperInstance(googleAccountCredential)).thenReturn(googleApiHelper)
        whenever(googleApiHelper.getOrCreateBackupFolderId(any())).thenReturn(backupFolderId)
        whenever(googleApiHelper.getListOfFileInsideAFolder(backupFolderId)).thenReturn(files)
        whenever(files.files).thenReturn(dummyFiles)
    }

    private fun stubSuccessfulUploadDBCase() = runBlocking {
        whenever(googleApiHelper.updateDatabaseFileInDrive(file1, fileInfo1.file)).thenAnswer { }
        whenever(googleApiHelper.uploadDatabaseFileToDrive(eq(backupFolderId), any(), eq(fileInfo1.name))).thenReturn(file1)
    }

    private fun stubDriveFileCreation() = runBlocking {
        whenever(file1.name).thenReturn("database")
        whenever(file2.name).thenReturn("database-shm")
        whenever(file3.name).thenReturn("database-wal")
    }
}