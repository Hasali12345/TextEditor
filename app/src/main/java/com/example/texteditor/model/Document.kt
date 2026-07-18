package com.example.texteditor.model

/**
 * Data class representing a text document in the application.
 * Integrates properties required for both File Management and Version/Settings Control.
 */
data class Document(
    var id: Int = 0,
    var title: String,
    var content: String,
    var lastModified: Long,
    var isReadOnly: Boolean = false, // Required for Member 2's Read-Only mode setting
)