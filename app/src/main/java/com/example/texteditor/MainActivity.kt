package com.example.texteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.texteditor.ui.screens.EditorScreen
import com.example.texteditor.ui.theme.TextEditorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TextEditorTheme {
                EditorScreen()
            }
        }
    }
}