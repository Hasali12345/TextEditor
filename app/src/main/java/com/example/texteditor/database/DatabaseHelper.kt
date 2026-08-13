package com.example.texteditor.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.texteditor.model.Document

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "texteditor.db"
        private const val DATABASE_VERSION = 1

        // Member 1: File Management Tables
        const val TABLE_FILES = "files"
        const val COL_FILE_ID = "id"
        const val COL_FILE_TITLE = "title"
        const val COL_FILE_CONTENT = "content"
        const val COL_FILE_MODIFIED = "last_modified"

        // Member 2: Version Control Tables
        const val TABLE_VERSIONS = "versions"
        const val COL_VERSION_ID = "version_id"
        const val COL_VERSION_FILE_ID = "file_id"
        const val COL_VERSION_CONTENT = "version_content"
        const val COL_VERSION_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Member 1: Files Table creation
        val createFilesTable = ("CREATE TABLE $TABLE_FILES ("
                + "$COL_FILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_FILE_TITLE TEXT, "
                + "$COL_FILE_CONTENT TEXT, "
                + "$COL_FILE_MODIFIED INTEGER)")
        db?.execSQL(createFilesTable)

        // Member 2: Versions Table creation
        val createVersionsTable = ("CREATE TABLE $TABLE_VERSIONS ("
                + "$COL_VERSION_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_VERSION_FILE_ID INTEGER, "
                + "$COL_VERSION_CONTENT TEXT, "
                + "$COL_VERSION_TIMESTAMP INTEGER, "
                + "FOREIGN KEY($COL_VERSION_FILE_ID) REFERENCES $TABLE_FILES($COL_FILE_ID))")
        db?.execSQL(createVersionsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_VERSIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILES")
        onCreate(db)
    }
    fun insertFile(title: String, content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_FILE_TITLE, title)
            put(COL_FILE_CONTENT, content)
            put(COL_FILE_MODIFIED, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_FILES, null, values)

        if (id != -1L) {
            insertVersion(id.toInt(), content)
        }
        return id
    }

    fun updateFile(id: Int, title: String, content: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_FILE_TITLE, title)
            put(COL_FILE_CONTENT, content)
            put(COL_FILE_MODIFIED, System.currentTimeMillis())
        }

        insertVersion(id, content)

        return db.update(TABLE_FILES, values, "$COL_FILE_ID = ?", arrayOf(id.toString()))
    }

    fun getFileById(id: Int): Document? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_FILES, null, "$COL_FILE_ID = ?", arrayOf(id.toString()), null, null, null)

        var document: Document? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_CONTENT))
            val modified = cursor.getLong(cursor.getColumnIndexOrThrow(COL_FILE_MODIFIED))
            document = Document(id, title, content, modified)
        }
        cursor.close()
        return document
    }

    fun getAllFilesRecent(): List<Document> {
        val fileList = ArrayList<Document>()
        val db = this.readableDatabase
        // Last modified date eka matha descending order ekata list eka gani
        val selectQuery = "SELECT * FROM $TABLE_FILES ORDER BY $COL_FILE_MODIFIED DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_CONTENT))
                val modified = cursor.getLong(cursor.getColumnIndexOrThrow(COL_FILE_MODIFIED))
                fileList.add(Document(id, title, content, modified))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return fileList
    }
    private fun insertVersion(fileId: Int, content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_VERSION_FILE_ID, fileId)
            put(COL_VERSION_CONTENT, content)
            put(COL_VERSION_TIMESTAMP, System.currentTimeMillis())
        }
        return db.insert(TABLE_VERSIONS, null, values)
    }
}