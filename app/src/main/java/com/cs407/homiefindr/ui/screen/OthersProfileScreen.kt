package com.cs407.homiefindr.ui.screen

import android.widget.Toast
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cs407.homiefindr.data.model.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


@Composable
fun OthersProfileScreen(
    modifier: Modifier = Modifier,
    uid: String,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit
) {
//    val state = vm.uiState
    val db = remember { Firebase.firestore }
    val currentUser = Firebase.auth.currentUser?.uid ?: ""
    val context = LocalContext.current
    val firestore = Firebase.firestore

    var name by remember { mutableStateOf("Loading name...")  }
    var bio by remember { mutableStateOf("Loading bio...") }
    var avatarUri by remember { mutableStateOf("") }

    //get user information
    LaunchedEffect(uid) {
        if (uid.isBlank()) {
            Toast.makeText(
                context,
                "Couldn't open user Profile because it was blank",
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        }

        try {
            val userDocRef = firestore.collection("users").document(uid)
            val document = userDocRef.get().await()
            val userProfile: UserProfile

            if (document.exists()) {
                userProfile = document.toObject<UserProfile>()!!
                name = userProfile.name
                bio = userProfile.bio
                avatarUri = userProfile.profileImageUrl
            } else {
                Toast.makeText(
                    context,
                    "Couldn't open user Profile because user Profile doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
                onBack()
            }
        } catch(e: Exception) {
            Toast.makeText(
                context,
                "Couldn't open user Profile",
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        }
    }

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
                    if (!avatarUri.isBlank()) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Profile image",
                            modifier = Modifier.size(96.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile image",
                            modifier = Modifier.size(96.dp)
                        )
                    }

//                    Text(
//                        text = "pfp ph",
//                        style = MaterialTheme.typography.displayLarge
//                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // place holder for user name
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // place holder for tags
//                    Spacer(modifier = Modifier.height(24.dp))
//                    Text(
//                        text = "UW MADISON | CS | SENIOR",
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontWeight = FontWeight.Bold
//                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // place holder for bio
                    Text(
                        text = bio,
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
                                // message
                                try {
                                    startOrGetConversation(
                                        db = db,
                                        currentUserId = currentUser,
                                        otherUserId = uid,
                                        onResult = onOpenChat ,
                                        onError = {onBack}
                                    )

                                    Toast.makeText(
                                        context,
                                        "Successfully made chat",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch(e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Couldn't open chat",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


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

//@Preview(showBackground = true)
//@Composable
//fun OthersProfileScreenPreview() {
//    OthersProfileScreen()
//}
