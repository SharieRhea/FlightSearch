package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SearchBarRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SEARCH_CONTENT = stringPreferencesKey(name = "search_content")
        const val TAG = "SearchBarRepository"
    }

    suspend fun saveSearchContent(searchContent: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_CONTENT] = searchContent
        }
    }

    fun getSearchContent(): Flow<String> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    Log.e(TAG, "Error reading preferences.", it)
                    emit(emptyPreferences())
                } else
                    throw it
            }
            .map { preferences ->
                preferences[SEARCH_CONTENT] ?: ""
            }
    }
}