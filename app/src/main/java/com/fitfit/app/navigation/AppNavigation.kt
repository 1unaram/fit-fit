package com.fitfit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitfit.app.ui.screen.clothesScreen.ClothesScreen
import com.fitfit.app.ui.screen.homeScreen.HomeScreen
import com.fitfit.app.ui.screen.outfitsScreen.OutfitsScreen

object Screens {
    const val HOME = "home"
    const val CLOTHES = "clothes"
    const val OUTFITS = "outfits"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.HOME
    ) {
        composable(Screens.HOME) { HomeScreen(navController=navController) }
        composable(Screens.CLOTHES) { ClothesScreen(navController=navController) }
        composable(Screens.OUTFITS) { OutfitsScreen(navController=navController) }
    }
}