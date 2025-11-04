package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant

data class Conversation(
    val id: String,
    val title: String,
    val lastMsg: String,
    val time: Instant = Instant.now()
)

private val demoConversations = listOf(
    Conversation("bob",   "Bob",   "Yep, that works for me…"),
    Conversation("adam",  "Adam",  "No messages yet"),
    Conversation("group1","Bucky, James, Sara", "anyone?")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesListScreen(onOpenChat: (String) -> Unit) {
    var list by remember { mutableStateOf(demoConversations) }

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = { Text("Messages") })
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list) { c ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(c.id) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(c.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(c.lastMsg, maxLines = 1)
                    }
                }
            }
        }
    }
}
