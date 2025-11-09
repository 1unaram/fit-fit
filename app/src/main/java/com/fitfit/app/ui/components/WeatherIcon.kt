package com.fitfit.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun WeatherIcon(iconCode: String, contentDescription: String?) {
    AsyncImage(
        model = "https://openweathermap.org/img/wn/$iconCode@2x.png",
        contentDescription = contentDescription,
        modifier = Modifier.size(64.dp)
    )
}