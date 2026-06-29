package com.shitu.pickpic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var language by remember { mutableStateOf("中文") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ListItem(
                headlineContent = { Text("切换语言") },
                trailingContent = {
                    TextButton(onClick = { language = if (language == "中文") "English" else "中文" }) {
                        Text(language)
                    }
                }
            )
            Divider()
            ListItem(
                headlineContent = { Text("关于拾图") },
                supportingContent = { Text("版本 1.0.0") }
            )
        }
    }
}
