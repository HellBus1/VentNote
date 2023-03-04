package com.digiventure.ventnote.notes

import com.digiventure.ventnote.common.BaseUnitTest
import com.digiventure.ventnote.data.NoteModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteLocalService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotesPageRepositoryShould: BaseUnitTest() {
    private val service: NoteLocalService = mock()
    private val noteList = mock<List<NoteModel>>()
    private val exception = RuntimeException("Failed to get list of notes")

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