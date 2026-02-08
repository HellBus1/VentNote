package com.digiventure.ventnote.feature.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.digiventure.ventnote.R
import com.digiventure.ventnote.config.NoteDatabase
import com.digiventure.ventnote.data.persistence.NoteModel

class NoteWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var notes: List<NoteModel> = emptyList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // Fetch notes from database
        notes = NoteDatabase.getInstance(context).dao().getSyncNotes()
    }

    override fun onDestroy() {
        notes = emptyList()
    }

    override fun getCount(): Int = notes.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= notes.size) return RemoteViews(context.packageName, R.layout.note_widget_item)

        val note = notes[position]
        val views = RemoteViews(context.packageName, R.layout.note_widget_item)

        views.setTextViewText(R.id.widget_item_title, note.title)
        views.setTextViewText(R.id.widget_item_content, note.note)

        // Fill in specific data for the click template
        val fillInIntent = Intent().apply {
            putExtra("noteId", note.id.toString())
        }
        views.setOnClickFillInIntent(R.id.widget_item_root, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = notes[position].id.toLong()

    override fun hasStableIds(): Boolean = true
}
