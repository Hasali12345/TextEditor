package com.example.texteditor.editor

import android.content.Context
import kotlinx.coroutines.*

class CrashRecovery(
    private val context: Context,
    private val fileManager: FileManager
) {
    private var job: Job? = null

    fun startAutoSave(textProvider: () -> String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(10000) // Save every 10 seconds
                val currentText = textProvider()
                if (currentText.isNotEmpty()) {
                    fileManager.saveToCache(currentText)
                }
            }
        }
    }

    fun stopAutoSave() {
        job?.cancel()
        job = null
    }

    fun hasRecoverableContent(): Boolean {
        return fileManager.hasCache()
    }

    fun getRecoveredContent(): String? {
        return fileManager.readFromCache()
    }

    fun clearRecoveredContent() {
        fileManager.clearCache()
    }
}