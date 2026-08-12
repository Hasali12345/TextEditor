package com.example.texteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.example.texteditor.database.DatabaseHelper
import com.example.texteditor.editor.TextEditorManager
import com.example.texteditor.settings.AppSettingsManager
import com.example.texteditor.ui.screens.EditorScreen
import com.example.texteditor.ui.screens.HomeScreen
import com.example.texteditor.ui.theme.TextEditorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DatabaseHelper(this)
        val editorManager = TextEditorManager()
        val settingsManager = AppSettingsManager(this)

        setContent {
            TextEditorTheme {

                var showEditor by remember {
                    mutableStateOf(false)
                }

                if (showEditor) {

                    EditorScreen(
                        dbHelper = dbHelper,
                        editorManager = editorManager,
                        settingsManager = settingsManager
                    )

                } else {

                    HomeScreen(
                        onOpenEditor = {
                            showEditor = true
                        },
                        onOpenFile = {
                            showEditor = true
                        }
                    )
                }
            }
        }
    }
}