// com/cs407/homiefindr/data/model/ApartmentPost.kt
package com.cs407.homiefindr.data.model

data class ApartmentPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val price: Int = 0,
    val leasePeriod: String = "",
    val ownerId: String = "",
    val ownerEmail: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val imageUrls: List<String> = emptyList()   // NEW
)
