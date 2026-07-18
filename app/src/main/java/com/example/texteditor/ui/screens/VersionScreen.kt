package com.example.texteditor.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.texteditor.database.AppDatabase
import com.example.texteditor.database.VersionEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionScreen(
    fileName: String = "Untitled.txt",
    onBackClick: () -> Unit = {},
h    onRollbackClick: (String) -> Unit = { _ -> },
    onViewDiffClick: (String, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val versionDao = remember {
        AppDatabase.getDatabase(context).versionDao()
    }

    var versions by remember { mutableStateOf<List<VersionEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentText by remember { mutableStateOf("") }

    // Load versions from database
    LaunchedEffect(fileName) {
        isLoading = true
        versions = versionDao.getVersionsByFileName(fileName)
        isLoading = false

        // Get current text from the latest version
        versions.firstOrNull()?.let {
            currentText = it.diffText
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Version History", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", fontSize = 24.sp)
                    }
                },
                actions = {
                    if (versions.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    versions = versionDao.getVersionsByFileName(fileName)
                                    Toast.makeText(context, "✅ Refreshed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("🔄", fontSize = 20.sp)
                        }
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                versions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📄 No versions saved", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Save your document to create versions",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(versions) { version ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Version ${version.versionNumber}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                .format(version.date),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = version.diffText.take(100) + if (version.diffText.length > 100) "..." else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF4CAF50)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val fullVersion = versionDao.getVersionById(version.id)
                                                        fullVersion?.let {
                                                            onViewDiffClick(currentText, it.diffText)
                                                            Toast.makeText(
                                                                context,
                                                                "🔍 Showing diff for Version ${it.versionNumber}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(
                                                            context,
                                                            "❌ Diff failed: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2196F3)
                                            )
                                        ) {
                                            Text("View Diff")
                                        }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val fullVersion = versionDao.getVersionById(version.id)
                                                        fullVersion?.let {
                                                            onRollbackClick(it.diffText)
                                                            Toast.makeText(
                                                                context,
                                                                "✅ Rolled back to Version ${it.versionNumber}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            onBackClick()
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(
                                                            context,
                                                            "❌ Rollback failed: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF9800)
                                            )
                                        ) {
                                            Text("Rollback")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}