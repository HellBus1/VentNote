package com.digiventure.ventnote.notes

import com.digiventure.ventnote.utils.BaseUnitTest
import com.digiventure.ventnote.data.NoteModel
import com.digiventure.ventnote.data.local.NoteDAO
import com.digiventure.ventnote.data.local.NoteLocalService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotesPageServiceShould: BaseUnitTest() {
    private val dao: NoteDAO = mock()
    private val noteList = mock<List<NoteModel>>()

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
    fun emitsErrorResultWhenDatabaseFails() = runTest {
        val service = mockFailureCase()

        assertEquals(Result.success(emptyList<NoteModel>()), service.getNoteList().first())
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

    private fun mockFailureCase(): NoteLocalService {
        runBlocking {
            whenever(dao.getNotes()).thenReturn(
                flow {
                    emit(emptyList())
                }
            )
        }

        return NoteLocalService(dao)
    }
}