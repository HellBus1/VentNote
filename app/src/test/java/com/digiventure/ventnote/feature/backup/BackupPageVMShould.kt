package com.digiventure.ventnote.feature.backup

import android.app.Application
import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.google_drive.GoogleDriveRepository
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.data.persistence.TagDAO
import com.digiventure.ventnote.data.persistence.TagRepository
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM.FileBackupState
import com.digiventure.ventnote.feature.backup.viewmodel.BackupPageVM.FileRestoreState
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BackupPageVMShould : BaseUnitTest() {
    private val app: Application = mock()
    private val repository: GoogleDriveRepository = mock()
    private val databaseRepository: NoteRepository = mock()
    private val tagRepository: TagRepository = mock()
    private val databaseProxy: DatabaseProxy = mock()
    private val tagDao: TagDAO = mock()
    private val noteDao: NoteDAO = mock()

    private val mockDrive: com.google.api.services.drive.Drive = mock()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BackupPageVM

    private class TestBackupPageVM(
        app: Application,
        repository: GoogleDriveRepository,
        databaseRepository: NoteRepository,
        tagRepository: TagRepository,
        databaseProxy: DatabaseProxy,
        val drive: com.google.api.services.drive.Drive?
    ) : BackupPageVM(app, repository, databaseRepository, tagRepository, databaseProxy) {
        override fun getDriveInstance(): com.google.api.services.drive.Drive? = drive
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TestBackupPageVM(
            app = app,
            repository = repository,
            databaseRepository = databaseRepository,
            tagRepository = tagRepository,
            databaseProxy = databaseProxy,
            drive = mockDrive
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun restoreDatabase_setsSyncFinished_whenRestoreIsSuccessful() = runTest(testDispatcher) {
        val fileId = "test-file-id"
        whenever(repository.restoreDatabaseFile(any(), anyOrNull()))
            .thenReturn(flowOf(Result.success(Unit)))

        viewModel.restoreDatabase(fileId)
        advanceUntilIdle()

        assertEquals(FileRestoreState.SyncFinished, viewModel.uiState.value.fileRestoreState)
    }

    @Test
    fun restoreDatabase_setsSyncFailed_whenRestoreReturnsFailure() = runTest(testDispatcher) {
        val fileId = "test-file-id"
        val errorMessage = "Constraint violation during restore"
        whenever(repository.restoreDatabaseFile(any(), anyOrNull()))
            .thenReturn(flowOf(Result.failure(Exception(errorMessage))))

        viewModel.restoreDatabase(fileId)
        advanceUntilIdle()

        val state = viewModel.uiState.value.fileRestoreState
        assertTrue(state is FileRestoreState.SyncFailed)
        assertEquals(errorMessage, (state as FileRestoreState.SyncFailed).errorMessage)
    }

    @Test
    fun backupDatabase_setsSyncFinished_whenUploadIsSuccessful() = runTest(testDispatcher) {
        whenever(databaseProxy.tagDao()).thenReturn(tagDao)
        whenever(databaseRepository.getNoteList(Constants.UPDATED_AT, Constants.DESCENDING))
            .thenReturn(flowOf(Result.success(emptyList())))
        whenever(tagRepository.getAllTags())
            .thenReturn(flowOf(Result.success(emptyList())))
        whenever(tagDao.getAllNoteTagCrossRefs())
            .thenReturn(emptyList())
        whenever(repository.uploadDatabaseFile(any(), any(), anyOrNull()))
            .thenReturn(flowOf(Result.success(null)))
        whenever(repository.getBackupFileList(anyOrNull()))
            .thenReturn(flowOf(Result.success(emptyList())))

        viewModel.backupDatabase()
        advanceUntilIdle()

        assertEquals(FileBackupState.SyncFinished, viewModel.uiState.value.fileBackupState)
    }

    @Test
    fun backupDatabase_setsSyncFailed_whenUploadReturnsFailure() = runTest(testDispatcher) {
        val errorMessage = "Google Drive upload failed"
        whenever(databaseProxy.tagDao()).thenReturn(tagDao)
        whenever(databaseRepository.getNoteList(Constants.UPDATED_AT, Constants.DESCENDING))
            .thenReturn(flowOf(Result.success(emptyList())))
        whenever(tagRepository.getAllTags())
            .thenReturn(flowOf(Result.success(emptyList())))
        whenever(tagDao.getAllNoteTagCrossRefs())
            .thenReturn(emptyList())
        whenever(repository.uploadDatabaseFile(any(), any(), anyOrNull()))
            .thenReturn(flowOf(Result.failure(Exception(errorMessage))))

        viewModel.backupDatabase()
        advanceUntilIdle()

        val state = viewModel.uiState.value.fileBackupState
        assertTrue(state is FileBackupState.SyncFailed)
        assertEquals(errorMessage, (state as FileBackupState.SyncFailed).errorMessage)
    }
}
