// com/cs407/homiefindr/ui/screen/AddPostScreen.kt
package com.cs407.homiefindr.ui.screen

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale

private fun Long.toFormattedDateString(): String {
    val date = java.util.Date(this)
    val format = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    return format.format(date)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(
    clickBack: () -> Unit,
    vm: AddPostViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
//    var leasePeriod by remember { mutableStateOf("") }

    var leaseStartDateMillis by remember { mutableStateOf<Long?>(null) }
    var leaseEndDateMillis by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris != null) {
                imageUris = imageUris + uris
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                val uri = saveBitmapToMediaStore(context, bitmap)
                if (uri != null) {
                    imageUris = imageUris + uri
                }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = clickBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back Button"
            )
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Photos:")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    imageUris.forEach { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected image",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Text("Gallery")
                    }
                    OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
                        Text("Camera")
                    }
                }

                Text("Title:")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Post title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Apartment information:")
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Post content") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Upper limit for price:")
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Post price") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Lease period:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = leaseStartDateMillis?.toFormattedDateString() ?: "",
                            onValueChange = { /* Read-only */ },
                            label = { Text("Start Date") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showStartDatePicker = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = leaseEndDateMillis?.toFormattedDateString() ?: "",
                            onValueChange = { /* Read-only */ },
                            label = { Text("End Date") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showEndDatePicker = true }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = clickBack,
                        enabled = !vm.isSaving
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (title.isBlank() ||
                                content.isBlank() ||
                                price.isBlank() ||
                                leaseStartDateMillis == null ||
                                leaseEndDateMillis == null
                            ) {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else if (leaseEndDateMillis!! < leaseStartDateMillis!!) {
                                Toast.makeText(
                                    context,
                                    "End date must be after start date",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else {
                                val priceInt = price.toIntOrNull() ?: 0
                                vm.saveApartmentPost(
                                    title = title,
                                    content = content,
                                    price = priceInt,
                                    leaseStartDate = leaseStartDateMillis?.toFormattedDateString(),
                                    leaseEndDate = leaseEndDateMillis?.toFormattedDateString(),
                                    imageUris = imageUris
                                ) { ok ->
                                    if (ok) {
                                        clickBack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            vm.errorMessage ?: "Failed to post",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        enabled = !vm.isSaving
                    ) {
                        Text(if (vm.isSaving) "Posting..." else "Post")
                    }
                }
            }
        }
    }
    // Date Picker Dialog for Lease Start Date
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = leaseStartDateMillis)
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
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Date Picker Dialog for Lease End Date
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = leaseEndDateMillis)
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
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun saveBitmapToMediaStore(
    context: android.content.Context,
    bitmap: Bitmap
): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.Images.Media.DISPLAY_NAME,
            "homiefindr_${System.currentTimeMillis()}.jpg"
        )
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
    }
    return uri
}
