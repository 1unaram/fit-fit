package com.fitfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.repository.OutfitRepository
import com.fitfit.app.navigation.AppNavigation
import com.fitfit.app.ui.theme.FitFitTheme
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel
import com.fitfit.app.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitFitTheme {
                val userViewModel: UserViewModel = viewModel()
                val clothesViewModel: ClothesViewModel = viewModel()
                val outfitViewModel: OutfitViewModel = viewModel()
                val weatherViewModel: WeatherViewModel = viewModel()

                val currentUser by userViewModel.currentUser.collectAsState()

                // 로그인 상태에 따라 동기화 시작
                LaunchedEffect(currentUser) {
                    currentUser?.let { user ->
                        // User 동기화
                        userViewModel.startRealtimeSync()

                        // Clothes 동기화
                        clothesViewModel.startRealtimeSync(user.uid)
                        clothesViewModel.syncUnsyncedData()
                        clothesViewModel.loadClothes()

                        // Outfit 동기화
                        outfitViewModel.startRealtimeSync(user.uid)
                        outfitViewModel.syncUnsyncedData()
                        outfitViewModel.loadOutfits()
                        outfitViewModel.loadOutfitsWithClothes()

                        // Weather 동기화
                        weatherViewModel.startRealtimeSync(user.uid)
                        weatherViewModel.syncUnsyncedData()
                        weatherViewModel.loadWeathersFromDB()
                    }
                }

                // Outfit Repository 설정
                LaunchedEffect(Unit) {
                    val outfitDao = AppDatabase.getDatabase(this@MainActivity).outfitDao()
                    val outfitClothesDao = AppDatabase.getDatabase(this@MainActivity).outfitClothesDao()
                    val outfitRepository =
                        OutfitRepository(outfitDao, outfitClothesDao, this@MainActivity)

                    weatherViewModel.setOutfitRepository(outfitRepository)
                }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(60_000) // 1분
                        if (currentUser != null) {
                            weatherViewModel.updatePendingOutfitWeather()
                        }
                    }
                }

                AppNavigation()
            }
        }
    }
}