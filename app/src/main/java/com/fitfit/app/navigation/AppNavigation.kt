package com.fitfit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitfit.app.ui.screen.homeScreen.HomeScreen

object Screens {
    const val HOME = "home"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.HOME
    ) {
        composable(Screens.HOME) { HomeScreen(navController=navController) }
    }
}

