package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteLocalService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotesPageServiceShould: BaseUnitTest() {
    private val dao: NoteDAO = mock()
    private val noteList = mock<List<NoteModel>>()
    private val note = mock<NoteModel>()

    private val exception = RuntimeException("Failed to get list of notes")
    private val deleteException = RuntimeException("Failed to delete list of notes")

    @Test
    fun getNoteListFromDAO() = runTest {
        val service = NoteLocalService(dao)

        service.getNoteList().first()

        verify(dao, times(1)).getNotes()
    }

    @Test
    fun convertValuesToFlowResultAndEmitsThem() = runTest {
        val service = mockSuccessfulCase()

        assertEquals(Result.success(noteList), service.getNoteList().first())
    }

    @Test
    fun emitsErrorResultWhenFails() = runTest {
        runBlocking {
            whenever(dao.getNotes()).thenThrow(exception)
        }

        val service = NoteLocalService(dao)

        try {
            service.getNoteList().first()
            fail("Expected RuntimeException to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Failed to get list of notes", e.message)
        }
    }

    @Test
    fun deleteNoteListFromDAO() = runTest {
        val service = NoteLocalService(dao)

        service.deleteNoteList(note).first()

        verify(dao, times(1)).deleteNotes(note)
    }

    @Test
    fun convertResultToFLowAndEmitsThemAfterDeleteNoteList() = runTest {
        val service = NoteLocalService(dao)

        runBlocking { whenever(dao.deleteNotes(note)).thenReturn(1) }
        assertEquals(Result.success(true), service.deleteNoteList(note).first())

        runBlocking { whenever(dao.deleteNotes(note)).thenReturn(0) }
        assertEquals(Result.success(false), service.deleteNoteList(note).first())
    }

    @Test
    fun emitsErrorWhenDeletionFails() = runTest {
        val service = NoteLocalService(dao)

        runBlocking {
            whenever(dao.deleteNotes(note)).thenThrow(deleteException)
        }

        assertEquals("Failed to delete list of notes",
            service.deleteNoteList(note).first().exceptionOrNull()?.message)
    }

    private fun mockSuccessfulCase(): NoteLocalService {
        runBlocking {
            whenever(dao.getNotes()).thenReturn(
                flow {
                    emit(noteList)
                }
            )
        }

        return NoteLocalService(dao)
    }
}