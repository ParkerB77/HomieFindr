// com/cs407/homiefindr/ui/screen/AddPeopleViewModel.kt
package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.repository.FirestoreRepository

class AddPeopleViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun savePeoplePost(
        name: String,
        bio: String,
        maxPrice: Int?,
        leasePeriod: String,
        imageUris: List<Uri>,
        onDone: (Boolean) -> Unit
    ) {
        isSaving = true
        errorMessage = null

        repo.createPost(
            title = name,
            bio = bio,
            priceMin = null,
            priceMax = maxPrice,
            leaseStartDate = leasePeriod,
            leaseEndDate = null,
            imageUris = imageUris
        ) { ok ->
            isSaving = false
            if (!ok) errorMessage = "Failed to save people post"
            onDone(ok)
        }
    }
}
