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
import com.example.texteditor.database.AppDatabase
import com.example.texteditor.database.DatabaseHelper
import com.example.texteditor.database.VersionEntity
import com.example.texteditor.editor.DraftManager
import com.example.texteditor.editor.FileManager
import com.example.texteditor.editor.TextEditorManager
import com.example.texteditor.settings.AppSettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.VisualTransformation
import com.example.texteditor.editor.KotlinSyntaxHighlighter
import com.example.texteditor.editor.MarkdownRenderer
import com.example.texteditor.editor.MarkdownSyntaxHighlighter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    dbHelper: DatabaseHelper,
    editorManager: TextEditorManager,
    settingsManager: AppSettingsManager,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fileManager = remember { FileManager(context) }
    val draftManager = remember { DraftManager(context) }

    val versionDao = remember {
        AppDatabase.getDatabase(context).versionDao()
    }

    // ===== STATE =====
    val text = remember { mutableStateOf("") }
    val currentFileName = remember { mutableStateOf("Untitled.txt") }
    val currentUri = remember { mutableStateOf<Uri?>(null) }
    // ===== ADVANCED SYNTAX HIGHLIGHTING =====

    val syntaxHighlightingEnabled = remember {
        mutableStateOf(true)
    }

    val markdownPreviewEnabled = remember {
        mutableStateOf(false)
    }

    val kotlinSyntaxHighlighter = remember {
        KotlinSyntaxHighlighter(context)
    }

    val markdownSyntaxHighlighter = remember {
        MarkdownSyntaxHighlighter()
    }
    val showSearch = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    val replaceText = remember { mutableStateOf("") }

    // Navigation state
    val showVersions = remember { mutableStateOf(false) }
    val showSettings = remember { mutableStateOf(false) }
    val showDiff = remember { mutableStateOf(false) }
    val diffCurrentText = remember { mutableStateOf("") }
    val diffVersionText = remember { mutableStateOf("") }

    // Settings state
    val isReadOnly = remember { mutableStateOf(settingsManager.isReadOnlyEnabled()) }
    val isWordWrap = remember { mutableStateOf(settingsManager.isWordWrapEnabled()) }
    val fontSize = remember { mutableIntStateOf(settingsManager.getFontSize()) }

    // Auto Save
    val autoSaveEnabled = remember { mutableStateOf(settingsManager.isAutoSaveEnabled()) }
    val autoSaveDelay = remember { mutableIntStateOf(2000) }

    // Crash Recovery
    val showRecoveryDialog = remember { mutableStateOf(false) }
    var draftText by remember { mutableStateOf("") }
    var draftFileName by remember { mutableStateOf("") }

    // ===== CHECK FOR CRASH RECOVERY =====
    LaunchedEffect(Unit) {
        if (draftManager.hasDraft()) {
            val (text, fileName) = draftManager.getDraft()
            if (text != null && fileName != null) {
                draftText = text
                draftFileName = fileName
                showRecoveryDialog.value = true
            }
        }
    }

    // ===== AUTO SAVE =====
    LaunchedEffect(text.value) {
        if (autoSaveEnabled.value && currentUri.value != null && text.value.isNotEmpty()) {
            delay(autoSaveDelay.intValue.toLong())
            try {
                fileManager.writeFile(currentUri.value!!, text.value)
                dbHelper.insertFile(currentFileName.value, text.value)

                scope.launch {
                    val count = versionDao.getVersionCount(currentFileName.value)
                    versionDao.insertVersion(
                        VersionEntity(
                            fileName = currentFileName.value,
                            versionNumber = count + 1,
                            diffText = text.value,
                            date = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                // Silent fail for auto save
            }
        }
    }

    // ===== SAVE DRAFT FOR CRASH RECOVERY =====
    LaunchedEffect(text.value) {
        if (text.value.isNotEmpty() && currentFileName.value.isNotEmpty()) {
            draftManager.saveDraft(text.value, currentFileName.value)
        }
    }

    // ===== REFRESH SETTINGS ON BACK =====
    LaunchedEffect(showSettings.value) {
        if (!showSettings.value) {
            isReadOnly.value = settingsManager.isReadOnlyEnabled()
            isWordWrap.value = settingsManager.isWordWrapEnabled()
            fontSize.intValue = settingsManager.getFontSize()
            autoSaveEnabled.value = settingsManager.isAutoSaveEnabled()
        }
    }

    // ===== RECOVERY DIALOG =====
    if (showRecoveryDialog.value) {
        AlertDialog(
            onDismissRequest = {
                draftManager.clearDraft()
                showRecoveryDialog.value = false
            },
            title = { Text("Recover Unsaved Document?") },
            text = {
                Text("The app previously closed unexpectedly. Would you like to recover your unsaved document?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        text.value = draftText
                        currentFileName.value = draftFileName
                        draftManager.clearDraft()
                        showRecoveryDialog.value = false
                        Toast.makeText(context, "✅ Document recovered", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Recover")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        draftManager.clearDraft()
                        showRecoveryDialog.value = false
                        Toast.makeText(context, "Draft discarded", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Discard")
                }
            }
        )
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
            draftManager.clearDraft()
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
            draftManager.clearDraft()

            scope.launch {
                val count = versionDao.getVersionCount(currentFileName.value)
                versionDao.insertVersion(
                    VersionEntity(
                        fileName = currentFileName.value,
                        versionNumber = count + 1,
                        diffText = text.value,
                        date = System.currentTimeMillis()
                    )
                )
            }

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
            onBackClick = { showVersions.value = false },
            onRollbackClick = { versionText ->
                text.value = versionText
                editorManager.saveState(versionText)
                Toast.makeText(context, "✅ Version restored", Toast.LENGTH_LONG).show()
            },
            onViewDiffClick = { current, version ->
                diffCurrentText.value = current
                diffVersionText.value = version
                showDiff.value = true
            }
        )
    } else if (showDiff.value) {
        DiffScreen(
            currentText = diffCurrentText.value,
            versionText = diffVersionText.value,
            onBackClick = { showDiff.value = false }
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
            onAutoSaveChange = { updatedValue ->
                autoSaveEnabled.value = updatedValue
                settingsManager.setAutoSave(updatedValue)
            },
            initialReadOnly = isReadOnly.value,
            initialWordWrap = isWordWrap.value,
            initialFontSize = fontSize.intValue,
            initialAutoSave = autoSaveEnabled.value
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

                        // Search
                        TextButton(
                            onClick = {
                                showSearch.value = !showSearch.value
                            }
                        ) {
                            Text(
                                "🔍",
                                fontSize = 20.sp
                            )
                        }

                        // Syntax Highlighting ON / OFF
                        TextButton(
                            onClick = {
                                syntaxHighlightingEnabled.value =
                                    !syntaxHighlightingEnabled.value
                            }
                        ) {
                            Text(
                                if (syntaxHighlightingEnabled.value) {
                                    "🎨"
                                } else {
                                    "⚪"
                                },
                                fontSize = 20.sp
                            )
                        }

                        // Markdown Preview
                        TextButton(
                            onClick = {

                                val isMarkdown =
                                    currentFileName.value.endsWith(
                                        ".md",
                                        ignoreCase = true
                                    ) ||
                                            currentFileName.value.endsWith(
                                                ".markdown",
                                                ignoreCase = true
                                            )

                                if (isMarkdown) {

                                    markdownPreviewEnabled.value =
                                        !markdownPreviewEnabled.value

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Open a .md or .markdown file first",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Text(
                                "MD",
                                fontSize = 14.sp
                            )
                        }

                        // Undo
                        TextButton(
                            onClick = {
                                performUndo()
                            }
                        ) {
                            Text(
                                "↩️",
                                fontSize = 20.sp
                            )
                        }

                        // Redo
                        TextButton(
                            onClick = {
                                performRedo()
                            }
                        ) {
                            Text(
                                "↪️",
                                fontSize = 20.sp
                            )
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

                // ===== EDITOR + MARKDOWN PREVIEW =====

                val verticalScrollState = rememberScrollState()
                val horizontalScrollState = rememberScrollState()

                val isMarkdownFile =
                    currentFileName.value.endsWith(
                        ".md",
                        ignoreCase = true
                    ) ||
                            currentFileName.value.endsWith(
                                ".markdown",
                                ignoreCase = true
                            )

                if (markdownPreviewEnabled.value && isMarkdownFile) {

                    // =====================================================
                    // EDITOR + PREVIEW
                    // =====================================================

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {

                        // =================================================
                        // MARKDOWN EDITOR
                        // =================================================

                        BasicTextField(
                            value = text.value,

                            onValueChange = { newText ->

                                if (!isReadOnly.value) {

                                    text.value = newText

                                    editorManager.saveState(
                                        newText
                                    )
                                }
                            },

                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(12.dp)
                                .verticalScroll(
                                    verticalScrollState
                                )
                                .then(
                                    if (!isWordWrap.value) {
                                        Modifier.horizontalScroll(
                                            horizontalScrollState
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),

                            readOnly = isReadOnly.value,

                            textStyle = TextStyle(
                                fontSize = fontSize.intValue.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight =
                                    (fontSize.intValue + 8).sp
                            ),

                            cursorBrush = SolidColor(
                                if (isReadOnly.value) {
                                    Color.Transparent
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),

                            visualTransformation =
                                if (syntaxHighlightingEnabled.value) {
                                    markdownSyntaxHighlighter
                                } else {
                                    VisualTransformation.None
                                },

                            decorationBox = { innerTextField ->

                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    if (text.value.isEmpty()) {

                                        Text(
                                            text = "Write Markdown here...",

                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant,

                                            fontFamily =
                                                FontFamily.Monospace
                                        )
                                    }

                                    innerTextField()
                                }
                            }
                        )

                        // =================================================
                        // DIVIDER
                        // =================================================

                        VerticalDivider()

                        // =================================================
                        // PREVIEW
                        // =================================================

                        MarkdownPreview(
                            markdown = text.value,
                            fontSize = fontSize.intValue,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                } else {

                    // =====================================================
                    // NORMAL EDITOR
                    // =====================================================

                    BasicTextField(
                        value = text.value,

                        onValueChange = { newText ->

                            if (!isReadOnly.value) {

                                text.value = newText

                                editorManager.saveState(
                                    newText
                                )
                            }
                        },

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(
                                verticalScrollState
                            )
                            .then(
                                if (!isWordWrap.value) {
                                    Modifier.horizontalScroll(
                                        horizontalScrollState
                                    )
                                } else {
                                    Modifier
                                }
                            ),

                        readOnly = isReadOnly.value,

                        textStyle = TextStyle(
                            fontSize = fontSize.intValue.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight =
                                (fontSize.intValue + 8).sp
                        ),

                        cursorBrush = SolidColor(
                            if (isReadOnly.value) {
                                Color.Transparent
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        ),

                        visualTransformation =
                            if (syntaxHighlightingEnabled.value) {

                                if (
                                    currentFileName.value.endsWith(
                                        ".md",
                                        ignoreCase = true
                                    ) ||
                                    currentFileName.value.endsWith(
                                        ".markdown",
                                        ignoreCase = true
                                    )
                                ) {
                                    markdownSyntaxHighlighter
                                } else {
                                    kotlinSyntaxHighlighter
                                }

                            } else {
                                VisualTransformation.None
                            },

                        decorationBox = { innerTextField ->

                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                if (text.value.isEmpty()) {

                                    Text(
                                        text =
                                            if (isReadOnly.value) {
                                                "Read-only mode"
                                            } else {
                                                "📝 Start typing or press OPEN below..."
                                            },

                                        color =
                                            MaterialTheme.colorScheme
                                                .onSurfaceVariant,

                                        style = TextStyle(
                                            fontSize =
                                                fontSize.intValue.sp,

                                            fontFamily =
                                                FontFamily.Monospace
                                        )
                                    )
                                }

                                innerTextField()
                            }
                        }
                    )
                }

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
                                draftManager.clearDraft()

                                scope.launch {
                                    val count = versionDao.getVersionCount(currentFileName.value)
                                    versionDao.insertVersion(
                                        VersionEntity(
                                            fileName = currentFileName.value,
                                            versionNumber = count + 1,
                                            diffText = text.value,
                                            date = System.currentTimeMillis()
                                        )
                                    )
                                }

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
@Composable
fun MarkdownPreview(
    markdown: String,
    fontSize: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {

        Text(
            text = "Markdown Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (markdown.isBlank()) {

            Text(
                text = "Nothing to preview",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            markdown.lines().forEach { line ->

                when {

                    line.startsWith("# ") -> {
                        Text(
                            text = line.removePrefix("# "),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    line.startsWith("## ") -> {
                        Text(
                            text = line.removePrefix("## "),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    line.startsWith("### ") -> {
                        Text(
                            text = line.removePrefix("### "),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    line.startsWith("> ") -> {
                        Text(
                            text = line.removePrefix("> "),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = fontSize.sp
                        )
                    }

                    line.matches(
                        Regex("^\\s*[-*+]\\s+.*")
                    ) -> {
                        Text(
                            text = "• " + line.trim().substring(2),
                            fontSize = fontSize.sp
                        )
                    }

                    line.matches(
                        Regex("^\\s*\\d+\\.\\s+.*")
                    ) -> {
                        Text(
                            text = line,
                            fontSize = fontSize.sp
                        )
                    }

                    line.startsWith("```") -> {
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = fontSize.sp
                        )
                    }

                    else -> {
                        Text(
                            text = parseMarkdownInline(line),
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize + 8).sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )
            }
        }
    }
}
@Composable
fun parseMarkdownInline(
    text: String
): AnnotatedString {

    return buildAnnotatedString {

        var index = 0

        while (index < text.length) {

            // BOLD
            if (text.startsWith("**", index)) {

                val end = text.indexOf(
                    "**",
                    index + 2
                )

                if (end != -1) {

                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(
                            text.substring(
                                index + 2,
                                end
                            )
                        )
                    }

                    index = end + 2
                    continue
                }
            }

            // INLINE CODE
            if (text[index] == '`') {

                val end = text.indexOf(
                    '`',
                    index + 1
                )

                if (end != -1) {

                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        append(
                            text.substring(
                                index + 1,
                                end
                            )
                        )
                    }

                    index = end + 1
                    continue
                }
            }

            // ITALIC
            if (
                text[index] == '*' &&
                !text.startsWith("**", index)
            ) {

                val end = text.indexOf(
                    '*',
                    index + 1
                )

                if (end != -1) {

                    withStyle(
                        SpanStyle(
                            fontStyle =
                                androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    ) {
                        append(
                            text.substring(
                                index + 1,
                                end
                            )
                        )
                    }

                    index = end + 1
                    continue
                }
            }

            append(text[index])
            index++
        }
    }
}