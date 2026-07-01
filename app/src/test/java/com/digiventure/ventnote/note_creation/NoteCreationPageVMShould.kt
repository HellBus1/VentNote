package com.digiventure.ventnote.note_creation

import com.digiventure.utils.BaseUnitTest
import com.digiventure.utils.captureValues
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.data.persistence.TagRepository
import com.digiventure.ventnote.feature.note_creation.viewmodel.NoteCreationPageVM
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteCreationPageVMShould: BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val tagRepository: TagRepository = mock()
    private val note = mock<NoteModel>()

    private lateinit var viewModel: NoteCreationPageVM

    private val expected = Result.success(true)
    private val expectedId = Result.success(1L)
    private val exception = RuntimeException("Failed to insert list of notes")

    @Before
    fun setup() {
        runBlocking {
            whenever(tagRepository.getAllTags()).thenReturn(flowOf(Result.success(emptyList())))
        }
        viewModel = NoteCreationPageVM(repository, tagRepository)
    }

    /**
     * Test suite for add note from repository
     * */
    @Test
    fun addNoteFromRepository() = runTest {
        mockSuccessfulAddNoteCase()

        viewModel.addNote(note)

        verify(repository, times(1)).insertNote(note, emptyList())
    }

    @Test
    fun emitsNoteIdIsNotNegativeFromRepository() = runTest {
        mockSuccessfulAddNoteCase()

        val result = viewModel.addNote(note)

        assertEquals(expected, result)
    }

    @Test
    fun emitsErrorWhenAddNoteReceiveError() = runTest {
        mockErrorAddNoteCase()

        val result = viewModel.addNote(note)

        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun showLoaderWhileAddNote() = runTest {
        mockSuccessfulAddNoteCase()

        viewModel.loader.captureValues {
            viewModel.addNote(note)

            assertEquals(true, values.first())
        }
    }

    @Test
    fun closeLoaderAfterAddNoteSuccess() = runTest {
        mockSuccessfulAddNoteCase()

        viewModel.loader.captureValues {
            viewModel.addNote(note)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterAddNoteError() = runTest {
        mockErrorAddNoteCase()

        viewModel.loader.captureValues {
            viewModel.addNote(note)

            assertEquals(false, values.last())
        }
    }

    private fun mockSuccessfulAddNoteCase() {
        runBlocking {
            whenever(repository.insertNote(note, emptyList())).thenReturn(
                flowOf(expectedId)
            )
        }
    }

    private fun mockErrorAddNoteCase() {
        runBlocking {
            whenever(repository.insertNote(note, emptyList())).thenReturn(
                flowOf(Result.failure(exception))
            )
        }
    }
}