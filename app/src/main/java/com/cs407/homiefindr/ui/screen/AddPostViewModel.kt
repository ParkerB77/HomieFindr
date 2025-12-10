// com/cs407/homiefindr/ui/screen/AddPostViewModel.kt
package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cs407.homiefindr.data.repository.FirestoreRepository

class AddPostViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun saveApartmentPost(
        title: String,
        content: String,
        price: Int,
        leaseStartDate: String?,
        leaseEndDate: String?,
        imageUris: List<Uri>,
        onDone: (Boolean) -> Unit
    ) {
        isSaving = true
        errorMessage = null

        repo.addApartmentPost(
            title = title,
            content = content,
            price = price,
            leaseStartDate = leaseStartDate,
            leaseEndDate = leaseEndDate,
            imageUris = imageUris
        ) { ok, err ->
            isSaving = false
            errorMessage = err?.message
            onDone(ok)
        }
    }
}
