package com.digiventure.ventnote

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteRepositoryShould: BaseUnitTest() {
    private val service: NoteLocalService = Mockito.mock()
    private val noteList = Mockito.mock<List<NoteModel>>()
    private val note = Mockito.mock<NoteModel>()

    private val id = 1

    private val exception = RuntimeException("Failed to get list of notes")
    private val deletionException = RuntimeException("Failed to delete list of notes")
    private val noteDetailException = RuntimeException("Failed to get note detail")

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

        Assert.assertEquals(Result.success(note), repository.getNoteDetail(id).first())
    }

    @Test
    fun propagateError() = runTest {
        mockErrorGetNoteCase()

        repository.getNoteDetail(id).first()

        Assert.assertEquals(
            Result.failure<NoteModel>(noteDetailException),
            repository.getNoteDetail(id).first()
        )
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

    private fun mockErrorGetNoteCase() {
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
        repository.getNoteList()

        Mockito.verify(service, Mockito.times(1)).getNoteList()
    }

    @Test
    fun emitsFlowOfNoteListFromService() = runTest {
        mockSuccessfulGetNoteListCase()

        Assert.assertEquals(noteList, repository.getNoteList().first().getOrNull())
    }

    @Test
    fun propagateWhenGetNoteListError() = runTest {
        mockFailureGetNoteListCase()

        Assert.assertEquals(exception, repository.getNoteList().first().exceptionOrNull())
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
        repository.deleteNoteList(note)

        Mockito.verify(service, Mockito.times(1)).deleteNoteList(note)
    }

    @Test
    fun emitBooleanAfterDeleteFromService() = runTest {
        mockSuccessfulDeletionCase()

        Assert.assertEquals(true, repository.deleteNoteList(note).first().getOrNull())
    }

    @Test
    fun propagateErrorWhenDeletionError() = runTest {
        mockFailureDeletionCase()

        Assert.assertEquals(deletionException, repository.deleteNoteList(note).first().exceptionOrNull())
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
}