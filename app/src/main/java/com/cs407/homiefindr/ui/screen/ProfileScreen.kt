package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


data class ProfileUiState(
    val name: String = "Jason",
    val bio: String = "",
    val avatarUri: Uri? = null
)

@Composable
fun ProfileScreen() {
    var state by remember { mutableStateOf(ProfileUiState()) }
    var editing by remember { mutableStateOf(false) }

    // 系统相册选择器（Android 13+ 无需读存储权限；更低版本也能回落工作）
    val pickAvatar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) state = state.copy(avatarUri = uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像（优先显示选择的 Uri）
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
                contentDescription = "Avatar",
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // 名字：编辑/只读两种状态
        if (editing) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { state = state.copy(name = it) },
                label = { Text("Name") }
            )
        } else {
            Text(state.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { editing = !editing }) {
                Text(if (editing) "Done" else "Edit")
            }
            Button(onClick = { /* TODO: Sign-out */ }) {
                Text("Sign-out")
            }
        }

        if (editing) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    pickAvatar.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) { Text("Change Avatar") }
        }

        Spacer(Modifier.height(24.dp))

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
