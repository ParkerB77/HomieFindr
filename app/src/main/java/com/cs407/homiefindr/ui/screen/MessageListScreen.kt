package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import com.google.firebase.firestore.ListenerRegistration

data class Conversation(
    val id: String,
    val title: String,
    val lastMsg: String,
    val otherUserId: String,
    val time: Instant = Instant.now()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesListScreen(
    onOpenChat: (String) -> Unit
) {
    // Firestore 实例
    val db = remember { Firebase.firestore }
    // 当前登录用户 uid
    val currentUserId = Firebase.auth.currentUser?.uid ?: ""
    var nameCache by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    val profileListeners = remember { mutableMapOf<String, ListenerRegistration>() }
    var error by remember { mutableStateOf<String?>(null) }

    // ---------- 2. listen ----------
    DisposableEffect(currentUserId) {
        if (currentUserId.isBlank()) {
            error = "Not signed in"
            onDispose { }
        } else {
            val registration = db.collection("conversations")
                .whereArrayContains("members", currentUserId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        error = e.message
                        println("DEBUG listener error: ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        println("DEBUG listener size = ${snapshot.size()}")

                        val newConversations = snapshot.documents
                            .map { doc -> doc.toConversation(currentUserId) }
                            .sortedByDescending { it.time } // 可选：按时间倒序

                        conversations = newConversations

                        newConversations.forEach { conv ->
                            val otherId = conv.otherUserId
                            if (otherId.isNotBlank() && !profileListeners.containsKey(otherId)) {
                                val reg = db.collection("profiles")
                                    .document(otherId)
                                    .addSnapshotListener { snap, e ->
                                        if (e != null) return@addSnapshotListener
                                        if (snap != null && snap.exists()) {
                                            val name = snap.getString("name") ?: "User"
                                            // 更新 nameCache，驱动 UI 刷新
                                            nameCache = nameCache.toMutableMap().apply {
                                                put(otherId, name)
                                            }
                                        }
                                    }

                                profileListeners[otherId] = reg
                            }
                        }

                    }
                }

            onDispose {
                registration.remove()
                profileListeners.values.forEach { it.remove() }
                profileListeners.clear()
            }
        }
    }

    // --------------------- UI ---------------------
    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = { Text("Messages") })

        // Debug
        Text(
            text = "uid = $currentUserId",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "convs = ${conversations.size}",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (conversations.isEmpty() && error == null) {
            Text(
                text = "No conversations yet",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversations) { c ->

                val displayTitle = nameCache[c.otherUserId] ?: c.title

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(c.id) }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(displayTitle, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(c.lastMsg, maxLines = 1)
                    }
                }
            }
        }
    }
}

// ---------- Firestore Document -> Conversation ----------
private fun DocumentSnapshot.toConversation(currentUserId: String): Conversation {
    val id = this.id
    //val title = getString("title") ?: "Chat"
    val lastMsg = getString("lastMsg") ?: ""
    val ts: Timestamp? = getTimestamp("updatedAt")
    val instant = ts?.toDate()?.toInstant() ?: Instant.EPOCH

    val members = get("members") as? List<*> ?: emptyList<Any>()
    val otherUserId = members
        .mapNotNull { it as? String }
        .firstOrNull { it != currentUserId }
        .orEmpty()

    val fallbackTitle = otherUserId.ifBlank { "Chat" }

    return Conversation(
        id = id,
        title = fallbackTitle,
        lastMsg = lastMsg,
        otherUserId = otherUserId,
        time = instant
    )
}
