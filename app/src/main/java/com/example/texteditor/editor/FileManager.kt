package com.example.texteditor.editor

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*

class FileManager(private val context: Context) {

    companion object {
        private const val TAG = "FileManager"
        private const val CACHE_FILE = "temp_recovery.txt"
    }

    fun readFile(uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: ""
        } catch (e: IOException) {
            Log.e(TAG, "Error reading file: ${e.message}")
            ""
        }
    }

    fun writeFile(uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(content)
                }
                true
            } ?: false
        } catch (e: IOException) {
            Log.e(TAG, "Error writing file: ${e.message}")
            false
        }
    }

    fun getFileName(uri: Uri): String {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && it.moveToFirst()) {
                    return it.getString(nameIndex)
                }
            }
            uri.lastPathSegment ?: "Untitled.txt"
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file name: ${e.message}")
            "Untitled.txt"
        }
    }

    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }

    fun saveToCache(content: String) {
        try {
            val cacheFile = File(context.cacheDir, CACHE_FILE)
            cacheFile.writeText(content)
        } catch (e: IOException) {
            Log.e(TAG, "Error saving to cache: ${e.message}")
        }
    }

    fun readFromCache(): String? {
        return try {
            val cacheFile = File(context.cacheDir, CACHE_FILE)
            if (cacheFile.exists()) cacheFile.readText() else null
        } catch (e: IOException) {
            Log.e(TAG, "Error reading from cache: ${e.message}")
            null
        }
    }

    fun hasCache(): Boolean {
        return File(context.cacheDir, CACHE_FILE).exists()
    }

    fun clearCache() {
        try {
            File(context.cacheDir, CACHE_FILE).delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache: ${e.message}")
        }
    }
}