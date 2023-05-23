package com.digiventure.ventnote.feature.note_widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class NoteWidgetReceiver(
    override val glanceAppWidget: GlanceAppWidget = NoteWidget()
) : GlanceAppWidgetReceiver()