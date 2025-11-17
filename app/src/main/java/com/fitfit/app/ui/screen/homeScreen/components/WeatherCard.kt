package com.fitfit.app.ui.screen.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.viewmodel.WeatherCardData
import com.fitfit.app.viewmodel.WeatherCardUiState

@Composable
fun WeatherCard(
    state: WeatherCardUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        when (state) {
            is WeatherCardUiState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherCardUiState.Failure -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
            }
            is WeatherCardUiState.Success -> WeatherMainContent(state.cardData)
            WeatherCardUiState.Idle -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), contentAlignment = Alignment.Center) {
                    Text("날씨 정보를 불러오세요.", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun WeatherMainContent(cardData: WeatherCardData) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        WeatherIcon(cardData.todayWeatherIconCode, "Test")

        Spacer(Modifier.width(16.dp))



        // 왼쪽: 큰 텍스트(온도), 오른쪽: 세부정보
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${cardData.currentTemperature}°C",
                style = MaterialTheme.typography.displaySmall
            )
            cardData.todayWeatherDescription?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = "최저 ${cardData.todayMinTemperature ?: "-"}° / 최고 ${cardData.todayMaxTemperature ?: "-"}°",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.weight(1f))
        // 오른쪽 (강수, 풍속 등)
        Column(horizontalAlignment = Alignment.End) {
            Text("강수확률 ${cardData.probabilityOfPrecipitation}%")
            cardData.windSpeed?.let {
                Text("풍속 ${String.format("%.1f", it)} m/s")
            }
            cardData.locationName?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}