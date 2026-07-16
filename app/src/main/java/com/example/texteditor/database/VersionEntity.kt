package com.example.texteditor.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "versions")
data class VersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val versionNumber: Int,
    val diffText: String,
    val date: Long
)
