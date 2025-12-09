// AppNav.kt
package com.cs407.homiefindr.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.homiefindr.ui.screen.AddPeopleScreen
import com.cs407.homiefindr.ui.screen.AddPostScreen
import com.cs407.homiefindr.ui.screen.ApartmentsScreen
import com.cs407.homiefindr.ui.screen.ChatScreen
import com.cs407.homiefindr.ui.screen.FavoriteScreen
import com.cs407.homiefindr.ui.screen.LoginPage
import com.cs407.homiefindr.ui.screen.MessagesListScreen
import com.cs407.homiefindr.ui.screen.OthersProfileScreen
import com.cs407.homiefindr.ui.screen.PeopleScreen
import com.cs407.homiefindr.ui.screen.ProfileScreen
import com.cs407.homiefindr.ui.screen.startOrGetConversation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

sealed class Route(val route: String) {
    data object Login : Route("login")
    data object Home : Route("home")
    data object People : Route("people")
    data object Messages : Route("messages")
    data object Chat : Route("chat/{chatId}")

    data object Profile : Route("profile")
    data object Favorites : Route("favorites")

    data object OtherProfile : Route("OthersProfileScreen/{uid}")

    data object AddPerson : Route("AddPeopleScreen")
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
                            nav.navigate("profile/$uid")
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
                    nav.navigate("profile/${userState.uid}") {
                        popUpTo(Route.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Home.route) {
            ApartmentsScreen(
                onClickAdd = { nav.navigate(Route.AddApartment.route) },
                onOpenChat = { chatId -> nav.navigate("chat/$chatId") },
                onOpenOwnerProfile = { uid -> nav.navigate("OthersProfileScreen/$uid") }
            )
        }

        composable(Route.AddPerson.route) {
            AddPeopleScreen(clickBack = { nav.navigate(Route.People.route) })
        }
        composable(Route.AddApartment.route) {
            AddPostScreen(clickBack = { nav.navigate(Route.Home.route) })
        }

        composable(Route.People.route) {
            val db = Firebase.firestore
            val currentUserId = Firebase.auth.currentUser?.uid ?: ""

            PeopleScreen(
                onClickPerson = { otherUserId, otherName ->
                    // go to other person's profile
                    nav.navigate("OthersProfileScreen/$otherUserId")
                },
                onMessage = { otherUserId, otherName ->
                    if (currentUserId.isBlank()) {
                        // could show a toast later
                    } else {
                        startOrGetConversation(
                            db = db,
                            currentUserId = currentUserId,
                            otherUserId = otherUserId,
                            otherUserName = otherName,
                            onResult = { chatId ->
                                nav.navigate("chat/$chatId")
                            }
                        )
                    }
                },
                onClickAdd = { nav.navigate(Route.AddPerson.route) }
            )
        }

        messagesGraph(nav)

        composable(
            route = Route.OtherProfile.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { args ->
            val uid = args.arguments?.getString("uid") ?: ""

            OthersProfileScreen(
                uid = uid,
                onBack = { nav.popBackStack() },
                onOpenChat = { chatId -> nav.navigate("chat/$chatId") }
            )
        }

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
                },
                onNavigateToFavorites = {
                    nav.navigate(Route.Favorites.route)
                }
            )
        }

        composable(Route.Favorites.route) {
            FavoriteScreen(
                onOpenChat = { chatId -> nav.navigate("chat/$chatId") },
                onOpenOwnerProfile = { uid -> nav.navigate("OthersProfileScreen/$uid") }
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