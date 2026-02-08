package com.digiventure.ventnote.feature.widget

import android.content.Context
import javax.inject.Inject

interface WidgetRefresher {
    fun refresh(context: Context)
}

class NoteWidgetRefresher @Inject constructor() : WidgetRefresher {
    override fun refresh(context: Context) {
        NoteWidgetProvider.refreshWidgets(context)
    }
}
