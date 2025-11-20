package com.cs407.homiefindr.ui.screen

import android.widget.Button
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun AddPostScreen(isPeople: Boolean, clickBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var leasePeriod by remember { mutableStateOf("") }
    //TODO: images


    val context = LocalContext.current

    IconButton(
        onClick = clickBack
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back Button"
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
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
                //TODO: add images

                // Title input
                if (!isPeople) {
                    //if you're adding a person then you add the profile information
                    Text("Title: ")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text( "Post title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // TODO: if you're adding a person then get the person's name from their profile info
                }

                // content input
                if (isPeople) {
                    Text("Bio information: ")
                } else {
                    Text("Apartment information: ")
                }
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text( "Post content") },
                    modifier = Modifier.fillMaxWidth()
                )

                // price input
                Text("Upper limit for price: ")
                //TODO: make price only a number
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text( "Post price") },

                    )

                // leasePeriod input
                Text("Lease period")
                OutlinedTextField(
                    value = leasePeriod,
                    onValueChange = { leasePeriod = it },
                    label = { Text( "Post Lease Period") },

                    )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //cancel button
                    OutlinedButton (
                        onClick = clickBack,
                        enabled = true
                    ) {
                        Text( "Cancel")
                    }

                    //Post button
                    Button(
                        onClick = {
                            if (title.isBlank() || content.isBlank() || price.isBlank() || leasePeriod.isBlank()) {
                                // Display an error message if fields are empty
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Post to database
                                // TODO: update database
                                clickBack()
                            }
                        },
                        enabled = true
                    ) {
                        Text("Post")
                    }

                }



                }
        }
    }
    //try to post the post

}