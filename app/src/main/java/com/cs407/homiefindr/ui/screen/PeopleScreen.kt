package com.cs407.homiefindr.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun PeopleScreen() {

    val ids = intArrayOf(0, 1, 2, 3, 4)


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
            var expanded by remember { mutableStateOf(false) }
            // TODO: Search bar
            Text(text = "[Search bar]")
//            SearchBar(
//                inputField = TODO(),
//                expanded = expanded,
//                onExpandedChange = {expanded = it}
//            ) { }
            //Filter button
            IconButton(
                onClick = {/* do something*/ }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "filter button"
                )
            }
        }


        //The add button
        IconButton(
            onClick = {},
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
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile image",
                                    modifier = Modifier.size(70.dp)
                                )
                                Text(text = "name placeholder")
                            }

                            // Bio
                            Column {
                                Text(text = "Requirements")
                                HorizontalDivider()
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "icon for time"
                                )

                            }

                        }



                    }

                }
            }
        }
    }


}

@Preview
@Composable
fun PeoplePrev() {
    PeopleScreen()
}