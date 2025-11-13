package com.fitfit.app.ui.screen.homeScreen.components

//import android.app.Application
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Card
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.fitfit.app.data.util.LocationHelper
//import com.fitfit.app.data.util.LocationManager
//import com.fitfit.app.ui.components.WeatherIcon
//import com.fitfit.app.viewmodel.OpenWeatherState
//import com.fitfit.app.viewmodel.WeatherViewModel
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.rememberMultiplePermissionsState
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun WeatherScreen() {
//    val context = LocalContext.current
//    val application = context.applicationContext as Application
//
//    val weatherViewModel: WeatherViewModel = viewModel()
//    val locationManager = LocationManager(application)
//    val weatherState by weatherViewModel.openWeatherState.collectAsState()
//    var isLoadingLocation by remember { mutableStateOf(false) }
//
//    val locationPermissions = rememberMultiplePermissionsState(
//        permissions = listOf(
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//    )
//
//    LaunchedEffect(locationPermissions.allPermissionsGranted) {
//        if (locationPermissions.allPermissionsGranted) {
//            isLoadingLocation = true
//            val location = locationHelper.getCurrentLocation()
//            if (location != null) {
//                val (lat, lon) = location
//                weatherViewModel.fetchOneCallWeather(lat, lon)
//            } else {
//                // 5초 내에 위치를 못 찾으면 서울 좌표 사용
//                weatherViewModel.fetchOneCallWeather(37.5665, 126.9780)
//            }
//            isLoadingLocation = false
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // 권한이 거부된 경우
//        if (!locationPermissions.allPermissionsGranted) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    "위치 권한이 필요합니다.",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                androidx.compose.material3.Button(
//                    onClick = { locationPermissions.launchMultiplePermissionRequest() }
//                ) {
//                    Text("권한 요청")
//                }
//            }
//            return@Column
//        }
//
//        if (isLoadingLocation) {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                CircularProgressIndicator()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text("위치 확인 중...")
//            }
//            return@Column
//        }
//
//        when (weatherState) {
//            is OpenWeatherState.Idle -> {
//                Text("날씨 정보를 불러오는 중...")
//            }
//
//            is OpenWeatherState.Loading -> {
//                CircularProgressIndicator()
//            }
//
//            is OpenWeatherState.Success -> {
//                val weather = (weatherState as OpenWeatherState.Success).weatherData
//
//                // 현재 날씨
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        WeatherIcon(weather.current.weather.firstOrNull()?.icon ?: "", "현재 날씨 아이콘")
//                        Text("현재 날씨", style = MaterialTheme.typography.headlineSmall)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text("온도: ${weather.current.temp}°C")
//                        Text("체감: ${weather.current.feelsLike}°C")
//                        Text("날씨: ${weather.current.weather.firstOrNull()?.description ?: ""}")
//                        Text("습도: ${weather.current.humidity}%")
//                        Text("풍속: ${weather.current.windSpeed}m/s")
//                    }
//                }
//
//                // 시간별 예보 (있는 경우)
//                weather.hourly?.let { hourlyList ->
//                    Text(
//                        "시간별 예보 (${hourlyList.size}시간)",
//                        style = MaterialTheme.typography.headlineSmall,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                    LazyRow {
//                        items(
//                            items = hourlyList.take(24),
//                            key = { it.dt }
//                        ) { hourly ->
//                            Card(
//                                modifier = Modifier
//                                    .width(100.dp)
//                                    .padding(4.dp)
//                            ) {
//                                Column(
//                                    modifier = Modifier.padding(8.dp),
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Text(
//                                        SimpleDateFormat("HH:mm", Locale.getDefault())
//                                            .format(Date(hourly.dt * 1000))
//                                    )
//                                    Text("${hourly.temp}°C")
//                                    Text(hourly.weather.firstOrNull()?.description ?: "")
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // 일별 예보 (있는 경우)
//                weather.daily?.let { dailyList ->
//                    Text(
//                        "주간 예보",
//                        style = MaterialTheme.typography.headlineSmall,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                    Column {
//                        dailyList.forEach { daily ->
//                            Card(
//                                modifier = Modifier                                    .fillMaxWidth()
//                                    .padding(vertical = 4.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier.padding(16.dp),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Column {
//                                        Text(
//                                            SimpleDateFormat("MM/dd (E)", Locale.KOREAN)
//                                                .format(Date(daily.dt * 1000))
//                                        )
//                                        Text(daily.weather.firstOrNull()?.description ?: "")
//                                    }
//                                    Text("${daily.temp.min}°C / ${daily.temp.max}°C")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            is OpenWeatherState.Failure -> {
//                val errorMessage = (weatherState as OpenWeatherState.Failure).message
//                Text(
//                    "오류: $errorMessage",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}