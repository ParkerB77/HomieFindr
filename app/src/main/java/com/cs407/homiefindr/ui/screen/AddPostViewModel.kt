package com.cs407.homiefindr.ui.screen



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
        leasePeriod: String,
        onDone: (Boolean) -> Unit
    ) {
        isSaving = true
        errorMessage = null

        repo.addApartmentPost(
            title = title,
            content = content,
            price = price,
            leasePeriod = leasePeriod
        ) { ok, err ->
            isSaving = false
            errorMessage = err?.message
            onDone(ok)
        }
    }
}
