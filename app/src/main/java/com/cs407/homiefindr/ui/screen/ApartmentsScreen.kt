package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApartmentsScreen( onClickAdd: () -> Unit) {

    val ids = intArrayOf(0, 1, 2, 3, 4)
    var search: String by remember {mutableStateOf("")}

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        // The top row with the search bar
        Row(
            modifier = Modifier.align(Alignment.TopCenter)
//                .background(color = Color.Gray)
                .fillMaxWidth().padding(top = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            var expanded by remember { mutableStateOf(false) }
            //  Search bar

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("search") }
            )

            //Filter button
            IconButton(
                onClick = {/* filter posts */ }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }


        //The add button
        IconButton(
            onClick = onClickAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp)
//                .background(color = Color.Gray)

        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "add button",
                modifier = Modifier.size(40.dp)
            )
        }


        LazyColumn (
            modifier = Modifier.padding(top = 100.dp, bottom = 100.dp)
        ){
            ids.forEach { id ->
                item {
                    //if the apartment is saved (get from the data) (placeholder)
                    var isSaved by remember { mutableStateOf(true) }

                    ElevatedCard (
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween

                        ) {
                            //Apartment picture
                            Box (
                                modifier = Modifier.size(90.dp)
                            ){
                                //if there isn't an apartment picture have the default icon
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Apartment",
                                    modifier = Modifier.size(90.dp)
                                )

                                // Save button
                                IconButton(
                                    onClick = { /* TODO: change between saved and not saved */},
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    if (isSaved) {
                                        Icon (
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Apartment",
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        )
                                    } else {
                                        Icon (
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = "Apartment",
                                            modifier = Modifier.align(Alignment.TopEnd)
                                        )
                                    }

                                }

                            }

                            // Appartment information
                            Column (
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text(text = "Placeholder name")
                                Text("11/1/2026 to 11/1/2027")
                                Text("$800")
                                Text("no pets, in-unit laundry, 2B1B")
                            }

                        }



                    }

                }
            }
        }
    }


}