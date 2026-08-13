package com.example.texteditor.editor

import java.util.Stack

class TextEditorManager {
    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()
    private var isOperating = false
    fun saveState(currentText: String) {
        if (isOperating) return
        if (undoStack.isEmpty() || (undoStack.peek() != currentText)) {
            undoStack.push(currentText)
            redoStack.clear()
        }
    }
    fun undo(currentText: String): String? {
        if (undoStack.size > 1) {
            isOperating = true
            redoStack.push(undoStack.pop())
            val previousText = undoStack.peek()
            isOperating = false
            return previousText
        }
        return null
    }

    fun redo(): String? {
        if (redoStack.isNotEmpty()) {
            isOperating = true
            val nextText = redoStack.pop()
            undoStack.push(nextText)
            isOperating = false
            return nextText
        }
        return null
    }

    fun searchText(fullText: String, query: String): List<Int> {
        val indices = mutableListOf<Int>()
        if (query.isEmpty() || fullText.isEmpty()) return indices

        var index = fullText.indexOf(query)
        while (index >= 0) {
            indices.add(index)
            index = fullText.indexOf(query, index + query.length)
        }
        return indices
    }

    fun replaceText(fullText: String, query: String, replacement: String): String {
        if (query.isEmpty()) return fullText
        val updatedText = fullText.replace(query, replacement)
        saveState(updatedText)
        return updatedText
    }
}