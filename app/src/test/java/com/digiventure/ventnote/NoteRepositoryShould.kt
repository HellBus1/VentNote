package com.digiventure.ventnote

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteRepositoryShould: BaseUnitTest() {
    private val service: NoteLocalService = mock()
    private val noteList = mock<List<NoteModel>>()
    private val note = mock<NoteModel>()

    private val id = 1

    private val exception = RuntimeException("Failed to get list of notes")
    private val deletionException = RuntimeException("Failed to delete list of notes")
    private val noteDetailException = RuntimeException("Failed to get note detail")
    private val updateException = RuntimeException("Failed to update list of notes")
    private val insertException = RuntimeException("Failed to insert list of notes")

    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        repository = NoteRepository(service)
    }

    /**
     * Test suite for get noteDetail  from service
     * */
    @Test
    fun getNoteDetailFromService() = runTest {
        mockSuccessfulGetNoteCase()

        repository.getNoteDetail(id).first()

        verify(service, times(1)).getNoteDetail(id)
    }

    @Test
    fun emitsNoteDetailFromService() = runTest {
        mockSuccessfulGetNoteCase()

        assertEquals(Result.success(note), repository.getNoteDetail(id).first())
    }

    @Test
    fun propagateWhenGetNoteDetailError() = runTest {
        mockFailureGetNoteCase()

        assertEquals(noteDetailException, repository.getNoteDetail(id).first().exceptionOrNull())
    }

    private fun mockSuccessfulGetNoteCase() {
        runBlocking {
            whenever(service.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.success(note))
                }
            )
        }
    }

    private fun mockFailureGetNoteCase() {
        runBlocking {
            whenever(service.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.failure(noteDetailException))
                }
            )
        }
    }

    /**
     * Test suite for get notelist from service
     * */
    @Test
    fun getNoteListFromService() = runTest {
        mockSuccessfulGetNoteListCase()

        repository.getNoteList()

        verify(service, times(1)).getNoteList()
    }

    @Test
    fun emitsFlowOfNoteListFromService() = runTest {
        mockSuccessfulGetNoteListCase()

        assertEquals(Result.success(noteList), repository.getNoteList().first())
    }

    @Test
    fun propagateWhenGetNoteListError() = runTest {
        mockFailureGetNoteListCase()

        assertEquals(exception, repository.getNoteList().first().exceptionOrNull())
    }

    private fun mockSuccessfulGetNoteListCase() {
        runBlocking {
            whenever(service.getNoteList()).thenReturn(
                flow {
                    emit(Result.success(noteList))
                }
            )
        }
    }

    private fun mockFailureGetNoteListCase() {
        runBlocking {
            whenever(service.getNoteList()).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Test suite for delete notelist from service
     * */
    @Test
    fun deleteNoteListFromService() = runTest {
        mockSuccessfulDeletionCase()

        repository.deleteNoteList(note)

        verify(service, times(1)).deleteNoteList(note)
    }

    @Test
    fun emitBooleanAfterDeleteNoteListFromService() = runTest {
        mockSuccessfulDeletionCase()

        assertEquals(true, repository.deleteNoteList(note).first().getOrNull())
    }

    @Test
    fun propagateErrorWhenDeleteNoteListError() = runTest {
        mockFailureDeletionCase()

        assertEquals(deletionException, repository.deleteNoteList(note).first().exceptionOrNull())
    }

    private fun mockSuccessfulDeletionCase() {
        runBlocking {
            whenever(service.deleteNoteList(note)).thenReturn(
                flow {
                    emit(Result.success(true))
                }
            )
        }
    }

    private fun mockFailureDeletionCase() {
        runBlocking {
            whenever(service.deleteNoteList(note)).thenReturn(
                flow {
                    emit(Result.failure(deletionException))
                }
            )
        }
    }

    /**
     * Test suite for update notelist from service
     * */
    @Test
    fun updateNoteListFromService() = runTest {
        mockSuccessfulUpdateCase()

        repository.updateNoteList(note)

        verify(service, times(1)).updateNoteList(note)
    }

    @Test
    fun emitBooleanAfterUpdateNoteListFromService() = runTest {
        mockSuccessfulUpdateCase()

        assertEquals(true, repository.updateNoteList(note).first().getOrNull())
    }

    @Test
    fun propagateErrorWhenUpdateNoteListError() = runTest {
        mockFailureUpdateCase()

        assertEquals(updateException, repository.updateNoteList(note).first().exceptionOrNull())
    }

    private fun mockSuccessfulUpdateCase() {
        runBlocking {
            whenever(service.updateNoteList(note)).thenReturn(
                flow {
                    emit(Result.success(true))
                }
            )
        }
    }

    private fun mockFailureUpdateCase() {
        runBlocking {
            whenever(service.updateNoteList(note)).thenReturn(
                flow {
                    emit(Result.failure(updateException))
                }
            )
        }
    }

    /**
     * Test suite for insert note from service
     * */
    @Test
    fun insertNoteFromService() = runTest {
        mockSuccessfulInsertCase()

        repository.insertNote(note)

        verify(service, times(1)).insertNote(note)
    }

    @Test
    fun emitBooleanAfterInsertNoteFromService() = runTest {
        mockSuccessfulInsertCase()

        assertEquals(true, service.insertNote(note).first().getOrNull())
    }

    @Test
    fun propagateErrorWhenInsertNoteError() = runTest {
        mockFailureInsertCase()

        assertEquals(insertException, repository.insertNote(note).first().exceptionOrNull())
    }

    private fun mockSuccessfulInsertCase() {
        runBlocking {
            whenever(service.insertNote(note)).thenReturn(
                flowOf(Result.success(true))
            )
        }
    }

    private fun mockFailureInsertCase() {
        runBlocking {
            whenever(service.insertNote(note)).thenReturn(
                flowOf(Result.failure(insertException))
            )
        }
    }
}