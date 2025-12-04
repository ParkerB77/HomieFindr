package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cs407.homiefindr.data.model.Post
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PeopleScreen(
    onClickPerson: () -> Unit,
    onClickAdd: () -> Unit,
    vm: PeopleViewModel = viewModel()
) {
    val state = vm.uiState
    val posts = state.posts
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var search by remember { mutableStateOf("") }
    var galleryImages by remember { mutableStateOf<List<String>?>(null) }

    val filteredPosts =
        if (search.isBlank()) posts
        else posts.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.bio.contains(search, ignoreCase = true) ||
                    (it.leaseStartDate ?: "").contains(search, ignoreCase = true) ||
                    (it.leaseEndDate ?: "").contains(search, ignoreCase = true)
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("search people") },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { /* future filters */ }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            items(filteredPosts) { post ->
                val canDelete = currentUserId != null && post.creatorId == currentUserId
                PersonPostCard(
                    post = post,
                    canDelete = canDelete,
                    onDelete = { vm.deletePost(post.postId) },
                    onClick = onClickPerson,
                    onImageClick = {
                        if (post.imageUrls.isNotEmpty()) {
                            galleryImages = post.imageUrls
                        }
                    }
                )
            }
        }

        IconButton(
            onClick = onClickAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "add person post",
                modifier = Modifier.size(40.dp)
            )
        }

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
                    LazyRow {
                        items(images) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun PersonPostCard(
    post: Post,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onImageClick: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clickable(enabled = post.imageUrls.isNotEmpty()) { onImageClick() }
                ) {
                    if (post.imageUrls.isNotEmpty()) {
                        AsyncImage(
                            model = post.imageUrls.first(),
                            contentDescription = "Profile image",
                            modifier = Modifier.size(90.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Person",
                            modifier = Modifier.size(90.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = post.title, fontSize = 18.sp)

                    val priceText = when {
                        post.priceMin != null && post.priceMax != null ->
                            "$${post.priceMin} - $${post.priceMax}"
                        post.priceMax != null -> "Up to $${post.priceMax}"
                        post.priceMin != null -> "From $${post.priceMin}"
                        else -> "Price not set"
                    }
                    Text(text = priceText)

                    val leaseText = listOfNotNull(post.leaseStartDate, post.leaseEndDate)
                        .joinToString(" - ")
                        .ifBlank { "Lease period not set" }
                    Text(text = leaseText)

                    Text(text = post.bio, maxLines = 2)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post",
                            tint = Color.Red
                        )
                    }
                }

                IconButton(onClick = { isSaved = !isSaved }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Saved"
                    )
                }
            }
        }
    }
}
