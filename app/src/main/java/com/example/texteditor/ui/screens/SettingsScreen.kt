package com.example.texteditor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onReadOnlyChange: (Boolean) -> Unit = {},
    onWordWrapChange: (Boolean) -> Unit = {},
    onFontSizeChange: (Int) -> Unit = {},
    onAutoSaveChange: (Boolean) -> Unit = {},
    initialReadOnly: Boolean = false,
    initialWordWrap: Boolean = true,
    initialFontSize: Int = 16,
    initialAutoSave: Boolean = true,
) {
    var readOnly by remember { mutableStateOf(initialReadOnly) }
    var wordWrap by remember { mutableStateOf(initialWordWrap) }
    var fontSize by remember { mutableIntStateOf(initialFontSize) }
    var autoSave by remember { mutableStateOf(initialAutoSave) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Read Only
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Read Only Mode", fontWeight = FontWeight.Medium)
                        Text(
                            "Prevents editing the document",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = readOnly,
                        onCheckedChange = {
                            readOnly = it
                            onReadOnlyChange(it)
                        }
                    )
                }
            }

            // Word Wrap
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Word Wrap", fontWeight = FontWeight.Medium)
                        Text(
                            "Wrap text to next line",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = wordWrap,
                        onCheckedChange = {
                            wordWrap = it
                            onWordWrapChange(it)
                        }
                    )
                }
            }

            // Auto Save
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto Save", fontWeight = FontWeight.Medium)
                        Text(
                            "Automatically save document",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoSave,
                        onCheckedChange = {
                            autoSave = it
                            onAutoSaveChange(it)
                        }
                    )
                }
            }

            // Font Size
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Font Size", fontWeight = FontWeight.Medium)
                    Text(
                        "Current size: ${fontSize}sp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (fontSize > 8) {
                                    fontSize -= 2
                                    onFontSizeChange(fontSize)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("−")
                        }
                        Button(
                            onClick = {
                                if (fontSize < 40) {
                                    fontSize += 2
                                    onFontSizeChange(fontSize)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }
}