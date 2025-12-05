package com.cs407.homiefindr.ui.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.net.toUri
import com.cs407.homiefindr.data.model.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}

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

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadUserProfile()
    }

    /**
     * 从 Firestore 加载用户资料：
     * - 集合：users
     * - 文档 ID：当前用户 uid
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User not logged in."
                    )
                }
                return@launch
            }

            try {
                val userDocRef = firestore.collection("users").document(currentUser.uid)
                val document = userDocRef.get().await()

                val userProfile: UserProfile = if (document.exists()) {
                    document.toObject<UserProfile>()!!
                } else {
                    // 如果还没有文档，就用当前账号信息创建一份
                    UserProfile(
                        uid = currentUser.uid,
                        name = currentUser.displayName ?: "New Homie",
                        email = currentUser.email ?: ""
                    ).also { profile ->
                        userDocRef.set(profile).await()
                    }
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
                        },
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /** 本地修改 name */
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    /** 本地修改 bio */
    fun onBioChange(newBio: String) {
        _uiState.update { it.copy(bio = newBio) }
    }

    /** 本地修改头像 Uri */
    fun updateAvatar(newAvatarUri: Uri) {
        _uiState.update { it.copy(avatarUri = newAvatarUri) }
    }

    /**
     * 保存资料到 Firebase：
     * - 如有新头像：上传到 Storage -> 拿到 url
     * - 更新 Firestore: users/{uid}.name / bio / profileImageUrl
     * - 同步 FirebaseAuth displayName（保留你原来的逻辑）
     */
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User not logged in."
                    )
                }
                return@launch
            }

            try {
                val state = uiState.value
                val userDocRef = firestore.collection("users").document(currentUser.uid)

                var newImageUrl = ""
                val newAvatarUri = state.avatarUri

                // 只有是本地 content:// 才需要上传；如果你以后存的是 http url，这里就不会重复上传
                if (newAvatarUri != null && newAvatarUri.scheme?.startsWith("content") == true) {
                    val avatarRef = storage.reference.child("avatars/${currentUser.uid}")
                    avatarRef.putFile(newAvatarUri).await()
                    newImageUrl = avatarRef.downloadUrl.await().toString()
                }

                val updates = mutableMapOf<String, Any>(
                    "name" to state.name,
                    "bio" to state.bio
                )
                if (newImageUrl.isNotBlank()) {
                    updates["profileImageUrl"] = newImageUrl
                }

                // 更新 Firestore 里的 users/{uid}
                userDocRef.update(updates).await()

                // 同步 Firebase Auth 显示名（如果你有这个工具方法，就保留）
                if (auth.currentUser?.displayName != state.name) {
                    com.cs407.homiefindr.data.auth.updateName(state.name)
                }

                _uiState.update { it.copy(isLoading = false, errorMessage = null) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /** 登出事件 */
    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _events.emit(ProfileEvent.NavigateToLogin)
        }
    }
}
