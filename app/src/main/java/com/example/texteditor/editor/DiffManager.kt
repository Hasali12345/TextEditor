package com.example.texteditor.editor

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Patch

class DiffManager {

    fun generateDiff(original: String, revised: String): String {
        val originalLines = original.lines()
        val revisedLines = revised.lines()

        val patch = DiffUtils.diff(originalLines, revisedLines)
        return patch.deltas.joinToString("\n") { delta ->
            when (delta.type) {
                com.github.difflib.patch.DeltaType.INSERT ->
                    "+ ${delta.target.lines.joinToString("\n+ ")}"
                com.github.difflib.patch.DeltaType.DELETE ->
                    "- ${delta.source.lines.joinToString("\n- ")}"
                com.github.difflib.patch.DeltaType.CHANGE ->
                    "~ ${delta.source.lines.joinToString("\n~ ")} -> ${delta.target.lines.joinToString("\n~ ")}"
                else -> ""
            }
        }
    }

    fun applyDiff(original: String, diffText: String): String {
        // Simplified: In real app you'd parse and apply the diff
        // For now, we'll just return original if no diff
        return original
    }

    fun createVersionDiff(oldText: String, newText: String): String {
        return generateDiff(oldText, newText)
    }
}