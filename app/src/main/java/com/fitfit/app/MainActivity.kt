package com.fitfit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.CachePolicy
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

            // ImageLoader 생성
            val context = LocalContext.current
            remember {
                ImageLoader.Builder(context)
                    .crossfade(true)
                    .respectCacheHeaders(false)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
            }

            FitFitTheme {

                val userViewModel: UserViewModel = viewModel()
                val clothesViewModel: ClothesViewModel = viewModel()
                val outfitViewModel: OutfitViewModel = viewModel()
                val weatherViewModel: WeatherViewModel = viewModel()
                val currentUser by userViewModel.currentUser.collectAsState()

                // Outfit Repository 설정
                LaunchedEffect(Unit) {
                    val db = AppDatabase.getDatabase(this@MainActivity)
                    val outfitRepository = OutfitRepository(
                        outfitDao = db.outfitDao(),
                        outfitClothesDao = db.outfitClothesDao(),
                        context = this@MainActivity
                    )

                    weatherViewModel.setOutfitRepository(outfitRepository)
                    outfitViewModel.setOutfitRepository(outfitRepository)
                }

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
                        outfitViewModel.loadOutfitsWithClothes()
                    }
                }

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        while (true) {
                            weatherViewModel.updatePendingOutfitWeather()
                            delay(60_000) // 1분 간격. 리소스 여유따라 조정 가능
                        }
                    }
                }

                AppNavigation(
                    userViewModel,
                    clothesViewModel,
                    outfitViewModel,
                    weatherViewModel,
                )
            }

        }
    }
}