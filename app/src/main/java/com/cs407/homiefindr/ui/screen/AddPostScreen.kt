// com/cs407/homiefindr/ui/screen/AddPostScreen.kt
package com.cs407.homiefindr.ui.screen

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun AddPostScreen(
    clickBack: () -> Unit,
    vm: AddPostViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var leasePeriod by remember { mutableStateOf("") }
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
                OutlinedTextField(
                    value = leasePeriod,
                    onValueChange = { leasePeriod = it },
                    label = { Text("Post lease period") },
                    modifier = Modifier.fillMaxWidth()
                )

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
                            if (title.isBlank() || content.isBlank() ||
                                price.isBlank() || leasePeriod.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val priceInt = price.toIntOrNull() ?: 0
                                vm.saveApartmentPost(
                                    title = title,
                                    content = content,
                                    price = priceInt,
                                    leasePeriod = leasePeriod,
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
