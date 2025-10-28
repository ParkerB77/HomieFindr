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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun PeopleScreen() {

    val ids = intArrayOf(0, 1, 2, 3, 4, 5)

//    Column (
//        modifier = Modifier.fillMaxSize()
//    ){
//        Spacer(modifier = Modifier.fillMaxWidth().background(color = Color.Gray).padding(20.dp))
//        Row(
//            modifier = Modifier
//                .background(color = Color.Gray).fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(text = "[Search bar]")
//            IconButton(
//                onClick = {/* do something*/ }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = "filter button"
//                )
//            }
//
//        }
//
//        LazyColumn (){
//            ids.forEach { id ->
//                item {
//                    ElevatedCard (
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(text = "" + id)
//
//                    }
//
//                }
//            }
//        }
//
//
//
//        Row (verticalAlignment = Alignment.Bottom,
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier.background(color = Color.Red)) {
//            //.fillMaxSize().padding(20.dp)
//            Text(text = "Bottom tabs")
//        }
//    }

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column {
            Spacer(modifier = Modifier.fillMaxWidth().background(color = Color.Gray).padding(20.dp))
            Row(
                modifier = Modifier
                    .background(color = Color.Gray).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "[Search bar]")
                IconButton(
                    onClick = {/* do something*/ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "filter button"
                    )
                }

            }
        }


        LazyColumn (
            modifier = Modifier.padding(top = 100.dp, bottom = 80.dp)
        ){
            ids.forEach { id ->
                item {
                    ElevatedCard (
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween

                        ) {
                            //profile picture and name
                            Column {
                                Text(text = "image placeholder")
                                Text(text = "name placeholder")
                            }


                            //user information
                            Card {
//                                Text(text = "" + id)
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



        Row (verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(top = 850.dp).background(color = Color.Red)) {
            //.fillMaxSize().padding(20.dp)
            Column {
                Text(text = "Bottom tabs", fontSize = 30.sp)
                Spacer(modifier = Modifier.padding(10.dp) )
            }
        }
    }


}

@Preview
@Composable
fun PeoplePrev() {
    PeopleScreen()
}