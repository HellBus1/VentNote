package com.digiventure.ventnote.feature.note_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.note_widget.data.NoteDatabase
import com.digiventure.ventnote.feature.note_widget.data.NoteWidgetService
import com.digiventure.ventnote.ui.theme.Purple40

class NoteWidget : GlanceAppWidget() {
    companion object {
        const val isDetailShowing = false
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val service = NoteWidgetService(NoteDatabase.getDatabaseClient(context))

        val noteList = service.getNoteList()

        Box(modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .cornerRadius(8.dp)
            .background(Color.White)) {

            NoteListWidget(noteList = noteList)
        }
    }

    @Composable
    fun NoteDetailWidget() {

    }

    @Composable
    fun NoteListWidget(noteList: List<NoteModel>) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "VentNote",
                    style = TextStyle(fontWeight = FontWeight.Medium,
                        fontSize = 16.sp, color = ColorProvider(Purple40)),
                    modifier = GlanceModifier.defaultWeight())
                Button(
                    text = "Refresh",
                    modifier = GlanceModifier.cornerRadius(8.dp),
                    onClick = actionRunCallback<RefreshNoteListCallback>(),
                )
            }
            LazyColumn(modifier = GlanceModifier
                .wrapContentHeight()
                .fillMaxSize()) {
                items(noteList) {
                    NoteItem(item = it)
                }
            }
        }
    }

    @Composable
    fun NoteItem(item: NoteModel) {
        Box(modifier = GlanceModifier
            .padding(top = 16.dp)
            .fillMaxWidth()) {
            Column {
                Text(
                    item.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
                Text(
                    item.note,
                    maxLines = 2,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    modifier = GlanceModifier.padding(top = 2.dp)
                )
            }
        }
    }
}

class RefreshNoteListCallback: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        NoteWidget().update(context, glanceId)
    }
}

class NoteListItemCallback: ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        TODO("Not yet implemented")
    }

}