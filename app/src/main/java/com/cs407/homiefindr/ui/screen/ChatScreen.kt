package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val time: Instant = Instant.now(),
    val mine: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    onBack: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                Message(sender = "Bob", text = "Hi, I'm interested in renting.", mine = false),
                Message(sender = "Me",  text = "Great! When can you tour?",  mine = true)
            )
        )
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(chatId) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar { text ->
                val t = text.trim()
                if (t.isNotEmpty()) {
                    messages = messages + Message(sender = "Me", text = t, mine = true)
                    scope.launch { listState.animateScrollToItem(messages.lastIndex) }
                    // TODO: 发到后端；收到对方时 append mine=false 的 Message
                }
            }
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .imePadding()
                .navigationBarsPadding(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(messages, key = { it.id }) { m -> MessageBubble(m) }
        }
    }
}

@Composable
private fun MessageBubble(m: Message) {
    val shape = RoundedCornerShape(16.dp)
    val bg = if (m.mine) MaterialTheme.colorScheme.primaryContainer
    else       MaterialTheme.colorScheme.surfaceVariant
    val fg = if (m.mine) MaterialTheme.colorScheme.onPrimaryContainer
    else       MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(color = bg, contentColor = fg, shape = shape, tonalElevation = 1.dp) {
            Text(text = m.text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
        }
    }
}

@Composable
private fun ChatInputBar(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message…") },
            maxLines = 4
        )
        Spacer(Modifier.width(8.dp))
        FilledIconButton(onClick = {
            onSend(text)
            text = ""
        }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}
