package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.homiefindr.data.model.ApartmentPost

@Composable
fun ApartmentsScreen(
    onClickAdd: () -> Unit,
    vm: ApartmentsViewModel = viewModel()
) {
    val state = vm.uiState
    val posts = state.posts

    var search: String by remember { mutableStateOf("") }

    // simple local search filter
    val filteredPosts =
        if (search.isBlank()) posts
        else posts.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.content.contains(search, ignoreCase = true) ||
                    it.leasePeriod.contains(search, ignoreCase = true)
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Top row with search bar + filter button (same as you had)
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

            IconButton(
                onClick = { /* TODO: filter posts more smartly */ }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }


        // List of posts (replaces hard-coded ids, keeps your card layout style)
        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            items(filteredPosts) { post ->
                ApartmentCard(post = post)
            }
        }
        // The + add button (bottom-right, same position)
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

    }
}

@Composable
private fun ApartmentCard(post: ApartmentPost) {
    var isSaved by remember { mutableStateOf(true) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Apartment picture + save button (same structure as before)
            Box(
                modifier = Modifier.size(90.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Apartment",
                    modifier = Modifier.size(90.dp)
                )

                IconButton(
                    onClick = {
                        // TODO: toggle save in database if you want
                        isSaved = !isSaved
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    if (isSaved) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Saved"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Not saved"
                        )
                    }
                }
            }

            // Apartment information (using real data now)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = post.title, fontSize = 18.sp)
                Text(text = post.leasePeriod)
                Text(text = "$${post.price}")
                Text(text = post.content)
            }
        }
    }
}
