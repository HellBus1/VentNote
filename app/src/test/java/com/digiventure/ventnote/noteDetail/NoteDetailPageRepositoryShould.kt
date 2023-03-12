package com.digiventure.ventnote.noteDetail

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteDetailPageRepositoryShould: BaseUnitTest() {
    private val service: NoteLocalService = mock()
    private val note = mock<NoteModel>()

    private val id = 1

    private val expected = Result.success(note)
    private val exception = RuntimeException("Failed to get note detail")

    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        repository = NoteRepository(service)
    }

    @Test
    fun getNoteDetailFromService() = runTest {
        mockSuccessfulCase()

        repository.getNoteDetail(id).first()

        verify(service, times(1)).getNoteDetail(id)
    }

    @Test
    fun emitsNoteDetailFromService() = runTest {
        mockSuccessfulCase()

        assertEquals(expected, repository.getNoteDetail(id).first())
    }

    @Test
    fun propagateError() = runTest {
        mockErrorCase()

        repository.getNoteDetail(id).first()

        assertEquals(Result.failure<NoteModel>(exception), repository.getNoteDetail(id).first())
    }

    private fun mockSuccessfulCase() {
        runBlocking {
            whenever(service.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.success(note))
                }
            )
        }
    }

    private fun mockErrorCase() {
        runBlocking {
            whenever(service.getNoteDetail(id)).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }
}