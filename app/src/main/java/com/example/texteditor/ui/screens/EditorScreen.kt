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
    val text = remember { mutableStateOf("") }
    val currentFileName = remember { mutableStateOf("Untitled.txt") }
    val currentUri = remember { mutableStateOf<Uri?>(null) }
    val showSearch = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    val replaceText = remember { mutableStateOf("") }

    // Navigation state
    val showVersions = remember { mutableStateOf(false) }
    val showSettings = remember { mutableStateOf(false) }

    // Settings state
    val isReadOnly = remember { mutableStateOf(settingsManager.isReadOnlyEnabled()) }
    val isWordWrap = remember { mutableStateOf(settingsManager.isWordWrapEnabled()) }
    val fontSize = remember { mutableIntStateOf(settingsManager.getFontSize()) }

    // ===== REFRESH SETTINGS ON BACK =====
    LaunchedEffect(showSettings.value) {
        if (!showSettings.value) {
            isReadOnly.value = settingsManager.isReadOnlyEnabled()
            isWordWrap.value = settingsManager.isWordWrapEnabled()
            fontSize.intValue = settingsManager.getFontSize()
        }
    }

    // ===== FILE PICKERS =====
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val content = fileManager.readFile(it)
            text.value = content
            currentFileName.value = fileManager.getFileName(it)
            currentUri.value = it
            Toast.makeText(context, "✅ Opened: ${currentFileName.value}", Toast.LENGTH_LONG).show()
        }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        uri?.let {
            fileManager.writeFile(it, text.value)
            currentFileName.value = fileManager.getFileName(it)
            currentUri.value = it
            dbHelper.insertFile(currentFileName.value, text.value)
            Toast.makeText(context, "✅ Saved: ${currentFileName.value}", Toast.LENGTH_LONG).show()
        }
    }

    // ===== UNDO/REDO =====
    fun performUndo() {
        val previousText = editorManager.undo(text.value)
        if (previousText != null) {
            text.value = previousText
        } else {
            Toast.makeText(context, "Nothing to undo", Toast.LENGTH_SHORT).show()
        }
    }

    fun performRedo() {
        val nextText = editorManager.redo()
        if (nextText != null) {
            text.value = nextText
        } else {
            Toast.makeText(context, "Nothing to redo", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== REPLACE =====
    fun performReplaceAll() {
        if (searchText.value.isNotEmpty() && text.value.contains(searchText.value)) {
            text.value = editorManager.replaceText(text.value, searchText.value, replaceText.value)
            Toast.makeText(context, "✅ Replaced all", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Text not found", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== UI =====
    if (showVersions.value) {
        VersionScreen(
            fileName = currentFileName.value,
            onBackClick = { showVersions.value = false }
        )
    } else if (showSettings.value) {
        SettingsScreen(
            onBackClick = { showSettings.value = false },
            onReadOnlyChange = { updatedValue ->
                isReadOnly.value = updatedValue
                settingsManager.setReadOnlyMode(updatedValue)
            },
            onWordWrapChange = { updatedValue ->
                isWordWrap.value = updatedValue
                settingsManager.setWordWrap(updatedValue)
            },
            onFontSizeChange = { updatedValue ->
                fontSize.intValue = updatedValue
                settingsManager.setFontSize(updatedValue)
            },
            initialReadOnly = isReadOnly.value,
            initialWordWrap = isWordWrap.value,
            initialFontSize = fontSize.intValue
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Text Editor", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = currentFileName.value,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { showSearch.value = !showSearch.value }) {
                            Text("🔍", fontSize = 20.sp)
                        }
                        TextButton(
                            onClick = { performUndo() }
                        ) {
                            Text("↩️", fontSize = 20.sp)
                        }
                        TextButton(
                            onClick = { performRedo() }
                        ) {
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
                if (showSearch.value) {
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
                                    value = searchText.value,
                                    onValueChange = { searchText.value = it },
                                    label = { Text("Find") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = replaceText.value,
                                    onValueChange = { replaceText.value = it },
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
                                        showSearch.value = false
                                        searchText.value = ""
                                        replaceText.value = ""
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
                    value = text.value,
                    onValueChange = { newText ->
                        if (!isReadOnly.value) {
                            text.value = newText
                            editorManager.saveState(newText)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(verticalScrollState)
                        .then(if (!isWordWrap.value) Modifier.horizontalScroll(horizontalScrollState) else Modifier),
                    readOnly = isReadOnly.value,
                    textStyle = TextStyle(
                        fontSize = fontSize.intValue.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = (fontSize.intValue + 8).sp
                    ),
                    cursorBrush = SolidColor(if (isReadOnly.value) Color.Transparent else MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (text.value.isEmpty()) {
                                Text(
                                    text = if (isReadOnly.value) "Read-only mode" else "📝 Start typing or press OPEN below...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = TextStyle(fontSize = fontSize.intValue.sp, fontFamily = FontFamily.Monospace)
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("📂 Open", fontSize = 14.sp)
                    }

                    // SAVE
                    Button(
                        onClick = {
                            if (currentUri.value != null) {
                                fileManager.writeFile(currentUri.value!!, text.value)
                                dbHelper.insertFile(currentFileName.value, text.value)
                                Toast.makeText(context, "💾 File saved!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "💾 Choose where to save...", Toast.LENGTH_SHORT).show()
                                saveFileLauncher.launch("Untitled.txt")
                            }
                        },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("💾 Save", fontSize = 14.sp)
                    }

                    // VERSIONS
                    Button(
                        onClick = { showVersions.value = true },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Text("📜 Versions", fontSize = 14.sp)
                    }

                    // SETTINGS
                    Button(
                        onClick = { showSettings.value = true },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C27B0)
                        )
                    ) {
                        Text("⚙️ Settings", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}