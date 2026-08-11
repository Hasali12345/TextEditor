package com.example.texteditor.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.texteditor.editor.KotlinKeywords
import com.example.texteditor.editor.KotlinSyntaxHighlightTransformation
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
fun EditorScreen() {

    var text by rememberSaveable {
        mutableStateOf(
            """
package com.example.texteditor

// Kotlin syntax highlighting

class Test {

    fun hello() {

        val name = "World"

        if (name.isNotEmpty()) {
            println("Hello ${'$'}name")
        }

        for (i in 0..10) {
            println(i)
        }
    }
}
            """.trimIndent()
        )
    }

    var isMarkdownPreview by rememberSaveable {
        mutableStateOf(false)
    }

    val undoStack =
        remember {
            mutableStateListOf<String>()
        }

    val redoStack =
        remember {
            mutableStateListOf<String>()
        }

    val context =
        androidx.compose.ui.platform.LocalContext.current

    val openLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                val loaded =
                    readFile(
                        context,
                        uri
                    )

                if (loaded != null) {

                    undoStack.add(text)

                    text = loaded

                    redoStack.clear()
                }
            }
        }

    val saveLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(
                "text/plain"
            )
        ) { uri ->

            if (uri != null) {

                saveFile(
                    context,
                    uri,
                    text
                )
            }
        }

    Surface(
        modifier =
            Modifier.fillMaxSize()
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {

            // =========================================
            // TOP BAR
            // =========================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = "Text Editor",
                        fontSize = 17.sp
                    )

                    Text(
                        text = "Untitled.txt",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                TextButton(
                    onClick = {
                        isMarkdownPreview =
                            !isMarkdownPreview
                    }
                ) {

                    Text(
                        if (isMarkdownPreview)
                            "Code"
                        else
                            "MD"
                    )
                }

                TextButton(
                    onClick = {}
                ) {
                    Text("Fold")
                }

                TextButton(
                    onClick = {}
                ) {
                    Text("🔍")
                }

                TextButton(
                    onClick = {}
                ) {
                    Text("↶")
                }

                TextButton(
                    onClick = {}
                ) {
                    Text("↷")
                }
            }


            // =========================================
            // TOOLBAR
            // =========================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .padding(
                            horizontal = 8.dp
                        ),
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                OutlinedButton(
                    onClick = {

                        openLauncher.launch(
                            arrayOf(
                                "text/*",
                                "*/*"
                            )
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.FolderOpen,
                        contentDescription =
                            "Open"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text("Open")
                }


                OutlinedButton(
                    onClick = {

                        saveLauncher.launch(
                            "Untitled.txt"
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Save,
                        contentDescription =
                            "Save"
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text("Save")
                }


                OutlinedButton(
                    enabled =
                        undoStack.isNotEmpty(),

                    onClick = {

                        if (
                            undoStack.isNotEmpty()
                        ) {

                            redoStack.add(text)

                            text =
                                undoStack.removeAt(
                                    undoStack.lastIndex
                                )
                        }
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Undo,
                        contentDescription =
                            "Undo"
                    )
                }


                OutlinedButton(
                    enabled =
                        redoStack.isNotEmpty(),

                    onClick = {

                        if (
                            redoStack.isNotEmpty()
                        ) {

                            undoStack.add(text)

                            text =
                                redoStack.removeAt(
                                    redoStack.lastIndex
                                )
                        }
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Redo,
                        contentDescription =
                            "Redo"
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            // =========================================
            // EDITOR
            // =========================================

            if (isMarkdownPreview) {

                MarkdownPreview(
                    markdown = text,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                )

            } else {

                KotlinEditor(
                    text = text,

                    onTextChange = { newText ->

                        if (newText != text) {

                            undoStack.add(text)

                            if (
                                undoStack.size > 100
                            ) {
                                undoStack.removeAt(0)
                            }

                            redoStack.clear()

                            text = newText
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                )
            }


            // =========================================
            // BOTTOM BAR
            // =========================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        openLauncher.launch(
                            arrayOf(
                                "text/*",
                                "*/*"
                            )
                        )
                    }
                ) {

                    Text("📂 Open")
                }


                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        saveLauncher.launch(
                            "Untitled.txt"
                        )
                    }
                ) {

                    Text("💾 Save")
                }


                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {}
                ) {

                    Text("📜 Version")
                }


                Button(
                    modifier =
                        Modifier.weight(1f),
                    onClick = {}
                ) {

                    Text("⚙ Setting")
                }
            }
        }
    }
}


