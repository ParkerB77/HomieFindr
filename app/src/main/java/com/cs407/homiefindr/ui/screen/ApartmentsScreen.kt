package com.cs407.homiefindr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cs407.homiefindr.data.model.ApartmentPost
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentsScreen(
    onClickAdd: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenOwnerProfile: (String) -> Unit,
    vm: ApartmentsViewModel = viewModel(),
) {
    val state = vm.uiState
    val posts = state.posts
    val db = remember { Firebase.firestore }
    val currentUser = Firebase.auth.currentUser?.uid ?: ""
    val context = LocalContext.current

    var search: String by remember { mutableStateOf("") }
    var galleryImages by remember { mutableStateOf<List<String>?>(null) }

    val filteredPosts =
        if (search.isBlank()) posts
        else posts.filter {
            it.title.contains(search, ignoreCase = true) ||
                    it.content.contains(search, ignoreCase = true) ||
                    it.leasePeriod.contains(search, ignoreCase = true)
        }
    // for filtering
    var showFilterDialog by remember { mutableStateOf(false) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var priceRangeError by remember { mutableStateOf(false) }
    var leaseStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var leaseEndDateMillis by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var dateRangeError by remember { mutableStateOf(false) }
    var forMale by remember { mutableStateOf(false) }
    var forFemale by remember { mutableStateOf(false) }
    var petsAllowed by remember { mutableStateOf(false) }
    // rest of the filter options ....
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // search bar + filter
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("search") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { showFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }

        // list of posts
        LazyColumn(
            modifier = Modifier
                .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            items(filteredPosts) { post ->
                ApartmentCard(
                    post = post,
                    openChat = onOpenChat,
                    db = db,
                    currentUser = currentUser,
                    onShowImages = { galleryImages = it },
                    onShowToast = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onOpenOwnerProfile = onOpenOwnerProfile
                )
            }
        }

        // add button
        IconButton(
            onClick = onClickAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 150.dp, end = 16.dp)
                .background(color = Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "add button",
                modifier = Modifier.size(40.dp)
            )
        }

        // dialog to show all photos for a post
        galleryImages?.let { images ->
            AlertDialog(
                onDismissRequest = { galleryImages = null },
                confirmButton = {
                    TextButton(onClick = { galleryImages = null }) {
                        Text("Close")
                    }
                },
                title = { Text("Photos") },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(images) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(220.dp)
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            )
        }
    }
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = {
                showFilterDialog = false
            },
            title = {
                Text(text = "FilterOptions")
            },
            text = {
                // Content of the dialog
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Price Range", fontSize = 16.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = minPrice,
                            onValueChange = { minPrice = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = priceRangeError,
                            supportingText = {
                                if (priceRangeError) {
                                    Text(
                                        text = "Min-price must be less than max-price",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                        OutlinedTextField(
                            value = maxPrice,
                            onValueChange = { maxPrice = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = priceRangeError,
                            supportingText = {
                                if (priceRangeError) {
                                    Text(
                                        text = "Max-price must be more than min-price",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                    Text("Lease Period", fontSize = 16.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = leaseStartDateMillis?.toFormattedDateString() ?: "",
                                onValueChange = { /* Do nothing */ },
                                label = { Text("Starts") },
                                readOnly = true,
                                isError = dateRangeError,
                                supportingText = {
                                    if (dateRangeError) {
                                        Text(
                                            text = "Start date must be before end date",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
                            // clickable to show date picker
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showStartDatePicker = true }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = leaseEndDateMillis?.toFormattedDateString() ?: "",
                                onValueChange = { /* Do nothing */ },
                                label = { Text("Ends") },
                                readOnly = true,
                                isError = dateRangeError,
                                supportingText = {
                                    if (dateRangeError) {
                                        Text(
                                            text = "End date must be after start date",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                            )
                            // clickable to show date picker
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showEndDatePicker = true }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Open for:", fontSize = 16.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = petsAllowed,
                            onCheckedChange = { petsAllowed = it }
                        )
                        Text("Pets")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = forMale,
                            onCheckedChange = { forMale = it }
                        )
                        Text("For Male")
                        Checkbox(
                            checked = forFemale,
                            onCheckedChange = { forFemale = it }
                        )
                        Text("For Female")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val minPrice = minPrice.toIntOrNull()
                        val maxPrice = maxPrice.toIntOrNull()
                        val invalidPrice = minPrice != null && maxPrice != null && maxPrice < minPrice
                        val startDate = leaseStartDateMillis
                        val endDate = leaseEndDateMillis
                        val invalidDate = startDate != null && endDate != null && endDate < startDate
                        if (invalidPrice) {
                            priceRangeError = true
                        } else {
                            priceRangeError = false
                        }
                        if (invalidDate) {
                            dateRangeError = true
                        } else {
                            dateRangeError = false
                        }
                        if (!invalidPrice && !invalidDate) {
                            priceRangeError = false
                            dateRangeError = false
                            // TODO: filter logic with state variables
                            showFilterDialog = false
                        }
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFilterDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        leaseStartDateMillis = datePickerState.selectedDateMillis
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStartDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        leaseEndDateMillis = datePickerState.selectedDateMillis
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEndDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ApartmentCard(
    post: ApartmentPost,
    openChat: (String) -> Unit,
    db: FirebaseFirestore,
    currentUser: String,
    onShowImages: (List<String>) -> Unit,
    onShowToast: (String) -> Unit,
    onOpenOwnerProfile: (String) -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }
    val canDelete = post.ownerId == currentUser
    val context = LocalContext.current

    // Firestore reference for this user's favorite apartments
    val favoritesRef = remember(currentUser) {
        db.collection("users")
            .document(currentUser)
            .collection("favoriteApartments")
    }

    // Initialize saved state from Firestore
    LaunchedEffect(post.id, currentUser) {
        if (currentUser.isNotBlank()) {
            favoritesRef.document(post.id)
                .get()
                .addOnSuccessListener { doc ->
                    isSaved = doc.exists()
                }
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // fixed-height row so image covers whole left side of content area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: image + title under it
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(enabled = post.imageUrls.isNotEmpty()) {
                                if (post.imageUrls.isNotEmpty()) onShowImages(post.imageUrls)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val firstImage = post.imageUrls.firstOrNull()
                        if (firstImage != null) {
                            AsyncImage(
                                model = firstImage,
                                contentDescription = "Apartment image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Apartment",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = post.title.ifBlank { "Listing" },
                        fontSize = 22.sp,                // bigger title
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onOpenOwnerProfile(post.ownerId)
                        }
                    )
                }

                // RIGHT: "Requirements" + lease + price + description
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = "Requirements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "icon for time"
                        )
                        Text(
                            text = post.leasePeriod,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Money Icon",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                        Text(
                            text = "$${post.price}",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = post.content)
                }
            }

            // action row: delete (if owner) + save + chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canDelete) {
                    IconButton(
                        onClick = {
                            db.collection("apartmentPosts")
                                .document(post.id)
                                .delete()
                                .addOnSuccessListener {
                                    onShowToast("Deleted")
                                }
                                .addOnFailureListener {
                                    onShowToast("Delete failed")
                                }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post",
                            tint = Color.Red
                        )
                    }
                }

                IconButton(onClick = {
                    if (currentUser.isBlank()) {
                        onShowToast("Please sign in to save listings")
                        return@IconButton
                    }

                    val docRef = favoritesRef.document(post.id)

                    if (!isSaved) {
                        docRef.set(post)
                            .addOnSuccessListener {
                                isSaved = true
                                onShowToast("Saved")
                            }
                            .addOnFailureListener {
                                onShowToast("Failed to save")
                            }
                    } else {
                        docRef.delete()
                            .addOnSuccessListener {
                                isSaved = false
                                onShowToast("Removed from saved")
                            }
                            .addOnFailureListener {
                                onShowToast("Failed to remove")
                            }
                    }
                }) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Saved"
                    )
                }


                // message button
                IconButton(
                    onClick = {
                        startOrGetConversation(
                            db = db,
                            currentUserId = currentUser,
                            otherUserId = post.ownerId,
                            onResult = openChat,
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Couldn't open chat",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Chat Button"
                    )
                }
            }
        }
    }
}
private fun Long.toFormattedDateString(): String {
    val date = java.util.Date(this)
    val format = java.text.SimpleDateFormat("MM-dd-yyyy", java.util.Locale.getDefault())
    return format.format(date)
}