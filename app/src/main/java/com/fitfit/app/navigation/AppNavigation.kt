package com.fitfit.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fitfit.app.ui.navbar.BottomNavBar
import com.fitfit.app.ui.screen.clothesScreen.ClothesScreen
import com.fitfit.app.ui.screen.homeScreen.HomeScreen
import com.fitfit.app.ui.screen.loginScreen.LoginScreen
import com.fitfit.app.ui.screen.loginScreen.RegisterScreen
import com.fitfit.app.ui.screen.outfitsScreen.OutfitsScreen
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel
import com.fitfit.app.viewmodel.WeatherViewModel

object Screens {
    const val HOME = "home"
    const val CLOTHES = "clothes"
    const val OUTFITS = "outfits"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

@Composable
fun AppNavigation(
    userViewModel: UserViewModel,
    clothesViewModel: ClothesViewModel,
    outfitViewModel: OutfitViewModel,
    weatherViewModel: WeatherViewModel
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.currentUser.collectAsState()

    // 로그인 상태에 따라 시작 화면 결정
    val startDestination = if (currentUser != null) Screens.HOME else Screens.LOGIN

    // 로그인 화면에서는 하단바를 숨김
    val showBottomBar = currentUser != null

    // 현재 route 확인
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // 현재 선택된 탭 인덱스 계산
                val selectedIndex = when (currentRoute) {
                    Screens.CLOTHES -> 0
                    Screens.OUTFITS -> 1
                    Screens.HOME -> 2
                    else -> 2 // 기본값은 HOME
                }

                BottomNavBar(
                    selectedIndex = selectedIndex,
                    onTabSelected = { index ->
                        val route = when (index) {
                            0 -> Screens.CLOTHES
                            1 -> Screens.OUTFITS
                            2 -> Screens.HOME
                            else -> Screens.HOME
                        }

                        // 같은 화면을 다시 클릭하면 이동하지 않음
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                // 백스택 관리: HOME으로 돌아갈 때는 이전 화면들 제거
                                popUpTo(Screens.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.LOGIN) {
                LoginScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable(Screens.HOME) {
                HomeScreen(
                    userViewModel = userViewModel,
                    clothesViewModel = clothesViewModel,
                    outfitViewModel = outfitViewModel,
                    weatherViewModel = weatherViewModel
                )
            }
            composable(Screens.CLOTHES) {
                ClothesScreen(
                    clothesViewModel = clothesViewModel,
                )
            }
            composable(Screens.OUTFITS) {
                OutfitsScreen(
                    navController = navController,
                    outfitViewModel = outfitViewModel,
                    weatherViewModel = weatherViewModel
                )
            }
            composable(Screens.REGISTER) {
                RegisterScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
        }
    }
}
