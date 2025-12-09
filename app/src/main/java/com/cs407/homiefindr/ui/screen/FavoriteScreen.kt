package com.cs407.homiefindr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun FavoriteScreen(
    onOpenChat: (String) -> Unit,
    onOpenOwnerProfile: (String) -> Unit,
    vm: FavoriteViewModel = viewModel()
) {
    val state = vm.uiState
    val context = LocalContext.current
    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser?.uid ?: ""

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.errorMessage ?: "Error")
            }
        }

        state.posts.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No favorite apartments yet")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 100.dp
                )
            ) {
                items(state.posts) { post ->
                    // Reuse ApartmentCard from ApartmentsScreen.kt
                    ApartmentCard(
                        post = post,
                        openChat = onOpenChat,
                        db = db,
                        currentUser = currentUser,
                        onShowImages = { /* you can add a dialog if you want */ },
                        onShowToast = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onOpenOwnerProfile = onOpenOwnerProfile
                    )
                }
            }
        }
    }
}
