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

// ★ KTX 版本 Firestore 写法
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class ProfileUiState(
    val name: String = "Name",
    val bio: String = "",
    val avatarUri: Uri? = null
)

@Composable
fun ProfileScreen(
    userId: String        // 从导航传进来的 uid
) {
    var state by remember { mutableStateOf(ProfileUiState()) }
    var editing by remember { mutableStateOf(false) }

    // ★ KTX：直接用 Firebase.firestore
    val db = remember { Firebase.firestore }

    // ---------- 第一次进入，用 userId 从 Firestore 读 profile ----------
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            db.collection("profiles")
                .document(userId)
                .get()
                .addOnSuccessListener { doc: DocumentSnapshot ->
                    if (doc.exists()) {
                        val name = doc.getString("name") ?: "Name"
                        val bio = doc.getString("bio") ?: ""
                        val avatarUrl = doc.getString("avatarUrl")

                        state = state.copy(
                            name = name,
                            bio = bio,
                            avatarUri = avatarUrl?.let { Uri.parse(it) }
                        )
                    }
                }
        }
    }

    // ---------- 点击 Done 时：把当前资料写回 Firestore ----------
    fun saveProfileToFirestore() {
        if (userId.isBlank()) return

        val data = hashMapOf(
            "name" to state.name,
            "bio" to state.bio,
            "avatarUrl" to state.avatarUri?.toString()
        )

        db.collection("profiles")
            .document(userId)
            .set(data)
    }

    // ---------- 选头像 ----------
    val pickAvatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            state = state.copy(avatarUri = uri)
        }
    }

    // --------------------- Layout ---------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        FloatingActionButton(
            onClick = {
                // TODO: 之后可以跳转到 FavoriteScreen
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

        // 头像
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

        // 名字
        if (editing) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { state = state.copy(name = it) },
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
            OutlinedButton(
                onClick = {
                    if (editing) {
                        saveProfileToFirestore()
                    }
                    editing = !editing
                }
            ) {
                Text(if (editing) "Done" else "Edit")
            }

            // TODO: 这里以后可以加 Sign-out 按钮
        }

        if (editing) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    pickAvatarLauncher.launch(
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
                onValueChange = { state = state.copy(bio = it) },
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
