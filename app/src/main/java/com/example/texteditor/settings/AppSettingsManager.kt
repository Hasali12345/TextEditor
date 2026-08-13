package com.example.texteditor.settings

import android.content.Context
import android.content.SharedPreferences

class AppSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val READ_ONLY_KEY = "read_only"
        private const val WORD_WRAP_KEY = "word_wrap"
        private const val FONT_SIZE_KEY = "font_size"
        private const val AUTO_SAVE_KEY = "auto_save"
        private const val DEFAULT_FONT_SIZE = 16
    }

    // Read Only Mode
    fun setReadOnlyMode(enabled: Boolean) {
        prefs.edit().putBoolean(READ_ONLY_KEY, enabled).apply()
    }

    fun isReadOnlyEnabled(): Boolean {
        return prefs.getBoolean(READ_ONLY_KEY, false)
    }

    // Word Wrap
    fun setWordWrap(enabled: Boolean) {
        prefs.edit().putBoolean(WORD_WRAP_KEY, enabled).apply()
    }

    fun isWordWrapEnabled(): Boolean {
        return prefs.getBoolean(WORD_WRAP_KEY, true)
    }

    // Font Size
    fun setFontSize(size: Int) {
        prefs.edit().putInt(FONT_SIZE_KEY, size).apply()
    }

    fun getFontSize(): Int {
        return prefs.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE)
    }

    // Auto Save
    fun setAutoSave(enabled: Boolean) {
        prefs.edit().putBoolean(AUTO_SAVE_KEY, enabled).apply()
    }

    fun isAutoSaveEnabled(): Boolean {
        return prefs.getBoolean(AUTO_SAVE_KEY, true)
    }
}