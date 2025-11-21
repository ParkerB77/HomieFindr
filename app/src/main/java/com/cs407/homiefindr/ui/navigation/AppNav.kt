// AppNav.kt
package com.cs407.homiefindr.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.homiefindr.ui.screen.*
import com.google.firebase.auth.FirebaseAuth   // ★ 新增

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object Home : Route("home")
    data object People : Route("people")
    data object Messages : Route("messages")
    data object Chat : Route("chat/{chatId}")

    data object Profile : Route("profile")

    data object OtherProfile : Route("OthersProfileScreen")

    data object AddPerson : Route("AddPostScreen")

    data object AddApartment : Route("AddPostScreen")
}

data class BottomItem(val route: String, val label: String, val icon: ImageVector)
private val bottomItems = listOf(
    BottomItem(Route.Home.route, "Home", Icons.Filled.Home),
    BottomItem(Route.People.route, "People", Icons.Filled.Groups),
    BottomItem(Route.Messages.route, "Messages", Icons.Filled.Chat),
    BottomItem(Route.Profile.route, "Profile", Icons.Filled.AccountCircle),
)

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route ?: ""

    val showBottomBar =
        route.isNotEmpty() && !route.startsWith("chat") && route != Route.Login.route

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBar(nav) }
    ) { padding ->
        NavGraph(nav, paddingValues = padding)
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route

    NavigationBar {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = current?.startsWith(item.route) == true,
                onClick = {

                    if (item.route == Route.Profile.route) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (uid.isNotBlank()) {
                            nav.navigate("profile/$uid")   // ★ 加 uid
                            return@NavigationBarItem
                        }
                    }

                    nav.navigate(item.route) {
                        popUpTo(nav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun NavGraph(
    nav: NavHostController,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    NavHost(
        navController = nav,
        startDestination = Route.Login.route
    ) {
        composable(Route.Login.route) {
            LoginPage(
                loginButtonClick = { userState ->
                    nav.navigate("profile/${userState.uid}") {   // ★
                        popUpTo(Route.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Home.route) { ApartmentsScreen( onClickAdd = { nav.navigate(Route.AddApartment.route)}) }
        composable(Route.OtherProfile.route) { OthersProfileScreen() }
        composable(Route.AddPerson.route) { AddPostScreen(isPeople = true, clickBack = { nav.navigate(Route.People.route) }) }
        composable(Route.AddApartment.route) { AddPostScreen(isPeople = false, clickBack = { nav.navigate(Route.Home.route) }) }
        composable(Route.People.route) { PeopleScreen(onClickPerson = { nav.navigate(Route.OtherProfile.route) }, onClickAdd = { nav.navigate(Route.AddPerson.route)}) }
        messagesGraph(nav)

        composable(
            route = "profile/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { entry ->
            val uid = entry.arguments?.getString("uid") ?: ""

            ProfileScreen(
                onNavigateToLogin = {
                    nav.navigate(Route.Login.route) {
                        popUpTo(nav.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
//                onDeleteAndLogout = {
//                    // 1. Sign out from Firebase
//                    FirebaseAuth.getInstance().signOut()
//
//                    // 2. Go back to Login and clear back stack
//                    nav.navigate(Route.Login.route) {
//                        popUpTo(nav.graph.startDestinationId) { inclusive = true }
//                        launchSingleTop = true
//                    }
//                }
            )
        }

    }
}

private fun NavGraphBuilder.messagesGraph(nav: NavHostController) {
    composable(Route.Messages.route) {
        MessagesListScreen(
            onOpenChat = { chatId -> nav.navigate("chat/$chatId") }
        )
    }
    composable(
        route = Route.Chat.route,
        arguments = listOf(navArgument("chatId") { type = NavType.StringType })
    ) { backStackEntry ->
        val chatId = backStackEntry.arguments?.getString("chatId")!!
        ChatScreen(
            chatId = chatId,
            onBack = { nav.popBackStack() }
        )
    }
}
