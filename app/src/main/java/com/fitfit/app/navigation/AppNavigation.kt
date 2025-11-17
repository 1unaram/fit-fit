package com.fitfit.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitfit.app.ui.screen.clothesScreen.ClothesScreenExample
import com.fitfit.app.ui.screen.homeScreen.HomeScreen
import com.fitfit.app.ui.screen.loginScreen.LoginScreen
import com.fitfit.app.ui.screen.loginScreen.RegisterScreen
import com.fitfit.app.ui.screen.outfitsScreen.OutfitsScreen
import com.fitfit.app.viewmodel.UserViewModel

object Screens {
    const val HOME = "home"
    const val CLOTHES = "clothes"
    const val OUTFITS = "outfits"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.currentUser.collectAsState()

    // 로그인 상태에 따라 시작 화면 결정
    val startDestination = if (currentUser != null) Screens.HOME else Screens.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screens.LOGIN) {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(Screens.REGISTER) {
            RegisterScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(Screens.HOME) {
            HomeScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(Screens.CLOTHES) {
            ClothesScreenExample(navController = navController)
        }
        composable(Screens.OUTFITS) {
            OutfitsScreen(navController = navController)
        }
    }
}