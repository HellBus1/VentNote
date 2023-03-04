package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.utils.getValueForTest
import com.digiventure.ventnote.data.NoteModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotesPageViewModelShould: BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val notes = mock<List<NoteModel>>()

    private val expected = Result.success(notes)
    private val exception = RuntimeException("Failed to get list of notes")

    @Test
    fun getNotesFromRepository() = runTest {
        val viewModel = mockSuccessfulCase()

        viewModel.noteList.getValueForTest()

        verify(repository, times(1)).getNoteList()
    }

    @Test
    fun emitsNotesFromRepository() = runTest {
        val viewModel = mockSuccessfulCase()

        assertEquals(expected, viewModel.noteList.getValueForTest())
    }

    @Test
    fun emitsErrorWhenReceiveError() = runTest {
        val viewModel = mockErrorCase()

        assertEquals(exception, viewModel.noteList.getValueForTest()?.exceptionOrNull())
    }

    private fun mockSuccessfulCase(): NotesPageViewModel {
        runBlocking {
            whenever(repository.getNoteList()).thenReturn(
                flow {
                    emit(expected)
                }
            )
        }

        return NotesPageViewModel(repository)
    }

    private fun mockErrorCase(): NotesPageViewModel {
        runBlocking {
            whenever(repository.getNoteList()).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }

        return NotesPageViewModel(repository)
    }
}