package com.example.texteditor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onReadOnlyChange: (Boolean) -> Unit = {},
    onWordWrapChange: (Boolean) -> Unit = {}
) {
    var isReadOnly by remember { mutableStateOf(false) }
    var isWordWrap by remember { mutableStateOf(true) }
    var fontSize by remember { mutableStateOf(16) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "⚙️ Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("⬅ Back", fontSize = 16.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ===== READ ONLY =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                        Text(
                            text = "🔒 Read Only Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Prevent accidental edits to files",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isReadOnly,
                        onCheckedChange = {
                            isReadOnly = it
                            onReadOnlyChange(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            checkedTrackColor = Color(0xFF81C784)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ===== WORD WRAP =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                        Text(
                            text = "📝 Word Wrap",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Wrap long lines automatically",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isWordWrap,
                        onCheckedChange = {
                            isWordWrap = it
                            onWordWrapChange(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF2196F3),
                            checkedTrackColor = Color(0xFF64B5F6)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ===== FONT SIZE =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📐 Font Size: ${fontSize}sp",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Adjust text size in editor",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { if (fontSize > 10) fontSize -= 2 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5722)
                            )
                        ) {
                            Text("A-", fontSize = 20.sp)
                        }
                        Button(
                            onClick = { if (fontSize < 30) fontSize += 2 },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("A+", fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== APP INFO =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📱 Text Editor v1.0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "IS2205 Mobile Application Design and Development",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Mini-Project - Member 2",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}