package com.fitfit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitfit.app.ui.screen.clothesScreen.ClothesScreen
import com.fitfit.app.ui.screen.homeScreen.HomeScreen
import com.fitfit.app.ui.screen.loginScreen.LoginScreen
import com.fitfit.app.ui.screen.outfitsScreen.OutfitsScreen

object Screens {
    const val HOME = "home"
    const val CLOTHES = "clothes"
    const val OUTFITS = "outfits"
    const val LOGIN = "login"
}

@Composable
fun AppNavigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (isLoggedIn) Screens.HOME else Screens.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screens.HOME) { HomeScreen(navController=navController) }
        composable(Screens.CLOTHES) { ClothesScreen(navController=navController) }
        composable(Screens.OUTFITS) { OutfitsScreen(navController=navController) }
        composable(Screens.LOGIN) { LoginScreen(navController=navController) }
    }
}