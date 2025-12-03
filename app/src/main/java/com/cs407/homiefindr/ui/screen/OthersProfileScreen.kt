package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun OthersProfileScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // place holder for profile picture
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile image",
                        modifier = Modifier.size(96.dp)
                    )
//                    Text(
//                        text = "pfp ph",
//                        style = MaterialTheme.typography.displayLarge
//                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // place holder for user name
                    Text(
                        text = "Bob",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // place holder for tags
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "UW MADISON | CS | SENIOR",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // place holder for bio
                    Text(
                        text =
                                "Clean and organized.\n" +
                                "Enjoys gaming, simple cooking, \n" +
                                "and quiet study \n" +
                                "evenings. \n" +
                                "Looking for a \n" +
                                "respectful roommate \n" +
                                "with a similar routine.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton( onClick = {
                                // TODO:
                            }) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                )
                            }

                            Spacer(Modifier.width(16.dp))
                            IconButton(onClick = {
                                // TODO: message
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Message",
                                )
                            }

//                            Spacer(Modifier.width(16.dp))
//                            Icon(
//                                imageVector = Icons.Default.Share,
//                                contentDescription = "Share",
//                            )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OthersProfileScreenPreview() {
    OthersProfileScreen()
}
