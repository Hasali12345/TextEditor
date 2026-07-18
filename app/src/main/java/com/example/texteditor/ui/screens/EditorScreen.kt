package com.example.texteditor.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.texteditor.database.DatabaseHelper
import com.example.texteditor.editor.FileManager
import com.example.texteditor.editor.TextEditorManager
import com.example.texteditor.settings.AppSettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    dbHelper: DatabaseHelper,
    editorManager: TextEditorManager,
    settingsManager: AppSettingsManager,
) {
    val context = LocalContext.current
    val fileManager = remember { FileManager(context) }

    // ===== STATE =====
    var text by remember { mutableStateOf("") }
    var currentFileName by remember { mutableStateOf("Untitled.txt") }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }

    // Navigation state
    var showVersions by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Settings state
    var isReadOnly by remember { mutableStateOf(settingsManager.isReadOnlyEnabled()) }
    var isWordWrap by remember { mutableStateOf(settingsManager.isWordWrapEnabled()) }
    var fontSize by remember { mutableIntStateOf(settingsManager.getFontSize()) }

    // ===== REFRESH SETTINGS ON BACK =====
    LaunchedEffect(showSettings) {
        if (!showSettings) {
            isReadOnly = settingsManager.isReadOnlyEnabled()
            isWordWrap = settingsManager.isWordWrapEnabled()
            fontSize = settingsManager.getFontSize()
        }
    }

    // ===== FILE PICKERS =====
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val content = fileManager.readFile(it)
            text = content
            currentFileName = fileManager.getFileName(it)
            currentUri = it
            Toast.makeText(context, "✅ Opened: $currentFileName", Toast.LENGTH_LONG).show()
        }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        uri?.let {
            fileManager.writeFile(it, text)
            currentFileName = fileManager.getFileName(it)
            currentUri = it
            dbHelper.insertFile(currentFileName, text)
            Toast.makeText(context, "✅ Saved: $currentFileName", Toast.LENGTH_LONG).show()
        }
    }

    // ===== UNDO/REDO =====
    fun performUndo() {
        val previousText = editorManager.undo(text)
        if (previousText != null) {
            text = previousText
        } else {
            Toast.makeText(context, "Nothing to undo", Toast.LENGTH_SHORT).show()
        }
    }

    fun performRedo() {
        val nextText = editorManager.redo()
        if (nextText != null) {
            text = nextText
        } else {
            Toast.makeText(context, "Nothing to redo", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== REPLACE =====
    fun performReplaceAll() {
        if (searchText.isNotEmpty() && text.contains(searchText)) {
            text = editorManager.replaceText(text, searchText, replaceText)
            Toast.makeText(context, "✅ Replaced all", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Text not found", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== UI =====
    if (showVersions) {
        VersionScreen(
            fileName = currentFileName,
            onBackClick = { showVersions = false }
        )
    } else if (showSettings) {
        SettingsScreen(
            onBackClick = { showSettings = false },
            onReadOnlyChange = { updatedValue ->
                isReadOnly = updatedValue
                settingsManager.setReadOnlyMode(updatedValue)
            },
            onWordWrapChange = { updatedValue ->
                isWordWrap = updatedValue
                settingsManager.setWordWrap(updatedValue)
            },
            onFontSizeChange = { updatedValue ->
                fontSize = updatedValue
                settingsManager.setFontSize(updatedValue)
            },
            initialReadOnly = isReadOnly,
            initialWordWrap = isWordWrap,
            initialFontSize = fontSize
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Text Editor", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = currentFileName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { showSearch = !showSearch }) {
                            Text("🔍", fontSize = 20.sp)
                        }
                        TextButton(onClick = { performUndo() }) {
                            Text("↩️", fontSize = 20.sp)
                        }
                        TextButton(onClick = { performRedo() }) {
                            Text("↪️", fontSize = 20.sp)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ===== SEARCH BAR =====
                if (showSearch) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    label = { Text("Find") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = replaceText,
                                    onValueChange = { replaceText = it },
                                    label = { Text("Replace") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { performReplaceAll() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Replace All")
                                }
                                Button(
                                    onClick = {
                                        showSearch = false
                                        searchText = ""
                                        replaceText = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }

                // ===== EDITOR =====
                val verticalScrollState = rememberScrollState()
                val horizontalScrollState = rememberScrollState()

                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (!isReadOnly) {
                            text = newText
                            editorManager.saveState(newText)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(verticalScrollState)
                        .then(if (!isWordWrap) Modifier.horizontalScroll(horizontalScrollState) else Modifier),
                    readOnly = isReadOnly,
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = (fontSize + 8).sp
                    ),
                    cursorBrush = SolidColor(if (isReadOnly) Color.Transparent else MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (text.isEmpty()) {
                                Text(
                                    text = if (isReadOnly) "Read-only mode" else "📝 Start typing or press OPEN below...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = TextStyle(fontSize = fontSize.sp, fontFamily = FontFamily.Monospace)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // ===== BOTTOM BUTTONS =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // OPEN
                    Button(
                        onClick = {
                            Toast.makeText(context, "📂 Opening file picker...", Toast.LENGTH_SHORT).show()
                            openFileLauncher.launch(arrayOf("*/*"))
                        },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("📂 Open", fontSize = 14.sp)
                    }

                    // SAVE
                    Button(
                        onClick = {
                            if (currentUri != null) {
                                fileManager.writeFile(currentUri!!, text)
                                dbHelper.insertFile(currentFileName, text)
                                Toast.makeText(context, "💾 File saved!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "💾 Choose where to save...", Toast.LENGTH_SHORT).show()
                                saveFileLauncher.launch("Untitled.txt")
                            }
                        },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("💾 Save", fontSize = 14.sp)
                    }

                    // VERSIONS
                    Button(
                        onClick = { showVersions = true },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("📜 Versions", fontSize = 14.sp)
                    }

                    // SETTINGS
                    Button(
                        onClick = { showSettings = true },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                    ) {
                        Text("⚙️ Settings", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}