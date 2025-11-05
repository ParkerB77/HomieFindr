package com.cs407.homiefindr.data.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest



enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()){
        //TODO handle the case when email is empty
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
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty()) {
        //TODO when password is empty
        return PasswordResult.Empty
    }
    if (password.length < 6) {
        //TODO when password is short
        return PasswordResult.Short
    }
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
    val auth: FirebaseAuth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                onOk()
            } else {

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
    val auth: FirebaseAuth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 注册成功
                onOk()
            } else {
                onErr(task.exception?.message ?: "Authentication failed")
            }
        }
}


fun updateName(name: String, onOk: () -> Unit = {}, onErr: (String) -> Unit = {}) {
    val user = Firebase.auth.currentUser ?: return onErr("No signed-in user")
    val req = userProfileChangeRequest { displayName = name }
    user.updateProfile(req)
        .addOnSuccessListener { onOk() }
        .addOnFailureListener { e -> onErr(e.message ?: "Update profile failed") }
}