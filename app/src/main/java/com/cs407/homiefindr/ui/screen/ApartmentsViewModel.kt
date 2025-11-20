package com.cs407.homiefindr.ui.screen


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.model.ApartmentPost
import com.cs407.homiefindr.data.repository.FirestoreRepository

data class ApartmentsUiState(
    val posts: List<ApartmentPost> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ApartmentsViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    var uiState by mutableStateOf(ApartmentsUiState())
        private set

    init {
        repo.observeApartmentPosts(
            onSuccess = { posts ->
                uiState = uiState.copy(
                    posts = posts,
                    isLoading = false,
                    errorMessage = null
                )
            },
            onError = { e ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        )
    }
}
