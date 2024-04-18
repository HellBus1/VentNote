package com.digiventure.ventnote.module.proxy

import android.app.Application
import com.digiventure.ventnote.config.NoteDatabase

interface Resettable {
    fun reset()
}

interface Provider<T> {
    fun getObject() : T
}

class DatabaseProxy(
    private val application: Application
): Provider<NoteDatabase>, Resettable {

    private var database: NoteDatabase = NoteDatabase.getInstance(application)

    @Synchronized
    override fun getObject(): NoteDatabase = database

    @Synchronized
    override fun reset() {
        if (database.isOpen) {
            database.close()
            database = NoteDatabase.getInstance(application)
        }
    }
}