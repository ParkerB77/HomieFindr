package com.cs407.homiefindr.ui.screen

// ★ KTX 版本 Firestore 写法
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

//data class ProfileUiState(
//    val name: String = "Name",
//    val bio: String = "",
//    val avatarUri: Uri? = null
//)


@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToFavorites: () -> Unit,   // ← add this
) {
    val state by profileViewModel.uiState.collectAsState()

    var editing by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        profileViewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

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
        return
    }

    if (state.errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: ${state.errorMessage}")
        }
        return
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

        // Favorites button → navigate to FavoriteScreen
        FloatingActionButton(
            onClick = { onNavigateToFavorites() },   // ← call the lambda
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
                profileViewModel.signOut()
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
