package com.example.texteditor.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VersionDao {
    @Insert
    suspend fun insertVersion(version: VersionEntity)

    @Query("SELECT * FROM versions WHERE fileName = :fileName ORDER BY versionNumber DESC")
    suspend fun getVersionsByFileName(fileName: String): List<VersionEntity>

    @Query("SELECT * FROM versions WHERE id = :id")
    suspend fun getVersionById(id: Int): VersionEntity?

    @Query("SELECT COUNT(*) FROM versions WHERE fileName = :fileName")
    suspend fun getVersionCount(fileName: String): Int

    @Query("DELETE FROM versions WHERE fileName = :fileName")
    suspend fun deleteVersionsByFileName(fileName: String)
}