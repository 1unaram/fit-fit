package com.fitfit.app.ui.screen.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.viewmodel.WeatherCardData
import com.fitfit.app.viewmodel.WeatherCardUiState

val WeatherBlue = Color(0xFF3B75E4) // 사진 속 파란색 텍스트
val LabelGray = Color.DarkGray // 사진 속 회색 라벨
val CardBackground = Color.White.copy(alpha = 0.5f) // 반투명 흰색 배경

@Composable
fun WeatherCard(
    state: WeatherCardUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // 카드 외부 여백
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        when (state) {
            is WeatherCardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp), // 로딩 높이 확보
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WeatherBlue)
                }
            }
            is WeatherCardUiState.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = Color.Red)
                }
            }
            is WeatherCardUiState.Success -> WeatherMainContent(state.cardData)
            WeatherCardUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Load weather information.", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun WeatherMainContent(cardData: WeatherCardData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp), // 카드 내부 여백 넉넉하게
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // [왼쪽 영역] 아이콘 + 온도 (가운데 정렬)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // 아이콘과 텍스트 중앙 정렬
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // 1. 아이콘 크기 대폭 확대 (80dp)
            Box(modifier = Modifier.size(80.dp)) {
                WeatherIcon(
                    iconCode = cardData.todayWeatherIconCode,
                    contentDescription = "Weather",
                    // WeatherIcon 내부에서 modifier를 받는다면 fillMaxSize 권장
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. 온도 텍스트 (크고 굵게)
            Text(
                text = String.format("%.1f°C", cardData.currentTemperature),
                fontSize = 28.sp, // 폰트 키움
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // 3. 최저/최고 온도 (작고 회색)
            Text(
                text = "${String.format("%.1f", cardData.todayMinTemperature ?: 0.0)}°C - " +
                        "${String.format("%.1f", cardData.todayMaxTemperature ?: 0.0)}°C",
                fontSize = 12.sp,
                color = LabelGray,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // [오른쪽 영역] 설명 + 강수량 + 풍속
        Column(
            modifier = Modifier.weight(1f), // 남은 공간 차지
            horizontalAlignment = Alignment.End, // 오른쪽 정렬
            verticalArrangement = Arrangement.spacedBy(2.dp)         ) {
            // 1. 날씨 설명 (파란색, 오른쪽 정렬)
            cardData.todayWeatherDescription?.let {
                Text(
                    text = it,
                    color = WeatherBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End, // 텍스트 자체를 오른쪽 정렬
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // 설명과 수치 사이 간격
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp), // 👈 여기가 핵심! 라벨을 오른쪽으로 밈
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 2. 강수량 (Precipitation)
                StatRow(
                    label = "Precipitation",
                    value = String.format("%.2f", cardData.probabilityOfPrecipitation.toDouble())
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. 풍속 (Wind speed)
                StatRow(
                    label = "Wind speed",
                    value = "${
                        String.format(
                            "%.0f",
                            cardData.windSpeed ?: 0.0
                        )
                    } m/s" // 소수점 없이 깔끔하게 (사진 참고)
                )
            }
        }
    }
}

// [공통 컴포넌트] 라벨과 값을 양끝으로 배치하는 행
@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // 양끝 정렬
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = LabelGray,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}