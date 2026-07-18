package com.example.texteditor.editor

import android.content.Context
import android.content.SharedPreferences

class DraftManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("drafts", Context.MODE_PRIVATE)

    companion object {
        private const val DRAFT_KEY = "draft_text"
        private const val DRAFT_FILENAME_KEY = "draft_filename"
    }

    fun saveDraft(text: String, fileName: String) {
        prefs.edit().apply {
            putString(DRAFT_KEY, text)
            putString(DRAFT_FILENAME_KEY, fileName)
            apply()
        }
    }

    fun getDraft(): Pair<String?, String?> {
        val text = prefs.getString(DRAFT_KEY, null)
        val fileName = prefs.getString(DRAFT_FILENAME_KEY, null)
        return Pair(text, fileName)
    }

    fun clearDraft() {
        prefs.edit().apply {
            remove(DRAFT_KEY)
            remove(DRAFT_FILENAME_KEY)
            apply()
        }
    }

    fun hasDraft(): Boolean {
        return prefs.contains(DRAFT_KEY)
    }
}