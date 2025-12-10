package com.cs407.homiefindr.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.model.ApartmentPost
import com.cs407.homiefindr.data.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class FavoriteUiState(
    val apartmentPosts: List<ApartmentPost> = emptyList(),
    val peoplePosts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class FavoriteViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser?.uid ?: ""

    var uiState by mutableStateOf(FavoriteUiState())
        private set

    init {
        subscribeToFavorites()
    }

    private fun subscribeToFavorites() {
        if (currentUser.isBlank()) {
            uiState = FavoriteUiState(
                apartmentPosts = emptyList(),
                peoplePosts = emptyList(),
                isLoading = false,
                errorMessage = "Not logged in"
            )
            return
        }

        subscribeToApartmentFavorites()
        subscribeToPeopleFavorites()
    }

    private fun subscribeToApartmentFavorites() {
        db.collection("users")
            .document(currentUser)
            .collection("favoriteApartments")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                    return@addSnapshotListener
                }

                val posts = snapshot?.toObjects(ApartmentPost::class.java) ?: emptyList()
                uiState = uiState.copy(
                    apartmentPosts = posts,
                    isLoading = false,
                    errorMessage = null
                )
            }
    }

    private fun subscribeToPeopleFavorites() {
        db.collection("users")
            .document(currentUser)
            .collection("favoritePeoplePosts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                    return@addSnapshotListener
                }

                val peopleFavs = snapshot?.toObjects(Post::class.java) ?: emptyList()
                uiState = uiState.copy(
                    peoplePosts = peopleFavs,
                    isLoading = false,
                    errorMessage = null
                )
            }
    }
}
