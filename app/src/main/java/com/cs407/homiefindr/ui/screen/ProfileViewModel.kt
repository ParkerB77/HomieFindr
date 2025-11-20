package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.homiefindr.data.model.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri
import com.google.firebase.storage.ktx.storage

data class ProfileUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val bio: String = "",
    val avatarUri: Uri? = null,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
                return@launch
            }

            try {
                val userDocRef = firestore.collection("users").document(currentUser.uid)
                val document = userDocRef.get().await()
                val userProfile: UserProfile


                if (document.exists()) {
                    userProfile = document.toObject<UserProfile>()!!
                } else {
                    userProfile = UserProfile(
                        uid = currentUser.uid,
                        name = currentUser.displayName ?: "New Homie",
                        email = currentUser.email ?: ""
                    )
                    // saves the profile
                    userDocRef.set(userProfile).await()
                }

                 _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = userProfile.name,
                        bio = userProfile.bio,
                        avatarUri = if (userProfile.profileImageUrl.isNotBlank()) {
                            userProfile.profileImageUrl.toUri()
                        } else {
                            null
                        }
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }
    fun onBioChange(newBio: String) {
        _uiState.update {
            it.copy(bio = newBio)
        }
    }
    fun updateAvatar(newAvatarUri: Uri) {
        _uiState.update {
            it.copy(avatarUri = newAvatarUri)
        }
    }
    // save to firebase
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
                return@launch
            }
            try {
                val newAvatarUri = uiState.value.avatarUri
                var newImageUrl = ""

                if (newAvatarUri != null && newAvatarUri.scheme?.startsWith("content") == true) {
                    val avatarRef = storage.reference.child("avatars/${currentUser.uid}")

                    avatarRef.putFile(newAvatarUri).await()
                    newImageUrl = avatarRef.downloadUrl.await().toString()
                }
                val userDocRef = firestore.collection("users").document(currentUser.uid)
                userDocRef.firestore.collection("users").document(currentUser.uid)
                val updates = mutableMapOf<String, Any>(
                    "name" to uiState.value.name,
                    "bio" to uiState.value.bio,
                )
                if (newImageUrl.isNotBlank()) {
                    updates["profileImageUrl"] = newImageUrl
                }
                userDocRef.update(updates).await()
                if (auth.currentUser?.displayName != uiState.value.name) {
                    com.cs407.homiefindr.data.auth.updateName(uiState.value.name)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    // TODO: signOut()
}