package com.cs407.homiefindr.data.model

import com.google.firebase.Timestamp

data class Post(
    val postId: String = "",
    val creatorId: String = "",
    val title: String = "",
    val bio: String = "",
    val priceMin: Int? = null,
    val priceMax: Int? = null,
    val leaseStartDate: String? = null,
    val leaseEndDate: String? = null,
    val imageUrls: List<String> = emptyList(),
    val createdAt: Timestamp? = null
)