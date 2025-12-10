package com.cs407.homiefindr.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.model.Post
import com.cs407.homiefindr.data.repository.FirestoreRepository
//import com.google.type.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


data class PeopleUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // for filter
    val searchQuery: String = "",
    val minPrice: String = "",
    val maxPrice: String = "",
    val leaseStartDateMillis: Long? = null,
    val leaseEndDateMillis: Long? = null
) {
    val filteredPosts: List<Post>
        get() {
            var currentPosts = posts

            if (searchQuery.isNotBlank()) {
                currentPosts = currentPosts.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.bio.contains(searchQuery, ignoreCase = true) ||
                            it.leaseStartDate?.contains(searchQuery, ignoreCase = true) == true ||
                            it.leaseEndDate?.contains(searchQuery, ignoreCase = true) == true
                }
            }

            val filterMinPriceInt = minPrice.toIntOrNull()
            val filterMaxPriceInt = maxPrice.toIntOrNull()

            if (filterMinPriceInt != null || filterMaxPriceInt != null) {
                currentPosts = currentPosts.filter { post ->
                    val postMin = post.priceMin ?: Int.MIN_VALUE // Treat null as no lower bound
                    val postMax = post.priceMax ?: Int.MAX_VALUE // Treat null as no upper bound

                    val actualFilterMin = filterMinPriceInt ?: Int.MIN_VALUE
                    val actualFilterMax = filterMaxPriceInt ?: Int.MAX_VALUE

                    // Check for overlap: [postMin, postMax] overlaps [actualFilterMin, actualFilterMax]
                    (postMax >= actualFilterMin && postMin <= actualFilterMax)
                }
            }

            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())

            val filterStartDate = leaseStartDateMillis?.let { Date(it) }
            val filterEndDate = leaseEndDateMillis?.let { Date(it) }

            if (filterStartDate != null) {
                currentPosts = currentPosts.filter { post ->
                    post.leaseStartDate?.let { dateString ->
                        try {
                            val postDate = dateFormat.parse(dateString)
                            postDate != null && !postDate.before(filterStartDate) // Post's start date is on or after filter's start date
                        } catch (e: Exception) {
                            false // Cannot parse, exclude from filter
                        }
                    } ?: false // If post has no start date, it doesn't match a specific filterStartDate
                }
            }

            if (filterEndDate != null) {
                currentPosts = currentPosts.filter { post ->
                    post.leaseEndDate?.let { dateString ->
                        try {
                            val postDate = dateFormat.parse(dateString)
                            postDate != null && !postDate.after(filterEndDate) // Post's end date is on or before filter's end date
                        } catch (e: Exception) {
                            false // Cannot parse, exclude from filter
                        }
                    } ?: false // If post has no end date, it doesn't match a specific filterEndDate
                }
            }
            // gender, pets options ignored for now
            return currentPosts
        }
}

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

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun updateFilters(
        minPrice: String,
        maxPrice: String,
        leaseStartDateMillis: Long?,
        leaseEndDateMillis: Long?
    ) {
        uiState = uiState.copy(
            minPrice = minPrice,
            maxPrice = maxPrice,
            leaseStartDateMillis = leaseStartDateMillis,
            leaseEndDateMillis = leaseEndDateMillis
        )
    }

    // ← NEW: call this after a successful Firestore delete
    fun removePostFromState(postId: String) {
        uiState = uiState.copy(
            posts = uiState.posts.filterNot { it.postId == postId }
        )
    }
}
