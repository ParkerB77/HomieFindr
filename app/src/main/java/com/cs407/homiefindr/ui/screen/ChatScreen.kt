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
import com.google.firebase.Timestamp                        // ★ new
import com.google.firebase.auth.ktx.auth                    // ★ new
import com.google.firebase.firestore.FieldValue             // ★ new
import com.google.firebase.firestore.Query                 // ★ new
import com.google.firebase.firestore.SetOptions            // ★ new
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    chatId: String,            // this is the conversation document id
    onBack: () -> Unit
) {
    val db = remember { Firebase.firestore }               // ★ Firestore
    val currentUserId = Firebase.auth.currentUser?.uid ?: "" // ★ who am I

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var chatTitle by remember { mutableStateOf("Chat") }
    var otherUserId by remember { mutableStateOf("") }

    // ★ Listen to messages under conversations/{chatId}/messages in realtime
    DisposableEffect(chatId, currentUserId) {
        val registration = db.collection("conversations")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    messages = snapshot.documents.map { doc ->
                        val senderId = doc.getString("senderId") ?: ""
                        val text = doc.getString("text") ?: ""
                        val ts: Timestamp? = doc.getTimestamp("createdAt")
                        val instant = ts?.toDate()?.toInstant() ?: Instant.EPOCH
                        Message(
                            id = doc.id,
                            sender = senderId,
                            text = text,
                            time = instant,
                            mine = senderId == currentUserId
                        )
                    }
                }
            }

        onDispose {
            registration.remove()
        }
    }

    LaunchedEffect(chatId, currentUserId) {
        if (currentUserId.isBlank()) return@LaunchedEffect

        val convoRef = db.collection("conversations").document(chatId)
        convoRef.get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val members = doc.get("members") as? List<*> ?: emptyList<Any>()
                    val other = members
                        .mapNotNull { it as? String }
                        .firstOrNull { it != currentUserId }
                        ?: ""

                    // 写回到外面的 state 变量
                    otherUserId = other
                }
            }
    }
    DisposableEffect(otherUserId) {
        if (otherUserId.isBlank()) {
            onDispose { }
        } else {
            val reg = db.collection("users")
                .document(otherUserId)
                .addSnapshotListener { snap, e ->
                    if (e != null) return@addSnapshotListener
                    if (snap != null && snap.exists()) {
                        val name = snap.getString("name") ?: otherUserId
                        chatTitle = name
                    }
                }

            onDispose {
                reg.remove()
            }
        }
    }


        // auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }


    // ★ helper function to send a message to Firestore
    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || currentUserId.isBlank()) return

        val conversationRef = db.collection("conversations").document(chatId)

        // add message document
        val msgData = mapOf(
            "senderId" to currentUserId,
            "text" to trimmed,
            "createdAt" to FieldValue.serverTimestamp()
        )

        conversationRef.collection("messages").add(msgData)

        // update conversation metadata (for list screen)
        val convoUpdate = mapOf(
            "lastMsg" to trimmed,
            "updatedAt" to FieldValue.serverTimestamp()
            // You can also set "title" and "members" when creating the conversation
        )
        conversationRef.set(convoUpdate, SetOptions.merge())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(chatTitle) }, // you can later replace with conversation title
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar { text ->
                scope.launch {
                    sendMessage(text)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages, key = { it.id }) { m ->
                ChatBubble(m)
            }
        }
    }
}

@Composable
private fun ChatBubble(m: Message) {
    val bg = if (m.mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (m.mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (m.mine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(color = bg, contentColor = fg, shape = shape, tonalElevation = 1.dp) {
            Text(
                text = m.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
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
