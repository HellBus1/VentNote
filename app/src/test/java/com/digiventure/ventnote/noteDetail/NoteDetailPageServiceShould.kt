package com.digiventure.ventnote.noteDetail

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteLocalService
import com.digiventure.ventnote.data.local.NoteModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteDetailPageServiceShould: BaseUnitTest() {
    private val dao: NoteDAO = mock()
    private val note = mock<NoteModel>()

    private val id = 1

    private val exception = RuntimeException("Failed to get note detail")

    private lateinit var service: NoteLocalService

    @Before
    fun setup() {
        service = NoteLocalService(dao)
    }

    @Test
    fun getNoteDetailFromDAO() = runTest {
        mockSuccessfulCase()

        service.getNoteDetail(id).first()

        verify(dao, times(1)).getNoteDetail(id)
    }

    @Test
    fun adjustFlowResultAndEmitsThem() = runTest {
        mockSuccessfulCase()

        assertEquals(note, dao.getNoteDetail(id).first())
    }

    @Test
    fun emitsErrorResultWhenFails() = runTest {
        mockErrorCase()

        try {
            dao.getNoteDetail(id).first()
        } catch (e: RuntimeException) {
            assertEquals("Failed to get note detail", e.message)
        }
    }

    private fun mockSuccessfulCase() {
        runBlocking {
            whenever(dao.getNoteDetail(id)).thenReturn(
                flow {
                    emit(note)
                }
            )
        }
    }

    private fun mockErrorCase() {
        runBlocking {
            whenever(dao.getNoteDetail(id)).thenThrow(exception)
        }
    }
}