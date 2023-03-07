package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteLocalService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class NotesPageRepositoryShould: BaseUnitTest() {
    private val service: NoteLocalService = mock()
    private val noteList = mock<List<NoteModel>>()
    private val note = mock<NoteModel>()

    private val exception = RuntimeException("Failed to get list of notes")
    private val deletionException = RuntimeException("Failed to delete list of notes")

    @Test
    fun getNoteListFromService() = runTest {
        val repository = NoteRepository(service)

        repository.getNoteList()

        verify(service, times(1)).getNoteList()
    }

    @Test
    fun emitsMappedNoteListFromService() = runTest {
        val repository = mockSuccessfulCase()

        assertEquals(noteList, repository.getNoteList().first().getOrNull())
    }

    @Test
    fun propagateError() = runTest {
        val repository = mockFailureCase()

        assertEquals(exception, repository.getNoteList().first().exceptionOrNull())
    }

    @Test
    fun deleteNoteListFromService() = runTest {
        val repository = NoteRepository(service)

        repository.deleteNoteList(note)

        verify(service, times(1)).deleteNoteList(note)
    }

    @Test
    fun emitBooleanAfterDeleteFromService() = runTest {
        val repository = mockSuccessfulDeletionCase()

        assertEquals(true, repository.deleteNoteList(note).first().getOrNull())
    }

    @Test
    fun propagateErrorWhenDeletionError() = runTest {
        val repository = mockFailureDeletionCase()

        assertEquals(deletionException, repository.deleteNoteList(note).first().exceptionOrNull())
    }

    private fun mockSuccessfulDeletionCase(): NoteRepository {
        runBlocking {
            whenever(service.deleteNoteList(note)).thenReturn(
                flow {
                    emit(Result.success(true))
                }
            )
        }

        return NoteRepository(service)
    }

    private fun mockFailureDeletionCase(): NoteRepository {
        runBlocking {
            whenever(service.deleteNoteList(note)).thenReturn(
                flow {
                    emit(Result.failure(deletionException))
                }
            )
        }

        return NoteRepository(service)
    }

    private fun mockSuccessfulCase(): NoteRepository {
        runBlocking {
            whenever(service.getNoteList()).thenReturn(
                flow {
                    emit(Result.success(noteList))
                }
            )
        }

        return NoteRepository(service)
    }

    private fun mockFailureCase(): NoteRepository {
        runBlocking {
            whenever(service.getNoteList()).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }

        return NoteRepository(service)
    }
}