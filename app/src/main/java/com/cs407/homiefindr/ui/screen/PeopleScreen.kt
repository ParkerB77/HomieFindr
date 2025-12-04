package com.cs407.homiefindr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun PeopleScreen(
    onClickPerson: (String) -> Unit,
    onClickAdd: () -> Unit,
    vm: PeopleViewModel = viewModel()
) {
    val state = vm.uiState
    val posts = state.posts
    val db = remember { Firebase.firestore }
    val currentUser = Firebase.auth.currentUser?.uid ?: ""
    val context = LocalContext.current

    var search: String by remember { mutableStateOf("") }
    var galleryImages by remember { mutableStateOf<List<String>?>(null) }

    val filteredPosts =
        if (search.isBlank()) posts
        else posts.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.bio.contains(search, ignoreCase = true)
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // search bar + filter
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
                label = { Text("search") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { /* TODO filters */ }) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }

        // list of people posts
        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            items(filteredPosts) { post ->
                PersonCard(
                    post = post,
                    db = db,
                    currentUser = currentUser,
                    onShowImages = { galleryImages = it },
                    onShowToast = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onClickPerson = onClickPerson,
                    onDeleted = { deletedId ->
                        vm.removePostFromState(deletedId)
                    }
                )
            }
        }

        // add button
        IconButton(
            onClick = onClickAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "add button",
                modifier = Modifier.size(40.dp)
            )
        }

        // dialog to show all photos for a person post
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
}

@Composable
private fun PersonCard(
    post: Post,
    db: FirebaseFirestore,
    currentUser: String,
    onShowImages: (List<String>) -> Unit,
    onShowToast: (String) -> Unit,
    onClickPerson: (String) -> Unit,
    onDeleted: (String) -> Unit
) {
    // START AS NOT SAVED
    var isSaved by remember { mutableStateOf(false) }
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
                                if (post.imageUrls.isNotEmpty()) onShowImages(post.imageUrls)
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
                            onClickPerson(post.creatorId)
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

            // action row: delete (if owner) + save
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
                                    onDeleted(post.postId)      // remove from UI state
                                    onShowToast("Deleted")
                                }
                                .addOnFailureListener {
                                    onShowToast("Delete failed")
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

                IconButton(onClick = {
                    isSaved = !isSaved
                    onShowToast(if (isSaved) "Saved" else "Removed from saved")
                }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Saved"
                    )
                }
            }
        }
    }
}
