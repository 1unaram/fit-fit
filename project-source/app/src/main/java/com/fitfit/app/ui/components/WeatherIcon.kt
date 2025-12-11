package com.fitfit.app.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun WeatherIcon(
    iconCode: String?,
    contentDescription: String = "Weather Icon",
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    AsyncImage(
        model = "https://openweathermap.org/img/wn/$iconCode@2x.png",
        contentDescription = contentDescription,
        modifier = modifier.size(64.dp)
    )
}