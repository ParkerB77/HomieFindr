package com.cs407.homiefindr.data.model


data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val priceMin: Int? = null,
    val priceMax: Int? = null,
    val leaseStartDate: String? = null,
    val leaseEndDate: String? = null,
    val favoritePostIds: List<String> = emptyList(),
    val favoriteUserIds: List<String> = emptyList()
)
