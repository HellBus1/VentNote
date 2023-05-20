package com.digiventure.ventnote.data.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataStoreHelper @Inject constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun setLongData(key: String, value: Long): Flow<Result<Unit>> = flow {
        withContext(Dispatchers.IO) {
            val dataStoreKey = longPreferencesKey(key)
            try {
                dataStore.edit {
                    it[dataStoreKey] = value
                }
                emit(Result.success(Unit))
            } catch (e: Exception) {
                throw e
            }
        }
    }.catch {
        emit(Result.failure(it))
    }

    fun getLongData(key: String): Flow<Long> {
        val dataStoreKey = longPreferencesKey(key)
        return dataStore.data.map {
            it[dataStoreKey] ?: 0
        }
    }

    suspend fun setIntData(key: String, value: Int): Flow<Result<Unit>> = flow {
        withContext(Dispatchers.IO) {
            val dataStoreKey = intPreferencesKey(key)
            try {
                dataStore.edit {
                    it[dataStoreKey] = value
                }
                emit(Result.success(Unit))
            } catch (e: Exception) {
                throw e
            }
        }
    }.catch {
        emit(Result.failure(it))
    }

    fun getIntData(key: String): Flow<Int> {
        val dataStoreKey = intPreferencesKey(key)
        return dataStore.data.map {
            it[dataStoreKey] ?: 0
        }
    }
}