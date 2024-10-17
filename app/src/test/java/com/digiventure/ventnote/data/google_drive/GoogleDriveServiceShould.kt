package com.digiventure.ventnote.data.google_drive

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GoogleDriveServiceShould: BaseUnitTest() {
    private val proxy: DatabaseProxy = mock()
    private val dao: NoteDAO = mock()
    private val noteList: List<NoteModel> = listOf()
    private val fileName: String = "backup.json"
    private val fileId: String = "1"
    private val drive: Drive = mock()

    private lateinit var service: GoogleDriveService

    @Before
    fun setup() {
        service = GoogleDriveService(proxy)
    }

    @Test
    fun returnResultSuccess_whenUploadProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val createMock = mock<Drive.Files.Create>()
        val driveFile = File()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.create(any(), any())).thenReturn(createMock)
        whenever(createMock.execute()).thenReturn(driveFile)

        val result = service.uploadDatabaseFile(noteList, fileName, drive)

        assertTrue(result.isSuccess)
        assertEquals(driveFile, result.getOrNull())
        verify(drive, times(1)).files()
        verify(filesMock, times(1)).create(any(), any())
    }

    @Test
    fun returnResultFailure_whenUploadProcessThrowsIOExceptionWhileCreateDriveFile() = runTest {
        val filesMock = mock<Drive.Files>()
        val exception = IOException()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.create(any(), any())).thenThrow(exception)

        val result = service.uploadDatabaseFile(noteList, fileName, drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun returnResultSuccess_whenReadFileProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val getMock = mock<Drive.Files.Get>()
        val inputStream = this::class.java.classLoader?.getResourceAsStream("/src/test/res/backup.json")
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotesWithTimestamp(any())).thenAnswer { }
        whenever(proxy.dao()).thenReturn(dao)

        val result = service.readFile(fileId, drive)

        assertTrue(result.isSuccess)
        verify(proxy.dao(), times(1)).upsertNotesWithTimestamp(any())
    }

    @Test
    fun returnsFailureResult_whenReadFileProcessThrowsIOExceptionWhileGetBackupFileFromDrive() = runTest {
        val filesMock = mock<Drive.Files>()
        val exception = IOException()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenThrow(exception)

        val result = service.readFile(fileId, drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun returnResultFailure_whenReadFileProcessThrowsExceptionWhileUpsertNoteListToDatabase() = runTest {
        val filesMock = mock<Drive.Files>()
        val getMock = mock<Drive.Files.Get>()
        val exception = Exception()
        val inputStream = this::class.java.classLoader?.getResourceAsStream("/src/test/res/backup.json")
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotesWithTimestamp(any())).thenAnswer {
            throw exception
        }
        whenever(proxy.dao()).thenReturn(dao)

        val result = service.readFile(fileId, drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun returnResultSuccess_whenQueryFilesProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val fileList = mock<Drive.Files.List>()
        val fileListAfterSetSpace = mock<Drive.Files.List>()
        val driveFileList = mock<FileList>()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.list()).thenReturn(fileList)
        whenever(fileList.setSpaces(any())).thenReturn(fileListAfterSetSpace)
        whenever(fileListAfterSetSpace.execute()).thenReturn(driveFileList)

        val result = service.queryFiles(drive)

        assertTrue(result.isSuccess)
        assertEquals(driveFileList, result.getOrNull())
    }

    @Test
    fun returnResultFailure_whenQueryFilesProcessThrowsIOExceptionWhileGettingDriveFiles() = runTest {
        val filesMock = mock<Drive.Files>()
        val fileList = mock<Drive.Files.List>()
        val fileListAfterSetSpace = mock<Drive.Files.List>()
        val exception = IOException()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.list()).thenReturn(fileList)
        whenever(fileList.setSpaces(any())).thenReturn(fileListAfterSetSpace)
        whenever(fileListAfterSetSpace.execute()).thenThrow(exception)

        val result = service.queryFiles(drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun returnResultSuccess_whenDeleteFileProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val fileDelete = mock<Drive.Files.Delete>()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.delete(fileId)).thenReturn(fileDelete)
        whenever(fileDelete.execute()).thenAnswer { null }

        val result = service.deleteFile(fileId, drive)

        assertTrue(result.isSuccess)
    }

    @Test
    fun returnResultFailure_whenDeleteFileProcessThrowsIOExceptionWhileDeletingDriveFile() = runTest {
        val filesMock = mock<Drive.Files>()
        val fileDelete = mock<Drive.Files.Delete>()
        val exception = IOException()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.delete(fileId)).thenReturn(fileDelete)
        whenever(fileDelete.execute()).thenAnswer {
            throw exception
        }

        val result = service.deleteFile(fileId, drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}