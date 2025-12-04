package com.fitfit.app.ui.screen.homeScreen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.viewmodel.WeatherCardData
import com.fitfit.app.viewmodel.WeatherCardUiState

val WeatherBlue = Color(0xFF3B75E4) // 사진 속 파란색 텍스트
val LabelGray = Color.DarkGray // 사진 속 회색 라벨
val CardBackground = Color.White.copy(alpha = 0.7f) // 반투명 흰색 배경

@Composable
fun WeatherCard(
    state: WeatherCardUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable {onClick()},
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
                        .height(200.dp), // 로딩 높이 확보
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            is WeatherCardUiState.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(189.dp),
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
                        .height(189.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Load weather information.", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
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
            // 1. 아이콘
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(91.dp)
                    .clip(RoundedCornerShape(24.dp)) // 1. 둥근 모서리
                    .background(Color(0xFF3B75E4).copy(alpha = 0.3f))
//                    .padding(12.dp)
            ) {
                WeatherIcon(
                    iconCode = cardData.todayWeatherIconCode,
                    contentDescription = "Weather",
                    modifier = Modifier.fillMaxSize().size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. 온도 텍스트 (크고 굵게)
            Text(
                text = String.format("%.1f°C", cardData.currentTemperature),
                fontSize = 28.sp, // 폰트 키움
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )

            // 3. 최저/최고 온도 (작고 회색)
            Text(
                text = "${String.format("%.1f", cardData.todayMinTemperature ?: 0.0)}°C ~ " +
                        "${String.format("%.1f", cardData.todayMaxTemperature ?: 0.0)}°C",
                fontSize = 12.sp,
                color = LabelGray,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // [오른쪽 영역] 설명 + 강수량 + 풍속
        Column(
            modifier = Modifier.weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End, // 오른쪽 정렬
            verticalArrangement = Arrangement.Bottom
        ) {

            // 현재 위치 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(6.dp))

                cardData.locationName?.let { location ->
                    Text(
                        text = location,
                        color = LabelGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. 날씨 설명 (파란색, 오른쪽 정렬)
            cardData.todayWeatherDescription?.let {
                Text(
                    text = it.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    color = WeatherBlue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End, // 텍스트 자체를 오른쪽 정렬
                    lineHeight = 22.sp,
//                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                )
            }

            Spacer(modifier = Modifier.height(10.dp)) // 설명과 수치 사이 간격

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 2. 강수확률 (Probability of Precipitation)
                StatRow(
                    label = "Probability of\nPrecipitation",
                    value = String.format("%d%%", cardData.probabilityOfPrecipitation)
                )

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
            fontSize = 16.sp,
            color = LabelGray,
            fontWeight = FontWeight.Normal,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}