package com.cs407.homiefindr.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.cs407.homiefindr.data.model.UserProfile

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()) {
        return EmailResult.Empty
    }

    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    return if (pattern.matches(email)) EmailResult.Valid else EmailResult.Invalid
}

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun checkPassword(password: String): PasswordResult {
    if (password.isEmpty()) return PasswordResult.Empty
    if (password.length < 6) return PasswordResult.Short

    val hasDigit = Regex("\\d").containsMatchIn(password)
    val hasLower = Regex("[a-z]").containsMatchIn(password)
    val hasUpper = Regex("[A-Z]").containsMatchIn(password)

    return if (hasDigit && hasLower && hasUpper) {
        PasswordResult.Valid
    } else {
        PasswordResult.Invalid
    }
}

fun signIn(
    email: String,
    password: String,
    onOk: () -> Unit,
    onErr: (String) -> Unit
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onOk()
            } else {
                // if sign-in fails, create account
                createAccount(email, password, onOk, onErr)
            }
        }
}

fun createAccount(
    email: String,
    password: String,
    onOk: () -> Unit,
    onErr: (String) -> Unit
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid ?: run {
                    onErr("User ID missing")
                    return@addOnCompleteListener
                }

                val profile = UserProfile(
                    uid = uid,
                    name = user.displayName ?: "",
                    email = email
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .document(uid)
                    .set(profile)
                    .addOnSuccessListener { onOk() }
                    .addOnFailureListener { e ->
                        onErr(e.message ?: "Failed to save user profile")
                    }
            } else {
                onErr(task.exception?.message ?: "Authentication failed")
            }
        }
}

fun updateName(
    name: String,
    onOk: () -> Unit = {},
    onErr: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser ?: return onErr("No signed-in user")

    val req = userProfileChangeRequest { displayName = name }
    user.updateProfile(req)
        .addOnSuccessListener { onOk() }
        .addOnFailureListener { e -> onErr(e.message ?: "Update profile failed") }
}
