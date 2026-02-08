package com.digiventure.ventnote.feature.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.digiventure.ventnote.module.proxy.DatabaseProxy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteWidgetService : RemoteViewsService() {
    @Inject
    lateinit var proxy: DatabaseProxy

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NoteWidgetFactory(this.applicationContext, proxy)
    }
}
