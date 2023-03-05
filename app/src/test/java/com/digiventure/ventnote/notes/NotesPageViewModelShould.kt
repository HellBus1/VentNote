package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.utils.getValueForTest
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
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

    private lateinit var viewModel: NotesPageViewModel

    @Before
    fun setup() {
        viewModel = NotesPageViewModel(repository)
    }

    @Test
    fun getNotesFromRepository() = runTest {
        mockSuccessfulCase()

        viewModel.noteList.getValueForTest()

        verify(repository, times(1)).getNoteList()
    }

    @Test
    fun emitsNotesFromRepository() = runTest {
        mockSuccessfulCase()

        assertEquals(expected, viewModel.noteList.getValueForTest())
    }

    @Test
    fun emitsErrorWhenReceiveError() = runTest {
        mockErrorCase()

        assertEquals(exception, viewModel.noteList.getValueForTest()?.exceptionOrNull())
    }

    @Test
    fun isSearchingIsSameAsInput() {
        viewModel.isSearching.value = true
        assertEquals(true, viewModel.isSearching.value)
    }

    @Test
    fun searchedTitleTextIsSameAsInput() {
        val inputTitle = "Test Title"
        viewModel.searchedTitleText.value = inputTitle
        assertEquals(inputTitle, viewModel.searchedTitleText.value)
    }

    private fun mockSuccessfulCase() {
        runBlocking {
            whenever(repository.getNoteList()).thenReturn(
                flow {
                    emit(expected)
                }
            )
        }
    }

    private fun mockErrorCase() {
        runBlocking {
            whenever(repository.getNoteList()).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }
}