package com.example.texteditor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onOpenEditor: () -> Unit,
    onOpenFile: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Text Editor",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(45.dp)
        )

        Button(
            onClick = onOpenEditor,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B35D5)
            )
        ) {

            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "Open Text Editor"
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Open Text Editor",
                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedButton(
            onClick = onOpenFile,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {

            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = "Open File"
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Open File",
                fontSize = 17.sp
            )
        }
    }
}