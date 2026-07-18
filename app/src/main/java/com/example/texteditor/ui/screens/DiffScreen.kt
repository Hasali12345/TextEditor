package com.example.texteditor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(
    currentText: String,
    versionText: String,
    onBackClick: () -> Unit = {}
) {
    val currentLines = currentText.split("\n")
    val versionLines = versionText.split("\n")

    val diffResult = remember(currentText, versionText) {
        val result = mutableListOf<DiffLine>()
        val maxLines = maxOf(currentLines.size, versionLines.size)

        for (i in 0 until maxLines) {
            val current = if (i < currentLines.size) currentLines[i] else ""
            val version = if (i < versionLines.size) versionLines[i] else ""

            when {
                current == version && current.isNotEmpty() -> {
                    result.add(DiffLine("same", current, ""))
                }
                current != version && current.isNotEmpty() && version.isNotEmpty() -> {
                    result.add(DiffLine("removed", "", version))
                    result.add(DiffLine("added", current, ""))
                }
                current.isEmpty() && version.isNotEmpty() -> {
                    result.add(DiffLine("removed", "", version))
                }
                current.isNotEmpty() && version.isEmpty() -> {
                    result.add(DiffLine("added", current, ""))
                }
            }
        }
        result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diff View", fontWeight = FontWeight.Bold) },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF4CAF50))
                    )
                    Text("Added", fontSize = 12.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFF44336))
                    )
                    Text("Removed", fontSize = 12.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                    Text("Same", fontSize = 12.sp)
                }
            }

            Divider()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(diffResult) { diffLine ->
                    when (diffLine.type) {
                        "added" -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFC8E6C9)
                                )
                            ) {
                                Text(
                                    text = "+ ${diffLine.currentText}",
                                    modifier = Modifier.padding(8.dp),
                                    color = Color(0xFF2E7D32),
                                    fontSize = 14.sp
                                )
                            }
                        }
                        "removed" -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFCDD2)
                                )
                            ) {
                                Text(
                                    text = "- ${diffLine.versionText}",
                                    modifier = Modifier.padding(8.dp),
                                    color = Color(0xFFC62828),
                                    fontSize = 14.sp
                                )
                            }
                        }
                        else -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                )
                            ) {
                                Text(
                                    text = "  ${diffLine.currentText}",
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class DiffLine(
    val type: String,
    val currentText: String,
    val versionText: String
)