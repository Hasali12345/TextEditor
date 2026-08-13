package com.example.texteditor.model

data class Version(
    val id: Int = 0,
    val fileName: String,
    val versionNumber: Int,
    val diffText: String,
    val date: Long = System.currentTimeMillis(),
)