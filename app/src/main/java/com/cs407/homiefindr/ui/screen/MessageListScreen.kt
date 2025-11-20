package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp              // ★ new
import com.google.firebase.auth.ktx.auth        // ★ new
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query      // ★ new
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant

data class Conversation(
    val id: String,
    val title: String,
    val lastMsg: String,
    val time: Instant = Instant.now()
)

// ★ removed demoConversations; we now load from Firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesListScreen(onOpenChat: (String) -> Unit) {
    val db = remember { Firebase.firestore }                // ★ Firestore instance
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""// ★ logged-in user

    var list by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    // ★ Listen to all conversations that include the current user in "members" array
    DisposableEffect(currentUserId) {
        if (currentUserId.isBlank()) {
            error = "Not signed in"
            onDispose { }
        } else {
            val registration = db.collection("conversations")
                .whereArrayContains("members", currentUserId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        error = e.message
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        list = snapshot.documents.map { doc ->
                            doc.toConversation()
                        }
                    }
                }

            onDispose {
                registration.remove()
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = { Text("Messages") })

        // ★ Debug 当前 uid
        Text(
            text = "uid = $currentUserId",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

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
                        .clickable { onOpenChat(c.id) }   // ★ open chat with this conversation id
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

// ★ Helper extension to map Firestore document → Conversation data class
private fun DocumentSnapshot.toConversation(): Conversation {
    val id = this.id
    val title = getString("title") ?: "Chat"
    val lastMsg = getString("lastMsg") ?: ""
    val ts: Timestamp? = getTimestamp("updatedAt")
    val instant = ts?.toDate()?.toInstant() ?: Instant.EPOCH
    return Conversation(
        id = id,
        title = title,
        lastMsg = lastMsg,
        time = instant
    )
}