/*
 * =====================================================
 * KOTLIN EDITOR
 * =====================================================
 */

@Composable
private fun KotlinEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier
) {

    val lines =
        text.split("\n")

    Row(
        modifier =
            modifier
                .background(
                    Color(0xFFF7F5FC),
                    RoundedCornerShape(8.dp)
                )
    ) {

        // =============================================
        // LINE NUMBERS
        // =============================================

        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(45.dp)
                    .background(
                        Color(0xFFEDEBF4)
                    )
                    .padding(
                        top = 8.dp
                    )
        ) {

            lines.forEachIndexed { index, line ->

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(24.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            if (
                                line.trim()
                                    .endsWith("{")
                            ) {
                                "▼ ${index + 1}"
                            } else {
                                "  ${index + 1}"
                            },

                        color =
                            Color(0xFF6F7180),

                        fontSize = 11.sp,

                        fontFamily =
                            FontFamily.Monospace
                    )
                }
            }
        }


        // =============================================
        // CODE
        // =============================================

        BasicTextField(

            value = text,

            onValueChange = onTextChange,

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        8.dp
                    ),

            textStyle =
                TextStyle(
                    color =
                        Color(0xFF222228),

                    fontSize =
                        14.sp,

                    lineHeight =
                        24.sp,

                    fontFamily =
                        FontFamily.Monospace
                ),

            visualTransformation =
                KotlinSyntaxHighlightTransformation(

                    keywords =
                        KotlinKeywords.all,

                    keywordColor =
                        Color(0xFF8E44AD),

                    stringColor =
                        Color(0xFF009688),

                    commentColor =
                        Color(0xFF689F38),

                    annotationColor =
                        Color(0xFFE67E22),

                    numberColor =
                        Color(0xFF1976D2)
                )
        )
    }
}


/*
 * =====================================================
 * MARKDOWN PREVIEW
 * =====================================================
 */

@Composable
private fun MarkdownPreview(
    markdown: String,
    modifier: Modifier
) {

    Column(
        modifier =
            modifier
                .background(
                    Color.White,
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
    ) {

        markdown
            .split("\n")
            .forEach { line ->

                when {

                    line.startsWith("# ") -> {

                        Text(
                            text =
                                line.removePrefix(
                                    "# "
                                ),

                            fontSize =
                                28.sp
                        )
                    }

                    line.startsWith("## ") -> {

                        Text(
                            text =
                                line.removePrefix(
                                    "## "
                                ),

                            fontSize =
                                23.sp
                        )
                    }

                    line.startsWith("### ") -> {

                        Text(
                            text =
                                line.removePrefix(
                                    "### "
                                ),

                            fontSize =
                                19.sp
                        )
                    }

                    line.startsWith("- ") -> {

                        Text(
                            text =
                                "• " +
                                        line.removePrefix(
                                            "- "
                                        ),

                            fontSize =
                                16.sp
                        )
                    }

                    line.startsWith("> ") -> {

                        Text(
                            text =
                                line.removePrefix(
                                    "> "
                                ),

                            color =
                                Color.Gray,

                            fontSize =
                                16.sp
                        )
                    }

                    line.isBlank() -> {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )
                    }

                    else -> {

                        Text(
                            text = line,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )
            }
    }
}


/*
 * =====================================================
 * READ FILE
 * =====================================================
 */

private fun readFile(
    context: Context,
    uri: Uri
): String? {

    return try {

        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->

                BufferedReader(
                    InputStreamReader(input)
                ).readText()
            }

    } catch (
        e: Exception
    ) {

        null
    }
}


/*
 * =====================================================
 * SAVE FILE
 * =====================================================
 */

private fun saveFile(
    context: Context,
    uri: Uri,
    text: String
) {

    try {

        context.contentResolver
            .openOutputStream(uri)
            ?.use { output ->

                output.write(
                    text.toByteArray()
                )
            }

    } catch (
        _: Exception
    ) {
    }
}