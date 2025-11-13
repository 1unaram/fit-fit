package com.fitfit.app.ui.screen.homeScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfit.app.viewmodel.WeatherCardData
import com.fitfit.app.viewmodel.WeatherViewModel

@Composable
fun WeatherCard(
    data: WeatherCardData
) {
    val weatherViewModel: WeatherViewModel = viewModel()
    val weatherList by weatherViewModel.weatherList.collectAsState()
    val isLoadingApi by weatherViewModel.isLoadingApi.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.getWeatherCardData()
    }


}

