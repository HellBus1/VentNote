package com.digiventure.ventnote.feature.widget

import android.content.Context
import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.persistence.NoteDAO
import com.digiventure.ventnote.data.persistence.NoteModel
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

class NoteWidgetFactoryTest : BaseUnitTest() {
    private val context: Context = mock()
    private val proxy: DatabaseProxy = mock()
    private val dao: NoteDAO = mock()
    
    private lateinit var factory: NoteWidgetFactory
    
    private val notes = listOf(
        NoteModel(1, "Title 1", "Content 1", Date(), Date()),
        NoteModel(2, "Title 2", "Content 2", Date(), Date())
    )

    @Before
    fun setup() {
        whenever(proxy.dao()).thenReturn(dao)
        factory = NoteWidgetFactory(context, proxy)
    }

    @Test
    fun getCountShouldReturnNoteListSize() {
        whenever(dao.getSyncNotes()).thenReturn(notes)
        
        factory.onDataSetChanged()
        
        assertEquals(notes.size, factory.getCount())
    }

    @Test
    fun getItemIdShouldReturnNoteId() {
        whenever(dao.getSyncNotes()).thenReturn(notes)
        
        factory.onDataSetChanged()
        
        assertEquals(notes[0].id.toLong(), factory.getItemId(0))
        assertEquals(notes[1].id.toLong(), factory.getItemId(1))
    }

    @Test
    fun hasStableIdsShouldReturnTrue() {
        assertEquals(true, factory.hasStableIds())
    }

    @Test
    fun getViewTypeCountShouldReturnOne() {
        assertEquals(1, factory.getViewTypeCount())
    }
}
