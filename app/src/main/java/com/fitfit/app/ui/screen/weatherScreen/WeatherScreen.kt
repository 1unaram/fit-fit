package com.fitfit.app.ui.screen.weatherScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.viewmodel.DailyWeatherData
import com.fitfit.app.viewmodel.HourlyWeatherData
import com.fitfit.app.viewmodel.WeatherScreenState
import com.fitfit.app.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherScreenState by weatherViewModel.weatherScreenState.collectAsState()
    val locationName by weatherViewModel.locationName.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.getWeatherScreenData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.8f))
    ) {
        when (weatherScreenState) {
            is WeatherScreenState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }

            is WeatherScreenState.Success -> {
                val state = weatherScreenState as WeatherScreenState.Success
                WeatherScreenContent(
                    hourlyList = state.hourlyList,
                    dailyList = state.dailyList,
                    locationName = locationName
                )
            }

            is WeatherScreenState.Failure -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            (weatherScreenState as WeatherScreenState.Failure).message,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { weatherViewModel.getWeatherScreenData() }) {
                            Text("Refresh")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}


@Composable
fun WeatherScreenContent(
    hourlyList: List<HourlyWeatherData>,
    dailyList: List<DailyWeatherData>,
    locationName: String?
) {
    val textColor = Color(0xFF111111)


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // 상단 헤더 (위치 정보)
        item {
            WeatherHeader(locationName = locationName)
        }

        // 오늘의 현재 날씨 요약
        if (hourlyList.isNotEmpty() && dailyList.isNotEmpty()) {
            item {
                CurrentWeatherSummary(
                    current = hourlyList.first(),
                    todayDaily = dailyList.first()
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        // 시간별 날씨 (가로 스크롤)
        item {
            HourlyWeatherRow(hourlyList)

            Spacer(Modifier.height(40.dp))
        }

        // 주간 날씨 (세로 리스트)
        item {
            Text(
                "Weekly Weather",
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
        }

        items(dailyList, key = { it.dt }) { daily ->
            DailyWeatherRow(daily)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun WeatherHeader(
    locationName: String?,
) {
    val textColor = Color(0xFF111111)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                locationName ?: "Unknown Location",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CurrentWeatherSummary(current: HourlyWeatherData, todayDaily: DailyWeatherData) {
    val textColor = Color(0xFF111111)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 날씨 아이콘
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            WeatherIcon(current.weatherIcon, modifier = Modifier.size(80.dp))
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 현재 온도
                Text(
                    text = "${current.temp.toInt()}°",
                    style = MaterialTheme.typography.displayLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                // 날씨 설명
                Text(
                    text = current.weatherDescription,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))
            }

            // 최고/최저 기온
            Column (
                modifier = Modifier.weight(0.8f),
            ) {
                Text(
                    text = "H: ${todayDaily.tempMax.toInt()}°  ",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "L: ${todayDaily.tempMin.toInt()}°",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HourlyWeatherRow(hourlyList: List<HourlyWeatherData>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.05f),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(hourlyList, key = { it.dt }) { hourly ->
                    HourlyWeatherItem(hourly)
                }
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(hourly: HourlyWeatherData) {
    val textColor = Color(0xFF111111)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        // 시간
        Text(
            text = formatHour(hourly.dt),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(4.dp))

        // 날씨 아이콘
        WeatherIcon(hourly.weatherIcon)

        Spacer(Modifier.height(4.dp))

        // 온도
        Text(
            text = "${hourly.temp.toInt()}°",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DailyWeatherRow(daily: DailyWeatherData) {
    val textColor = Color(0xFF111111)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.05f),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFe1eefa)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 요일
                Text(
                    text = formatDayOfWeek(daily.dt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(2f)
                )

                // 날씨 아이콘
                WeatherIcon(daily.weatherIcon)

                Spacer(Modifier.width(8.dp))

                // 최고/최저 온도
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${daily.tempMax.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${daily.tempMin.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }


                // 강수 확률
                Text(
                    text = "☔ ${ (daily.pop * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

fun formatHour(timestamp: Long): String {
    val sdf = SimpleDateFormat("ha", Locale.ENGLISH)
    return sdf.format(Date(timestamp * 1000))
}

fun formatDayOfWeek(timestamp: Long): String {
    val sdf = SimpleDateFormat("E M.d", Locale.ENGLISH)
    return sdf.format(Date(timestamp * 1000))
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)


@Preview(showBackground = true, backgroundColor = 0xFFC9E6FF)
@Composable
fun WeatherScreenContentPreview() {
    val hourlyList = listOf(
        HourlyWeatherData(
            dt = 1764572400,
            temp = 8.04,
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        ),
        HourlyWeatherData(
            dt = 1764576000,
            temp = 8.03,
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )
    )
    val dailyList = listOf(
        DailyWeatherData(
            dt = 1764558000,
            tempMin = 4.91,
            tempMax = 11.32,
            weatherIcon = "01d",
            pop = 0.5
        ),
        DailyWeatherData(
            dt = 1764644400,
            tempMin = -3.72,
            tempMax = 6.29,
            weatherIcon = "04d",
            pop = 0.0
        )
    )

    WeatherScreenContent(
        hourlyList = hourlyList,
        dailyList = dailyList,
        "Seoul"
    )
}