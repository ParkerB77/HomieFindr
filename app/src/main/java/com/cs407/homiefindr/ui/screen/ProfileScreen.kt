package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel

// ★ KTX 版本 Firestore 写法
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//data class ProfileUiState(
//    val name: String = "Name",
//    val bio: String = "",
//    val avatarUri: Uri? = null
//)

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state by profileViewModel.uiState.collectAsState()

    var editing by remember { mutableStateOf(false) }

//    // ★ KTX：直接用 Firebase.firestore
//    val db = remember { Firebase.firestore }
//
//    // ---------- 第一次进入，用 userId 从 Firestore 读 profile ----------
//    LaunchedEffect(userId) {
//        if (userId.isNotBlank()) {
//            db.collection("profiles")
//                .document(userId)
//                .get()
//                .addOnSuccessListener { doc: DocumentSnapshot ->
//                    if (doc.exists()) {
//                        val name = doc.getString("name") ?: "Jason"
//                        val bio = doc.getString("bio") ?: ""
//                        val avatarUrl = doc.getString("avatarUrl")
//
//                        state = state.copy(
//                            name = name,
//                            bio = bio,
//                            avatarUri = avatarUrl?.let { Uri.parse(it) }
//                        )
//                    }
//                }
//        }
//    }
//
//    // ---------- 点击 Done 时：把当前资料写回 Firestore ----------
//
//    fun saveProfileToFirestore() {
//        if (userId.isBlank()) return
//
//        val data = hashMapOf(
//            "name" to state.name,
//            "bio" to state.bio,
//            "avatarUrl" to state.avatarUri?.toString()
//        )
//
//        db.collection("profiles")
//            .document(userId)
//            .set(data)
//    }
//
//    // ---------- 删除 profile 文档 ----------
//    fun deleteProfileFromFirestore() {
//        if (userId.isBlank()) return
//
//        db.collection("profiles")
//            .document(userId)
//            .delete()
//            .addOnSuccessListener {
//                // when profile is really gone, trigger logout + nav
//                onDeleteAndLogout()
//            }
//            .addOnFailureListener { e ->
//                // optional: show error somewhere
//                println("Delete failed: ${e.message}")
//            }
//    }
//
//
//    // ---------- 选头像 ----------

    val pickAvatar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            profileViewModel.updateAvatar(uri)
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return // stops updating ui when loading
    }

    if (state.errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: ${state.errorMessage}")
        }
        return // stops updating ui when there's an error
    }

    // --------------------- Layout ---------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        // for user to go into their favorite
        FloatingActionButton(
            onClick = {
                // TODO: go to FavoriteScreen
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite"
                )
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // avatar
        if (state.avatarUri != null) {
            AsyncImage(
                model = state.avatarUri,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Avatar",
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // name
        if (editing) {
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    profileViewModel.onNameChange(it)
                },
                label = { Text("Name") }
            )
        } else {
            Text(
                text = state.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = {
                if (editing) {
                profileViewModel.saveProfile()
                }
                editing = !editing
            }) {

                Text(if (editing) "Save" else "Edit")
            }
            Button(onClick = {
                // TODO: profileViewModel.signOut()
            }) {
                Text("Sign-out")
            }
        }

        if (editing) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    pickAvatar.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("Change Avatar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Description:", fontWeight = FontWeight.SemiBold)

        if (editing) {
            OutlinedTextField(
                value = state.bio,
                onValueChange = {
                    profileViewModel.onBioChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = { Text("Write something…") }
            )
        } else {
            Text(
                text = if (state.bio.isBlank()) "//bio" else state.bio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                fontSize = 16.sp
            )
        }
    }
}
