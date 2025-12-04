package com.cs407.homiefindr.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.model.Post
import com.cs407.homiefindr.data.repository.FirestoreRepository

data class PeopleUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class PeopleViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    var uiState by mutableStateOf(PeopleUiState())
        private set

    init {
        loadPeoplePosts()
    }

    private fun loadPeoplePosts() {
        repo.getAllPosts { posts ->
            uiState = uiState.copy(
                posts = posts,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    // ← NEW: call this after a successful Firestore delete
    fun removePostFromState(postId: String) {
        uiState = uiState.copy(
            posts = uiState.posts.filterNot { it.postId == postId }
        )
    }
}
