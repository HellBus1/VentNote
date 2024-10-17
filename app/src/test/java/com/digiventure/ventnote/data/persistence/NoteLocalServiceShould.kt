package com.digiventure.ventnote.data.persistence

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.module.proxy.DatabaseProxy
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

class NoteLocalServiceShould: BaseUnitTest() {
    private val proxy: DatabaseProxy = mock()
    private val dao: NoteDAO = mock()
    private val noteList = mock<List<NoteModel>>()
    private val note = mock<NoteModel>()
    private val sortBy = Constants.CREATED_AT
    private val orderBy = Constants.DESCENDING

    private val id = 1

    private val detailException = RuntimeException("Failed to get note detail")
    private val listException = RuntimeException("Failed to get list of notes")
    private val deleteException = RuntimeException("Failed to delete list of notes")
    private val updateException = RuntimeException("Failed to update list of notes")
    private val insertException = RuntimeException("Failed to insert list of notes")

    private lateinit var service: NoteLocalService

    @Before
    fun setup() {
        whenever(proxy.dao()).thenReturn(dao)
        service = NoteLocalService(proxy)
    }

    /**
     * Test suite for getNoteDetail from dao
     * */
    @Test
    fun getNoteDetailFromDAO() = runTest {
        stubSuccessfulGetDetailCase()

        service.getNoteDetail(id).first()

        verify(dao, times(1)).getNoteDetail(id)
    }

    @Test
    fun emitsFlowOfNoteAndEmitsThem() = runTest {
        stubSuccessfulGetDetailCase()

        assertEquals(note, dao.getNoteDetail(id).first())
    }

    @Test
    fun emitsErrorResultWhenGetDetailsFails() = runTest {
        stubErrorGetDetailCase()

        val actualResult = service.getNoteDetail(id).first()
        val actualException = actualResult.exceptionOrNull()

        assertEquals(detailException.message, actualException?.message)
    }

    private fun stubSuccessfulGetDetailCase() {
        runBlocking {
            whenever(dao.getNoteDetail(id)).thenReturn(
                flow {
                    emit(note)
                }
            )
        }
    }

    private fun stubErrorGetDetailCase() {
        runBlocking {
            whenever(dao.getNoteDetail(id)).thenReturn(
                flow {
                    throw detailException
                }
            )
        }
    }

    /**
     * Test suite for getNoteList from dao
     * */
    @Test
    fun getNoteListFromDAO() = runTest {
        stubSuccessfulGetListNoteCase()

        service.getNoteList(sortBy, orderBy).first()

        verify(dao, times(1)).getNotes(sortBy, orderBy)
    }

    @Test
    fun emitsFlowOfNoteListAndEmitsThem() = runTest {
        stubSuccessfulGetListNoteCase()

        assertEquals(Result.success(noteList), service.getNoteList(sortBy, orderBy).first())
    }

    @Test
    fun emitsErrorResultWhenGetNoteListFails() = runTest {
        stubErrorGetListNoteCase()

        val actualResult = service.getNoteList(sortBy, orderBy).first()
        val actualException = actualResult.exceptionOrNull()

        assertEquals(listException.message, actualException?.message)
    }

    private fun stubSuccessfulGetListNoteCase() {
        runBlocking {
            whenever(dao.getNotes(sortBy, orderBy)).thenReturn(
                flow {
                    emit(noteList)
                }
            )
        }
    }

    private fun stubErrorGetListNoteCase() {
        runBlocking {
            whenever(dao.getNotes(sortBy, orderBy)).thenReturn(
                flow {
                    throw listException
                }
            )
        }
    }

    /**
     * Test suite for deleteNoteList from dao
     * */
    @Test
    fun deleteNoteListFromDAO() = runTest {
        service.deleteNoteList(note).first()

        verify(dao, times(1)).deleteNotes(note)
    }

    @Test
    fun emitsFlowOfBooleanThatDeletedCountSameAsRequestedCount() = runTest {
        runBlocking { whenever(dao.deleteNotes(note)).thenReturn(1) }
        assertEquals(Result.success(true), service.deleteNoteList(note).first())

        runBlocking { whenever(dao.deleteNotes(note)).thenReturn(0) }
        assertEquals(Result.success(false), service.deleteNoteList(note).first())
    }

    @Test
    fun emitsErrorWhenDeletionFails() = runTest {
        runBlocking {
            whenever(dao.deleteNotes(note)).thenThrow(deleteException)
        }

        assertEquals(
            deleteException.message,
            service.deleteNoteList(note).first().exceptionOrNull()?.message
        )
    }

    /**
     * Test suite for updateNote from dao
     * */
    @Test
    fun updateNoteFromDAO() = runTest {
        service.updateNoteList(note).first()

        verify(dao, times(1)).updateWithTimestamp(note)
    }

    @Test
    fun emitsFlowOfBooleanThatUpdatedCountSameAsRequestedCount() = runTest {
        runBlocking { whenever(dao.updateWithTimestamp(note)).thenReturn(1) }
        assertEquals(Result.success(true), service.updateNoteList(note).first())

        runBlocking { whenever(dao.updateWithTimestamp(note)).thenReturn(0) }
        assertEquals(Result.success(false), service.updateNoteList(note).first())
    }

    @Test
    fun emitsErrorWhenUpdateFails() = runTest {
        runBlocking {
            whenever(dao.updateWithTimestamp(note)).thenThrow(updateException)
        }

        assertEquals(
            updateException.message,
            service.updateNoteList(note).first().exceptionOrNull()?.message
        )
    }

    /**
     * Test suite for insertNote from dao
     * */
    @Test
    fun insertNoteFromDAO() = runTest {
        service.insertNote(note).first()

        verify(dao, times(1)).insertWithTimestamp(note)
    }

    @Test
    fun emitsFlowOfBooleanThatReturnedIdIsNegativeOrNot() = runTest {
        runBlocking { whenever(dao.insertWithTimestamp(note)).thenReturn(1) }
        assertEquals(Result.success(true), service.insertNote(note).first())

        runBlocking { whenever(dao.insertWithTimestamp(note)).thenReturn(-1) }
        assertEquals(Result.success(false), service.insertNote(note).first())
    }

    @Test
    fun emitsErrorWhenInsertionFails() = runTest {
        runBlocking { whenever(dao.insertWithTimestamp(note)).thenThrow(insertException) }

        assertEquals(
            insertException.message,
            service.insertNote(note).first().exceptionOrNull()?.message
        )
    }
}