package com.cs407.homiefindr.data.repository


import android.net.Uri
import com.cs407.homiefindr.data.model.Post
import com.cs407.homiefindr.data.model.UserProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.cs407.homiefindr.data.model.ApartmentPost
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // ---------- USERS ----------
    private val postsCollection = db.collection("apartmentPosts")

    // Listen for all posts (updates in real time)
    fun observeApartmentPosts(
        onSuccess: (List<ApartmentPost>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        postsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(ApartmentPost::class.java) ?: emptyList()
                onSuccess(list)
            }
    }

    // Add a new post
    fun addApartmentPost(
        title: String,
        content: String,
        price: Int,
        leasePeriod: String,
        onResult: (Boolean, Throwable?) -> Unit
    ) {
        val uid = Firebase.auth.currentUser?.uid
            ?: return onResult(false, IllegalStateException("User not logged in"))

        val docRef = postsCollection.document()
        val post = ApartmentPost(
            id = docRef.id,
            title = title,
            content = content,
            price = price,
            leasePeriod = leasePeriod,
            ownerId = uid,
            createdAt = System.currentTimeMillis()
        )

        docRef.set(post)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e) }
    }

    fun createUserProfile(name: String, email: String, onDone: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            onDone(false); return
        }

        val profile = UserProfile(
            uid = uid,
            name = name,
            email = email
        )

        db.collection("users")
            .document(uid)
            .set(profile)
            .addOnCompleteListener { onDone(it.isSuccessful) }
    }

    fun getCurrentUserProfile(onResult: (UserProfile?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            onResult(null); return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.toObject(UserProfile::class.java))
            }
            .addOnFailureListener { onResult(null) }
    }

    // ---------- POSTS ----------

    fun createPost(
        title: String,
        bio: String,
        priceMin: Int?,
        priceMax: Int?,
        leaseStartDate: String?,
        leaseEndDate: String?,
        imageUris: List<Uri>,
        onDone: (Boolean) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run {
            onDone(false); return
        }

        val postRef = db.collection("posts").document()
        val postId = postRef.id

        if (imageUris.isEmpty()) {
            val post = Post(
                postId = postId,
                creatorId = uid,
                title = title,
                bio = bio,
                priceMin = priceMin,
                priceMax = priceMax,
                leaseStartDate = leaseStartDate,
                leaseEndDate = leaseEndDate,
                imageUrls = emptyList(),
                createdAt = Timestamp.now()
            )
            postRef.set(post).addOnCompleteListener { onDone(it.isSuccessful) }
            return
        }

        val urls = mutableListOf<String>()
        var remaining = imageUris.size
        var error = false

        imageUris.forEach { uri ->
            val fileRef = storage.child("posts/$postId/${uri.lastPathSegment ?: "image.jpg"}")
            fileRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) task.exception?.let { throw it }
                    fileRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    urls.add(downloadUri.toString())
                    remaining -= 1
                    if (remaining == 0) {
                        if (error) {
                            onDone(false)
                        } else {
                            val post = Post(
                                postId = postId,
                                creatorId = uid,
                                title = title,
                                bio = bio,
                                priceMin = priceMin,
                                priceMax = priceMax,
                                leaseStartDate = leaseStartDate,
                                leaseEndDate = leaseEndDate,
                                imageUrls = urls,
                                createdAt = Timestamp.now()
                            )
                            postRef.set(post).addOnCompleteListener { onDone(it.isSuccessful) }
                        }
                    }
                }
                .addOnFailureListener {
                    error = true
                    remaining -= 1
                    if (remaining == 0) onDone(false)
                }
        }
    }

    fun getAllPosts(onResult: (List<Post>) -> Unit) {
        db.collection("posts")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                val posts = snap.documents.mapNotNull { it.toObject(Post::class.java) }
                onResult(posts)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
