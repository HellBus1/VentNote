package com.digiventure.ventnote.notes

import com.digiventure.utils.BaseUnitTest
import com.digiventure.utils.captureValues
import com.digiventure.utils.getValueForTest
import com.digiventure.ventnote.commons.Constants
import com.digiventure.ventnote.data.local.NoteDataStore
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.data.persistence.NoteRepository
import com.digiventure.ventnote.data.persistence.NoteTagCrossRef
import com.digiventure.ventnote.data.persistence.TagDAO
import com.digiventure.ventnote.data.persistence.TagRepository
import com.digiventure.ventnote.feature.notes.viewmodel.NotesPageVM
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotesPageVMShould : BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val tagRepository: TagRepository = mock()
    private val noteDataStore: NoteDataStore = mock()
    private val databaseProxy: DatabaseProxy = mock()
    private val tagDao: TagDAO = mock()
    private val noteDao: NoteDAO = mock()

    private val notes = listOf(
        NoteModel(1, "title1", "description1"),
        NoteModel(2, "title2", "description2")
    )
    private val note = NoteModel(1, "title1", "description1")
    private val sortBy = Constants.CREATED_AT
    private val orderBy = Constants.DESCENDING

    private val expected = Result.success(notes)
    private val exception = RuntimeException("Failed to get list of notes")

    private val expectedDeletion = Result.success(true)
    private val exceptionDeletion = RuntimeException("Failed to delete list of notes")

    private val pinException = RuntimeException("Failed to update list of notes")

    private lateinit var viewModel: NotesPageVM

    @Before
    fun setup() {
        runBlocking {
            whenever(noteDataStore.getStringData(Constants.NOTE_VIEW_MODE)).thenReturn(
                flowOf(Constants.VIEW_MODE_LIST)
            )
            // Provide empty flows so the init {} coroutines don't throw
            whenever(tagRepository.getAllTags()).thenReturn(flowOf(Result.success(emptyList())))
            whenever(databaseProxy.tagDao()).thenReturn(tagDao)
            whenever(tagDao.getAllNoteTagCrossRefsFlow()).thenReturn(flowOf(emptyList<NoteTagCrossRef>()))
        }
        viewModel = NotesPageVM(repository, tagRepository, noteDataStore, databaseProxy)
    }

    @Test
    fun setSortAndOrderDataWithPairOfTitleAndDescending() = runTest {
        val sortBy = Constants.TITLE
        val orderBy = Constants.ASCENDING

        viewModel.sortAndOrder(sortBy, orderBy)

        assertEquals(viewModel.sortAndOrderData.value, Pair(sortBy, orderBy))
    }

    @Test
    fun haveNullNoteListInTheInitialState() {
        val noteList = viewModel.noteList.getValueForTest()

        assertEquals(noteList?.getOrNull(), null)
    }

    @Test
    fun emitsNotesFromRepositoryWhenObserveNotesIsInvoked() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)

        viewModel.observeNotes()

        verify(repository, times(1)).getNoteList(sortBy, orderBy)
        assertEquals(expected, viewModel.noteList.getValueForTest())
    }

    @Test
    fun emitsErrorWhenReceiveError() = runTest {
        mockErrorCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)

        viewModel.observeNotes()

        assertEquals(exception, viewModel.noteList.getValueForTest()?.exceptionOrNull())
    }

    @Test
    fun emitErrorWhenGetNoteListIsThrowingError() = runTest {
        mockErrorCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)

        viewModel.observeNotes()

        assertEquals(exception, viewModel.noteList.getValueForTest()?.exceptionOrNull())
    }

    @Test
    fun closeLoaderAfterNoteListLoaded() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)

        viewModel.observeNotes()

        viewModel.loader.captureValues {
            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterGetNoteListError() = runTest {
        mockErrorCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)

        viewModel.observeNotes()

        viewModel.loader.captureValues {
            viewModel.noteList.getValueForTest()

            assertEquals(false, values.last())
        }
    }

    @Test
    fun verifySearchedTitleTextIsSameAsInput() = runTest {
        val inputTitle = "Test Title"
        viewModel.searchedTitleText.value = inputTitle
        assertEquals(inputTitle, viewModel.searchedTitleText.value)
    }

    @Test
    fun verifyIsMarkingIsSameAsInput() = runTest {
        viewModel.isMarking.value = true
        assertEquals(true, viewModel.isMarking.value)

        viewModel.isMarking.value = false
        assertEquals(false, viewModel.isMarking.value)
    }

    @Test
    fun verifySetNoteViewModeUpdatesStateAndDataStore() = runTest {
        viewModel.setNoteViewMode(Constants.VIEW_MODE_STAGGERED)
        assertEquals(Constants.VIEW_MODE_STAGGERED, viewModel.noteViewMode.value)
        verify(noteDataStore, times(1)).setStringData(Constants.NOTE_VIEW_MODE, Constants.VIEW_MODE_STAGGERED)
    }

    @Test
    fun verifyMarkedNoteListCanBeAddedOrRemoved() = runTest {
        viewModel.markedNoteList.add(note)
        assertEquals(1, viewModel.markedNoteList.size)

        viewModel.markedNoteList.remove(note)
        assertEquals(0, viewModel.markedNoteList.size)
    }

    @Test
    fun deleteNoteListFromRepository() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockSuccessfulDeletionCase()

        viewModel.deleteNoteList(note)

        verify(repository, times(1)).deleteNoteList(note)
    }

    @Test
    fun emitsBooleanFromRepository() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockSuccessfulDeletionCase()

        val result = viewModel.deleteNoteList(note)

        assertEquals(expectedDeletion, result)
    }

    @Test
    fun emitsErrorWhenDeletionError() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockErrorDeletionCase()

        val result = viewModel.deleteNoteList(note)

        assertEquals(Result.failure<Boolean>(exceptionDeletion), result)
    }

    @Test
    fun emitErrorWhenDeletionIsThrowingError() = runTest {
        whenever(repository.deleteNoteList(note)).thenThrow(exceptionDeletion)

        val result = viewModel.deleteNoteList(note)

        assertEquals(Result.failure<Boolean>(exceptionDeletion), result)
    }

    @Test
    fun showLoaderWhileDeletingNote() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockSuccessfulDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(true, values.first())
        }
    }

    @Test
    fun closeLoaderAfterDeleteNoteSuccess() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockSuccessfulDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun closeLoaderAfterDeleteNoteError() = runTest {
        mockSuccessfulCase()
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        mockErrorDeletionCase()

        viewModel.loader.captureValues {
            viewModel.deleteNoteList(note)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun verifyAddToMarkedNoteListAddsNoteToList() {
        viewModel.addToMarkedNoteList(note)
        assertTrue(note in viewModel.markedNoteList)
    }

    @Test
    fun verifyAddToMarkedNoteListRemovesNoteFromListIfAlreadyExists() {
        viewModel.addToMarkedNoteList(note)
        viewModel.addToMarkedNoteList(note)
        assertTrue(note !in viewModel.markedNoteList)
    }

    @Test
    fun verifyMarkAllNoteAddsAllUniqueNotesToMarkedList() = runTest {
        val notes = listOf(note, note)

        viewModel.markedNoteList.addAll(notes)

        viewModel.markAllNote(notes)

        assertTrue(viewModel.markedNoteList.size == 2)
    }

    @Test
    fun verifyUnMarkAllNoteRemovesAllNotesFromMarkedList() {
        val noteList = listOf(note, note)

        viewModel.markedNoteList.addAll(noteList)

        viewModel.unMarkAllNote()

        assertTrue(viewModel.markedNoteList.isEmpty())
    }

    @Test
    fun verifyCloseMarkingEventIsSetIsMarkingToFalseAndClearMarkedNoteList() {
        viewModel.markedNoteList.addAll(listOf(note, note, note))

        viewModel.closeMarkingEvent()

        assertFalse(viewModel.isMarking.value)
        assertEquals(viewModel.markedNoteList.size, 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Note Pinning Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun toggleNotePin_callsRepository() = runTest {
        mockSuccessfulPinCase()

        viewModel.toggleNotePin(note.id, true)

        verify(repository, times(1)).toggleNotePin(note.id, true)
    }

    @Test
    fun toggleNotePin_returnsSuccessResult() = runTest {
        mockSuccessfulPinCase()

        val result = viewModel.toggleNotePin(note.id, true)

        assertEquals(Result.success(true), result)
    }

    @Test
    fun toggleNotePin_returnsErrorResultOnFailure() = runTest {
        mockErrorPinCase()

        val result = viewModel.toggleNotePin(note.id, true)

        assertEquals(pinException.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun toggleNotePin_throwingException_returnsErrorResult() = runTest {
        whenever(repository.toggleNotePin(note.id, true)).thenThrow(pinException)

        val result = viewModel.toggleNotePin(note.id, true)

        assertEquals(pinException.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun toggleNotePin_showsLoaderDuringOperation() = runTest {
        mockSuccessfulPinCase()

        viewModel.loader.captureValues {
            viewModel.toggleNotePin(note.id, true)

            assertTrue(values.contains(true))
        }
    }

    @Test
    fun toggleNotePin_hidesLoaderAfterSuccess() = runTest {
        mockSuccessfulPinCase()

        viewModel.loader.captureValues {
            viewModel.toggleNotePin(note.id, true)

            assertEquals(false, values.last())
        }
    }

    @Test
    fun toggleNotePin_hidesLoaderAfterError() = runTest {
        mockErrorPinCase()

        viewModel.loader.captureValues {
            viewModel.toggleNotePin(note.id, true)

            assertEquals(false, values.last())
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Previously-missing coverage: sort change + tag filter path
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun sortAndOrderChange_doesNotThrowAndUpdatesLiveData() = runTest {
        // Verify that changing sortAndOrderData to a different value updates correctly
        viewModel.sortAndOrder(Constants.TITLE, Constants.ASCENDING)
        assertEquals(Pair(Constants.TITLE, Constants.ASCENDING), viewModel.sortAndOrderData.value)

        viewModel.sortAndOrder(Constants.UPDATED_AT, Constants.DESCENDING)
        assertEquals(Pair(Constants.UPDATED_AT, Constants.DESCENDING), viewModel.sortAndOrderData.value)
    }

    @Test
    fun observeNotes_withSelectedTagId_callsGetNotesByTag() = runTest {
        val tagId = 42
        val tagNotes = listOf(NoteModel(3, "Tagged Note", "body"))
        whenever(repository.getNotesByTag(tagId, sortBy, orderBy)).thenReturn(
            flowOf(Result.success(tagNotes))
        )
        viewModel.sortAndOrderData.value = Pair(sortBy, orderBy)
        viewModel.selectedTagId.value = tagId

        viewModel.observeNotes()

        verify(repository, times(1)).getNotesByTag(tagId, sortBy, orderBy)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

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

    private fun mockSuccessfulCase() {
        runBlocking {
            whenever(repository.getNoteList(sortBy, orderBy)).thenReturn(
                flow {
                    emit(expected)
                }
            )
        }
    }

    private fun mockErrorCase() {
        runBlocking {
            whenever(repository.getNoteList(sortBy, orderBy)).thenReturn(
                flow {
                    emit(Result.failure(exception))
                }
            )
        }
    }

    private fun mockSuccessfulPinCase() {
        runBlocking {
            whenever(repository.toggleNotePin(note.id, true)).thenReturn(
                flowOf(Result.success(true))
            )
        }
    }

    private fun mockErrorPinCase() {
        runBlocking {
            whenever(repository.toggleNotePin(note.id, true)).thenReturn(
                flowOf(Result.failure(pinException))
            )
        }
    }
}
