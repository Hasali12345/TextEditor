package com.example.texteditor.settings

import android.content.Context
import android.content.SharedPreferences

class AppSettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "text_editor_settings"
        private const val KEY_WORD_WRAP = "key_word_wrap"
        private const val KEY_READ_ONLY = "key_read_only"
        private const val KEY_FONT_SIZE = "key_font_size"
    }

    fun setWordWrap(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WORD_WRAP, enabled).apply()
    }

    fun isWordWrapEnabled(): Boolean {
        return prefs.getBoolean(KEY_WORD_WRAP, true)
    }

    fun setReadOnlyMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_READ_ONLY, enabled).apply()
    }

    fun isReadOnlyEnabled(): Boolean {
        return prefs.getBoolean(KEY_READ_ONLY, false)
    }

    fun setFontSize(size: Int) {
        prefs.edit().putInt(KEY_FONT_SIZE, size).apply()
    }

    fun getFontSize(): Int {
        return prefs.getInt(KEY_FONT_SIZE, 16)
    }
}