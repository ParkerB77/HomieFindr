package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@Composable
fun PeopleScreen( onClickPerson: () -> Unit, onClickAdd: () -> Unit) {

    val ids = intArrayOf(0, 1, 2, 3, 4)
    var search: String by remember {mutableStateOf("")}
    // for filtering
    var showFilterDialog by remember { mutableStateOf(false) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var leaseStartDate by remember { mutableStateOf("") }
    var leaseEndDate by remember { mutableStateOf("") }
    var forMale by remember { mutableStateOf(false) }
    var forFemale by remember { mutableStateOf(false) }
    var petsAllowed by remember { mutableStateOf(false) }
    // rest of the filter options ....

    Box (
        modifier = Modifier.fillMaxSize()
    ){

        // The top row with the search bar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
//                .background(color = Color.Gray)
                .fillMaxWidth()
                .padding(top = 30.dp),
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
//            SearchBar(
//                inputField = {
//                    OutlinedTextField(
//                        value = search,
//                        onValueChange = {search = it}
//                    )
//                },
//                expanded = expanded,
//                onExpandedChange = {expanded = it}
//            ) { }
            //Filter button
            IconButton(
                onClick = {
                    showFilterDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }

        LazyColumn (
            modifier = Modifier.padding(top = 100.dp, bottom = 100.dp)
        ){
            ids.forEach { id ->
                item {
                    ElevatedCard (
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween

                        ) {
                            //profile picture and name
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                IconButton(
                                    onClick = onClickPerson
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile image",
                                        modifier = Modifier.size(70.dp)
                                    )
                                }

                                Text(text = "name placeholder")
                            }

                            // Bio
                            Column {
                                Text(text = "Requirements")
                                HorizontalDivider()
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "icon for time"
                                    )
                                    Text("Spring")
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = "Money Icon"
                                    )
                                    Text("$900")
                                }
                                Text("Looking for one other roomate, no pets")



                            }

                        }



                    }

                }
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = maxPrice,
                            onValueChange = { maxPrice = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = leaseStartDate,
                            onValueChange = { leaseStartDate = it },
                            label = { Text("Starts") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // TODO: date picker
                        )
                        OutlinedTextField(
                            value = leaseEndDate,
                            onValueChange = { leaseEndDate = it },
                            label = { Text("Ends") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = petsAllowed,
                            onCheckedChange = { petsAllowed = it }
                        )
                        Text("Pets Allowed")
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
                        // TODO: filter logic with state variables
                        showFilterDialog = false
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
}

//@Preview
//@Composable
//fun PeoplePrev() {
//    PeopleScreen()
//}