package com.example.texteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.texteditor.database.DatabaseHelper
import com.example.texteditor.editor.TextEditorManager
import com.example.texteditor.settings.AppSettingsManager
import com.example.texteditor.ui.screens.EditorScreen
import com.example.texteditor.ui.theme.TextEditorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DatabaseHelper(this)
        val editorManager = TextEditorManager()
        val settingsManager = AppSettingsManager(this)

        setContent {
            TextEditorTheme {
                EditorScreen(
                    dbHelper = dbHelper,
                    editorManager = editorManager,
                    settingsManager = settingsManager,
                )
            }
        }
    }
}