package com.cs407.homiefindr.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddPostScreen(
    isPeople: Boolean,
    clickBack: () -> Unit,
    vm: AddPostViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var leasePeriod by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button in the top-left corner
        IconButton(
            onClick = clickBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back Button"
            )
        }

        // Card with the form
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
                // Title input
                if (!isPeople) {
                    Text("Title:")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Post title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // TODO: if adding a person, get their name from profile info
                }

                // Content input
                Text(if (isPeople) "Bio information:" else "Apartment information:")
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Post content") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Price input
                Text("Upper limit for price:")
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Post price") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Lease period input
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
                    // Cancel button
                    OutlinedButton(
                        onClick = clickBack,
                        enabled = !vm.isSaving
                    ) {
                        Text("Cancel")
                    }

                    // Post button
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
                            } else if (!isPeople) {
                                val priceInt = price.toIntOrNull() ?: 0
                                vm.saveApartmentPost(
                                    title = title,
                                    content = content,
                                    price = priceInt,
                                    leasePeriod = leasePeriod
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
                            } else {
                                // TODO: implement posting logic for "people" posts
                                clickBack()
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
