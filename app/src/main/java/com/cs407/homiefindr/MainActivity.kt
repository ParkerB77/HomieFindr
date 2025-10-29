package com.cs407.homiefindr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.cs407.homiefindr.ui.theme.HomieFindrTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cs407.homiefindr.ui.screen.ApartmentsScreen
import com.cs407.homiefindr.ui.screen.PeopleScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomieFindrTheme {
                val navController = rememberNavController()


                NavHost(
                    navController = navController,
                    startDestination= "people" //placeholder
                ) {
                    composable("people") {
                        PeopleScreen()
                    }
                    composable(route = "apartments") {
                        ApartmentsScreen()
                    }
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    HomieFindrTheme {
//        Greeting("Android")
//    }
//}