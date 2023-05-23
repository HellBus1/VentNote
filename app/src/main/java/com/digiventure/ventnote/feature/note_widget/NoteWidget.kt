package com.digiventure.ventnote.feature.note_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
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
import com.digiventure.ventnote.R
import com.digiventure.ventnote.data.local.NoteModel
import com.digiventure.ventnote.feature.note_widget.data.NoteDatabase
import com.digiventure.ventnote.feature.note_widget.data.NoteWidgetService
import com.digiventure.ventnote.ui.theme.Purple40

class NoteWidget : GlanceAppWidget() {
    companion object {
        val isDetailShowingKey = booleanPreferencesKey("is_detail_showing")
        val paramIsDetailShowing = ActionParameters.Key<Boolean>("is_detail_showing")

        val noteIdKey = intPreferencesKey("note_id")
        val paramNoteId = ActionParameters.Key<Int>("note_id")
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val service = NoteWidgetService(NoteDatabase.getDatabaseClient(context))

        val isDetailShowingValue = currentState(key = isDetailShowingKey) ?: false
        val noteIdValue = currentState(key = noteIdKey) ?: -1

        val noteList = service.getNoteList()
        val note = if (noteIdValue != -1) service.getNote(noteIdValue) else NoteModel("", "")

        Box(modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .cornerRadius(8.dp)
            .background(Color.White)) {

            if (!isDetailShowingValue && noteIdValue == -1) {
                NoteListWidget(noteList, paramIsDetailShowing, paramNoteId)
            } else if (isDetailShowingValue && noteIdValue != -1) {
                NoteDetailWidget(note, paramIsDetailShowing, paramNoteId)
            }
        }
    }

    @Composable
    fun NoteListWidget(
        noteList: List<NoteModel>,
        key1: ActionParameters.Key<Boolean>,
        key2: ActionParameters.Key<Int>
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = GlanceModifier.defaultWeight()) {}
                Text(
                    text = LocalContext.current.getString(R.string.refresh),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColorProvider(Purple40)
                    ),
                    modifier = GlanceModifier
                        .cornerRadius(8.dp)
                        .clickable(actionRunCallback<RefreshNoteListCallback>()),
                )
            }

            if (noteList.isEmpty()) {
                Column(modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = LocalContext.current.getString(R.string.empty_notes))
                }
            } else {
                LazyColumn(modifier = GlanceModifier.wrapContentHeight().fillMaxSize()) {
                    items(noteList) {
                        Box(modifier = GlanceModifier.padding(top = 16.dp).fillMaxWidth()) {
                            Row(modifier = GlanceModifier.fillMaxWidth()) {
                                Column(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
                                    Row(
                                        modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            it.title,
                                            style = TextStyle(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                            ),
                                            modifier = GlanceModifier.defaultWeight()
                                        )
                                        Text(text = LocalContext.current.getString(R.string.detail),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                                color = ColorProvider(Purple40)
                                            ),
                                            modifier = GlanceModifier.clickable(
                                                actionRunCallback<NoteListItemCallback>(actionParametersOf(
                                                    key1 to false,
                                                    key2 to it.id
                                                ))
                                            )
                                        )
                                    }
                                    Text(text = it.note,
                                        maxLines = 2,
                                        style = TextStyle(
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp
                                        ),
                                        modifier = GlanceModifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NoteDetailWidget(
        note: NoteModel,
        key1: ActionParameters.Key<Boolean>,
        key2: ActionParameters.Key<Int>
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = GlanceModifier.defaultWeight()) {}
                Text(
                    text = LocalContext.current.getString(R.string.back),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, color = ColorProvider(Purple40)
                    ),
                    modifier = GlanceModifier
                        .cornerRadius(8.dp)
                        .clickable(actionRunCallback<NoteListItemCallback>(actionParametersOf(
                            key1 to true,
                            key2 to -1
                        ))),
                )
            }

            Column {
                Text(note.title,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
                Text(note.note,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
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
        val isDetailShowingValue = parameters[NoteWidget.paramIsDetailShowing] ?: false
        val noteIdValue = parameters[NoteWidget.paramNoteId] ?: -1

        updateAppWidgetState(context, glanceId) {
            it[NoteWidget.isDetailShowingKey] = !isDetailShowingValue
            it[NoteWidget.noteIdKey] = noteIdValue
        }

        NoteWidget().update(context, glanceId)
    }

}