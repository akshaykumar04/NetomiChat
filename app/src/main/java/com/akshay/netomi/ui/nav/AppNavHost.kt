package com.akshay.netomi.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akshay.netomi.ui.chat.ChatDetailScreen
import com.akshay.netomi.ui.home.ChatHomeScreen
import com.akshay.netomi.ui.home.ChatHomeViewModel


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME_ROUTE) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppDestinations.HOME_ROUTE)
            }
            val viewModel: ChatHomeViewModel = hiltViewModel(parentEntry)

            ChatHomeScreen(
                viewModel = viewModel,
                onChatClick = { chatRoom ->
                    navController.navigate("${AppDestinations.DETAILS_ROUTE}/${chatRoom.name}")
                }
            )
        }

        composable(
            route = "${AppDestinations.DETAILS_ROUTE}/{chatRoomName}",
            arguments = listOf(navArgument("chatRoomName") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(AppDestinations.HOME_ROUTE)
            }
            val viewModel: ChatHomeViewModel = hiltViewModel(parentEntry)
            val chatRoomName = backStackEntry.arguments?.getString("chatRoomName") ?: ""

            ChatDetailScreen(
                viewModel = viewModel,
                chatRoomName = chatRoomName,
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}