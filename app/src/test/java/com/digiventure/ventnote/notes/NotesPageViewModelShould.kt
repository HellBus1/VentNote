package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.utils.getValueForTest
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageViewModel
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

class NotesPageViewModelShould: BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val notes = mock<List<NoteModel>>()
    private val note = mock<NoteModel>()

    private val expected = Result.success(notes)
    private val exception = RuntimeException("Failed to get list of notes")

    private val expectedDeletion = Result.success(true)
    private val exceptionDeletion = RuntimeException("Failed to delete list of notes")

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
    fun verifyIsSearchingIsSameAsInput() {
        viewModel.isSearching.value = true
        assertEquals(true, viewModel.isSearching.value)

        viewModel.isSearching.value = false
        assertEquals(false, viewModel.isSearching.value)
    }

    @Test
    fun verifySearchedTitleTextIsSameAsInput() {
        val inputTitle = "Test Title"
        viewModel.searchedTitleText.value = inputTitle
        assertEquals(inputTitle, viewModel.searchedTitleText.value)
    }

    @Test
    fun verifyIsMarkingIsSameAsInput() {
        viewModel.isMarking.value = true
        assertEquals(true, viewModel.isMarking.value)

        viewModel.isMarking.value = false
        assertEquals(false, viewModel.isMarking.value)
    }

    @Test
    fun verifyMarkedNoteListCanBeAddedOrRemoved() {
        viewModel.markedNoteList.add(note)
        assertEquals(1, viewModel.markedNoteList.size)

        viewModel.markedNoteList.remove(note)
        assertEquals(0, viewModel.markedNoteList.size)
    }

    @Test
    fun deleteNoteListFromRepository() = runTest {
        mockSuccessfulDeletionCase()

        viewModel.deleteNoteList(note)

        verify(repository, times(1)).deleteNoteList(note)
    }

    @Test
    fun emitsBooleanFromRepository() {
        // TODO (implement boolean checking flow)
    }

    @Test
    fun emitsErrorWhenDeletionError() {
        // TODO (implement error checking flow)
    }

    private fun mockSuccessfulDeletionCase() {
        runBlocking {
            whenever(repository.deleteNoteList(note)).thenReturn(
                flowOf(expectedDeletion)
            )
        }
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