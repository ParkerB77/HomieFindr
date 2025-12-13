// com/cs407/homiefindr/ui/screen/ApartmentsViewModel.kt
package com.cs407.homiefindr.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.model.ApartmentPost
import com.cs407.homiefindr.data.repository.FirestoreRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ApartmentsUiState(
    val posts: List<ApartmentPost> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // for filter
    val searchQuery: String = "",
    val minPrice: String = "",
    val maxPrice: String = "",
    val leaseStartDateMillis: Long? = null,
    val leaseEndDateMillis: Long? = null,
//    val forMale: Boolean = false,
//    val forFemale: Boolean = false,
//    val petsAllowed: Boolean = false
) {
    val filteredPosts: List<ApartmentPost>
        get() {
            var currentPosts = posts

            if (searchQuery.isNotBlank()) {
                currentPosts = currentPosts.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true) ||
                            (it.leasePeriod.contains(searchQuery, ignoreCase = true))
                }
            }

            val minPriceInt = minPrice.toIntOrNull()
            val maxPriceInt = maxPrice.toIntOrNull()

            if (minPriceInt != null) {
                currentPosts = currentPosts.filter { it.price >= minPriceInt }
            }
            if (maxPriceInt != null) {
                currentPosts = currentPosts.filter { it.price <= maxPriceInt }
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
            /*
            if (filterStartDate != null || filterEndDate != null) {
                currentPosts = currentPosts.filter { post ->
                    val parts = post.leasePeriod.split("-")
                    val postStartDateString = parts.getOrNull(0)
                    val postEndDateString = parts.getOrNull(1)

                    val postStartDate = postStartDateString?.let {
                        try {
                            dateFormat.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val postEndDate = postEndDateString?.let {
                        try {
                            dateFormat.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    var matchesDates = true
                    if (filterStartDate != null) {
                        matchesDates =
                            matchesDates && (postStartDate != null && !postStartDate.before(
                                filterStartDate
                            ))
                    }
                    if (filterEndDate != null) {
                        matchesDates =
                            matchesDates && (postEndDate != null && !postEndDate.after(filterEndDate))
                    }
                    matchesDates
                }
            }
            // gender, pets options ignored for now
            return currentPosts
            */
        }
}

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

    fun deletePost(postId: String) {
        repo.deleteApartmentPost(postId) { ok, e ->
            if (!ok) {
                uiState = uiState.copy(errorMessage = e?.message)
            }
        }
    }
}
