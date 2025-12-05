package com.cs407.homiefindr.ui.screen

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// Generate one-to-one chat IDs: Ensure the order of the two uids remains fixed.
fun oneToOneChatId(uid1: String, uid2: String): String {
    return listOf(uid1, uid2).sorted().joinToString("_")
}

// Create/reuse a session, then return the chatId via callback.
fun startOrGetConversation(
    db: FirebaseFirestore,
    currentUserId: String,
    otherUserId: String,
    otherUserName: String? = null,
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    if (currentUserId.isBlank() || otherUserId.isBlank()) {
        onError("uid is blank")
        return
    }

    val chatId = oneToOneChatId(currentUserId, otherUserId)
    val convoRef = db.collection("conversations").document(chatId)

    val data = mutableMapOf<String, Any>(
        "members" to FieldValue.arrayUnion(currentUserId, otherUserId),
        "lastMsg" to "",
        "updatedAt" to Timestamp.now()
    )

    if (!otherUserName.isNullOrBlank()) {
        data["title"] = otherUserName
    }


    // merge = true: Update if already exists; create if not found.
    convoRef.set(data, SetOptions.merge())
        .addOnSuccessListener { onResult(chatId) }
        .addOnFailureListener { e -> onError(e.message ?: "unknown error") }
}
