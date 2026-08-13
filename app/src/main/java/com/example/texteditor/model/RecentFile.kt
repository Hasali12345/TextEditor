package com.example.texteditor.model

data class RecentFile(
    val name: String,
    val path: String,
    val lastOpened: Long = System.currentTimeMillis(),
)