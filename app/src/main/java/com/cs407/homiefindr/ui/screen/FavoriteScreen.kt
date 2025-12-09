package com.cs407.homiefindr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cs407.homiefindr.data.model.Post
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

        state.apartmentPosts.isEmpty() && state.peoplePosts.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No favorite posts yet")
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
                // Favorite apartments
                if (state.apartmentPosts.isNotEmpty()) {
                    item {
                        Text(
                            text = "Favorite Apartments",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(state.apartmentPosts) { post ->
                        ApartmentCard(
                            post = post,
                            openChat = onOpenChat,
                            db = db,
                            currentUser = currentUser,
                            onShowImages = { /* add dialog if you want */ },
                            onShowToast = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onOpenOwnerProfile = onOpenOwnerProfile
                        )
                    }
                }

                // Favorite people posts
                if (state.peoplePosts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Favorite People",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(state.peoplePosts) { post ->
                        FavoritePersonCard(
                            post = post,
                            onOpenOwnerProfile = onOpenOwnerProfile
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritePersonCard(
    post: Post,
    onOpenOwnerProfile: (String) -> Unit
) {
    val db = remember { Firebase.firestore }
    val currentUser = Firebase.auth.currentUser?.uid ?: ""
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(true) }
    var galleryImages by remember { mutableStateOf<List<String>?>(null) }
    val canDelete = post.creatorId == currentUser

    val leaseText: String = when {
        !post.leaseStartDate.isNullOrBlank() && !post.leaseEndDate.isNullOrBlank() ->
            "${post.leaseStartDate} - ${post.leaseEndDate}"
        !post.leaseStartDate.isNullOrBlank() -> post.leaseStartDate!!
        !post.leaseEndDate.isNullOrBlank() -> post.leaseEndDate!!
        else -> ""
    }

    val priceText: String = when {
        post.priceMin != null && post.priceMax != null && post.priceMin != post.priceMax ->
            "${post.priceMin} - ${post.priceMax}"
        post.priceMin != null -> "${post.priceMin}"
        post.priceMax != null -> "${post.priceMax}"
        else -> "0"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: image + name under it
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(enabled = post.imageUrls.isNotEmpty()) {
                                if (post.imageUrls.isNotEmpty()) galleryImages = post.imageUrls
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val firstImage = post.imageUrls.firstOrNull()
                        if (firstImage != null) {
                            AsyncImage(
                                model = firstImage,
                                contentDescription = "Profile image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = post.title.ifBlank { "Name" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onOpenOwnerProfile(post.creatorId)
                        }
                    )
                }

                // RIGHT: "Requirements" + lease + price + bio
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = "Requirements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = leaseText,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Money Icon"
                        )
                        Text(
                            text = "$$priceText",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = post.bio)
                }
            }

            // action row: delete (if owner) + save + chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canDelete) {
                    IconButton(
                        onClick = {
                            db.collection("posts")
                                .document(post.postId)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post",
                            tint = Color.Red
                        )
                    }
                }

                // Remove from favorites button
                IconButton(onClick = {
                    val favDoc = db.collection("users")
                        .document(currentUser)
                        .collection("favoritePeoplePosts")
                        .document(post.postId)

                    favDoc.delete()
                        .addOnSuccessListener {
                            isSaved = false
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Remove failed", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite"
                    )
                }

                // Chat button
                IconButton(
                    onClick = {
                        onOpenOwnerProfile(post.creatorId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Chat Button"
                    )
                }
            }
        }
    }

    // Gallery dialog
    galleryImages?.let { images ->
        AlertDialog(
            onDismissRequest = { galleryImages = null },
            confirmButton = {
                TextButton(onClick = { galleryImages = null }) {
                    Text("Close")
                }
            },
            title = { Text("Photos") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(images) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(220.dp)
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        )
    }
}