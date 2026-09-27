package com.digiventure.ventnote.data.google_drive

import android.app.Application
import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.google_drive.BackupPayload
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagDAO
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.feature.widget.WidgetRefresher
import com.google.gson.Gson
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
    private val app: Application = mock()
    private val proxy: DatabaseProxy = mock()
    private val dao: NoteDAO = mock()
    private val tagDao: TagDAO = mock()
    private val refresher: WidgetRefresher = mock()
    private val payload: BackupPayload = BackupPayload(notes = listOf(NoteModel(1, "title", "note")))
    private val fileName: String = "backup.json"
    private val fileId: String = "1"
    private val drive: Drive = mock()

    private lateinit var service: GoogleDriveService

    @Before
    fun setup() {
        service = GoogleDriveService(app, proxy, refresher)
    }

    @Test
    fun returnResultSuccess_whenUploadProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val createMock = mock<Drive.Files.Create>()
        val driveFile = File()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.create(any(), any())).thenReturn(createMock)
        whenever(createMock.execute()).thenReturn(driveFile)

        val result = service.uploadDatabaseFile(payload, fileName, drive)

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

        val result = service.uploadDatabaseFile(payload, fileName, drive)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun returnResultSuccess_whenReadFileProcessIsSuccess() = runTest {
        val filesMock = mock<Drive.Files>()
        val getMock = mock<Drive.Files.Get>()
        val inputStream = "[]".byteInputStream()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotes(any())).thenAnswer { }
        whenever(proxy.dao()).thenReturn(dao)
        val result = service.readFile(fileId, drive)

        if (result.isFailure) {
            println("Exception was: ${result.exceptionOrNull()}")
            result.exceptionOrNull()?.printStackTrace()
        }
        assertTrue(result.isSuccess)
        verify(proxy.dao(), times(1)).upsertNotes(any())
        verify(refresher, times(1)).refresh(app)
    }

    @Test
    fun returnResultSuccess_whenReadFileWithPayloadFormatRestoresNotesAndTagsAndCrossRefs() = runTest {
        val filesMock = mock<Drive.Files>()
        val getMock = mock<Drive.Files.Get>()
        val testPayload = BackupPayload(
            version = 1,
            notes = listOf(NoteModel(1, "title", "note")),
            tags = listOf(TagModel(1, "Personal", "#FF0000")),
            noteTags = listOf(NoteTagCrossRef(1, 1))
        )
        val jsonString = Gson().toJson(testPayload)
        val inputStream = jsonString.byteInputStream()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotes(any())).thenAnswer { }
        whenever(dao.getSyncNotes()).thenReturn(emptyList())
        whenever(tagDao.getAllTagsSync()).thenReturn(emptyList())
        whenever(tagDao.upsertTags(any())).thenAnswer { }
        whenever(tagDao.upsertNoteTagCrossRefs(any())).thenAnswer { }
        whenever(proxy.dao()).thenReturn(dao)
        whenever(proxy.tagDao()).thenReturn(tagDao)

        val result = service.readFile(fileId, drive)

        assertTrue(result.isSuccess)
        verify(proxy.dao(), times(1)).upsertNotes(any())
        verify(proxy.tagDao(), times(1)).upsertTags(any())
        verify(proxy.tagDao(), times(1)).upsertNoteTagCrossRefs(any())
        verify(refresher, times(1)).refresh(app)
    }

    @Test
    fun returnResultSuccess_whenReadFileWithPartialPayloadFormatMissingTags() = runTest {
        val filesMock = mock<Drive.Files>()
        val getMock = mock<Drive.Files.Get>()
        val jsonString = "{\"version\":1,\"notes\":[{\"id\":1,\"title\":\"title\",\"note\":\"note\"}]}"
        val inputStream = jsonString.byteInputStream()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotes(any())).thenAnswer { }
        whenever(proxy.dao()).thenReturn(dao)

        val result = service.readFile(fileId, drive)

        assertTrue(result.isSuccess)
        verify(proxy.dao(), times(1)).upsertNotes(any())
        verify(refresher, times(1)).refresh(app)
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
        val inputStream = "[]".byteInputStream()
        whenever(drive.files()).thenReturn(filesMock)
        whenever(filesMock.get(fileId)).thenReturn(getMock)
        whenever(getMock.executeMediaAsInputStream()).thenReturn(inputStream)
        whenever(dao.upsertNotes(any())).thenAnswer {
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