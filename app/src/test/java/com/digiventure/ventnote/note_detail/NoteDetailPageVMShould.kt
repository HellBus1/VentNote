package com.digiventure.ventnote.note_detail

import com.digiventure.utils.BaseUnitTest
import com.digiventure.utils.captureValues
import com.digiventure.utils.getValueForTest
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.feature.note_detail.viewmodel.NoteDetailPageVM
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteDetailPageVMShould: BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val note = Mockito.mock<NoteModel>()
    private val id = 1

    private val expected = Result.success(note)
    private val exception = RuntimeException("Failed to get note detail")

    private val expectedDeletion = Result.success(true)
    private val exceptionDeletion = RuntimeException("Failed to delete list of notes")

    private val expectedUpdating = Result.success(true)
    private val exceptionUpdating = RuntimeException("Failed to delete list of notes")

    private lateinit var viewModel: NoteDetailPageVM

    @Before
    fun setup() {
        viewModel = NoteDetailPageVM(repository)
    }

    /**
     * Test suite for get detail from repository
     * */
    @Test
    fun getNoteDetailFromRepository() = runTest {
        mockSuccessfulGetNoteListCase()
        viewModel.getNoteDetail(id)

        viewModel.noteDetail.getValueForTest()

        verify(repository, times(1)).getNoteDetail(id)
    }

    @Test
    fun emitsNoteDetailFromRepository() = runTest {
        mockSuccessfulGetNoteListCase()
        viewModel.getNoteDetail(id)

        assertEquals(expected, viewModel.noteDetail.getValueForTest())
    }

    @Test
    fun emitsErrorWhenGetNoteDetailReceiveError() = runTest {
        mockErrorGetNoteListCase()
        viewModel.getNoteDetail(id)

        assertEquals(Result.failure<NoteModel>(exception), viewModel.noteDetail.getValueForTest())
    }

    @Test
    fun showLoaderWhileLoadingNoteDetail() = runTest {
        mockSuccessfulGetNoteListCase()

        viewModel.loader.captureValues {
            viewModel.getNoteDetail(id)

            assertEquals(true, values.first())
        }
    }

    @Test
    fun closeLoaderAfterNoteDetailLoaded() = runTest {
        mockSuccessfulGetNoteListCase()

        viewModel.loader.captureValues {
            viewModel.getNoteDetail(id)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterGetNoteDetailError() = runTest {
        mockErrorGetNoteListCase()

        viewModel.loader.captureValues {
            viewModel.getNoteDetail(id)

            assertEquals(false, values.last())
        }
    }

    private fun mockSuccessfulGetNoteListCase() {
        runBlocking {
            whenever(repository.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.success(note))
                }
            )
        }
    }

    private fun mockErrorGetNoteListCase() {
        runBlocking {
            whenever(repository.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Test suite for update note from repository
     * */
    @Test
    fun updateNoteFromRepository() = runTest {
        mockSuccessfulUpdateCase()

        viewModel.updateNote(note)

        verify(repository, times(1)).updateNoteList(note)
    }

    @Test
    fun emitsBooleanOfUpdatingLengthFromRepository() = runTest {
        mockSuccessfulUpdateCase()

        val result = viewModel.updateNote(note)

        assertEquals(expectedUpdating, result)
    }

    @Test
    fun emitsErrorWhenUpdatingError() = runTest {
        mockErrorUpdateCase()

        val result = viewModel.updateNote(note)

        assertEquals(Result.failure<Boolean>(exceptionUpdating), result)
    }

    @Test
    fun showLoaderWhileUpdateNote() = runTest {
        mockSuccessfulUpdateCase()

        viewModel.loader.captureValues {
            viewModel.updateNote(note)

            assertEquals(true, values.first())
        }
    }

    @Test
    fun closeLoaderAfterUpdateNoteSuccess() = runTest {
        mockSuccessfulUpdateCase()

        viewModel.loader.captureValues {
            viewModel.updateNote(note)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterUpdateNoteError() = runTest {
        mockErrorUpdateCase()

        viewModel.loader.captureValues {
            viewModel.updateNote(note)

            assertEquals(false, values.last())
        }
    }

    private fun mockSuccessfulUpdateCase() {
        runBlocking {
            whenever(repository.updateNoteList(note)).thenReturn(
                flowOf(expectedUpdating)
            )
        }
    }

    private fun mockErrorUpdateCase() {
        runBlocking {
            whenever(repository.updateNoteList(note)).thenReturn(
                flowOf(Result.failure(exceptionUpdating))
            )
        }
    }

    /**
     * Test suite for delete note from repository
     * */
    @Test
    fun deleteNoteListFromRepository() = runTest {
        mockSuccessfulDeletionCase()

        viewModel.deleteNoteList(note)

        verify(repository, times(1)).deleteNoteList(note)
    }

    @Test
    fun emitsBooleanOfDeletionLengthFromRepository() = runTest {
        mockSuccessfulDeletionCase()

        val result = viewModel.deleteNoteList(note)

        assertEquals(expectedDeletion, result)
    }

    @Test
    fun emitsErrorWhenDeletionError() = runTest {
        mockErrorDeletionCase()

        val result = viewModel.deleteNoteList(note)

        assertEquals(Result.failure<Boolean>(exceptionDeletion), result)
    }

    @Test
    fun showLoaderWhileDeletingNote() = runTest {
        mockSuccessfulDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(true, values.first())
        }
    }

    @Test
    fun closeLoaderAfterDeleteNoteSuccess() = runTest {
        mockSuccessfulDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterDeleteNoteError() = runTest {
        mockErrorDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(false, values.last())
        }
    }

    private fun mockSuccessfulDeletionCase() {
        runBlocking {
            whenever(repository.deleteNoteList(note)).thenReturn(
                flowOf(expectedDeletion)
            )
        }
    }

    private fun mockErrorDeletionCase() {
        runBlocking {
            whenever(repository.deleteNoteList(note)).thenReturn(
                flowOf(Result.failure(exceptionDeletion))
            )
        }
    }
}