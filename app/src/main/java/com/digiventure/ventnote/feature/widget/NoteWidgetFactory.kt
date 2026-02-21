package com.digiventure.ventnote.feature.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.richtext.MarkdownToSpannable
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import com.digiventure.ventnote.data.persistence.NoteModel

class NoteWidgetFactory(
    private val context: Context,
    private val proxy: DatabaseProxy
) : RemoteViewsService.RemoteViewsFactory {
    private var notes: List<NoteModel> = emptyList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // Fetch notes from database
        notes = proxy.dao().getSyncNotes()
    }

    override fun onDestroy() {
        notes = emptyList()
    }

    override fun getCount(): Int = notes.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= notes.size) return RemoteViews(context.packageName, R.layout.note_widget_item)

        val note = notes[position]
        val views = RemoteViews(context.packageName, R.layout.note_widget_item)

        // Convert markdown to SpannableString for rich text rendering in widget
        val styledTitle = MarkdownToSpannable.convert(note.title)
        val styledContent = MarkdownToSpannable.convert(note.note)

        views.setTextViewText(R.id.widget_item_title, styledTitle)
        views.setTextViewText(R.id.widget_item_content, styledContent)

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
