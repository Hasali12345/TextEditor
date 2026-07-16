package com.example.texteditor.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsManager(private val context: Context) {

    companion object {
        private val READ_ONLY = booleanPreferencesKey("read_only")
        private val WORD_WRAP = booleanPreferencesKey("word_wrap")
        private val FONT_SIZE = intPreferencesKey("font_size")
        private val RECENT_FILES = stringPreferencesKey("recent_files")
    }

    val readOnlyFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[READ_ONLY] ?: false }

    val wordWrapFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[WORD_WRAP] ?: true }

    val fontSizeFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[FONT_SIZE] ?: 16 }

    suspend fun setReadOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[READ_ONLY] = enabled
        }
    }

    suspend fun setWordWrap(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WORD_WRAP] = enabled
        }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size
        }
    }
}