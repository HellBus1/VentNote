package com.digiventure.ventnote.module.proxy

import android.app.Application
import com.digiventure.ventnote.config.NoteDatabase
import com.digiventure.ventnote.data.persistence.NoteDAO


interface Provider<T> {
    fun getObject() : T
}

class DatabaseProxy(
    private val application: Application
) : Provider<NoteDatabase> {

    @Volatile
    private var database: NoteDatabase? = null

    @Synchronized
    override fun getObject(): NoteDatabase {
        if (database == null) {
            database = NoteDatabase.getInstance(application)
        }
        return database!!
    }

    fun dao(): NoteDAO {
        return getObject().dao()
    }
}