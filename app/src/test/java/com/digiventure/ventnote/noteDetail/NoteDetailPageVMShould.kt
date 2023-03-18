package com.digiventure.ventnote.noteDetail

import com.digiventure.utils.BaseUnitTest
import com.digiventure.utils.getValueForTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.noteDetail.viewmodel.NoteDetailPageVM
import kotlinx.coroutines.flow.flow
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

    private lateinit var viewModel: NoteDetailPageVM

    @Before
    fun setup() {
        viewModel = NoteDetailPageVM(repository)
    }

    @Test
    fun getNoteDetailFromRepository() = runTest {
        mockSuccessfulCase()
        viewModel.getNoteDetail(id)

        viewModel.noteDetail.getValueForTest()

        verify(repository, times(1)).getNoteDetail(id)
    }

    @Test
    fun emitsNoteDetailFromRepository() = runTest {
        mockSuccessfulCase()
        viewModel.getNoteDetail(id)

        assertEquals(expected, viewModel.noteDetail.getValueForTest())
    }

    @Test
    fun emitsErrorWhenReceiveError() = runTest {
        mockErrorCase()
        viewModel.getNoteDetail(id)

        assertEquals(Result.failure<NoteModel>(exception), viewModel.noteDetail.getValueForTest())
    }

    private fun mockSuccessfulCase() {
        runBlocking {
            whenever(repository.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.success(note))
                }
            )
        }
    }

    private fun mockErrorCase() {
        runBlocking {
            whenever(repository.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }
}