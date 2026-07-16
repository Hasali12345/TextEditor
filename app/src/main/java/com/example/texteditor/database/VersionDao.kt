package com.example.texteditor.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Insert
    suspend fun insertVersion(version: VersionEntity)

    @Query("SELECT * FROM versions WHERE fileName = :fileName ORDER BY versionNumber DESC")
    fun getVersionsForFile(fileName: String): Flow<List<VersionEntity>>

    @Query("SELECT * FROM versions WHERE fileName = :fileName ORDER BY versionNumber DESC")
    suspend fun getVersionsForFileSync(fileName: String): List<VersionEntity>

    @Query("SELECT * FROM versions WHERE id = :id")
    suspend fun getVersionById(id: Int): VersionEntity?

    @Query("DELETE FROM versions WHERE fileName = :fileName")
    suspend fun deleteVersionsForFile(fileName: String)

    @Query("SELECT COUNT(*) FROM versions WHERE fileName = :fileName")
    suspend fun getVersionCount(fileName: String): Int

    @Query("SELECT * FROM versions WHERE fileName = :fileName ORDER BY versionNumber ASC LIMIT 1")
    suspend fun getFirstVersion(fileName: String): VersionEntity?
}