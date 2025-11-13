package com.fitfit.app.ui.screen.homeScreen.components

import FilterSelectScreen
import FilterState
import android.R.attr.fontWeight
import android.R.attr.label
import android.R.attr.lineHeight
import android.R.attr.text
import android.R.attr.top
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlin.String

@Composable
fun OutfitDataScreen(
    date: String,
    weatherIcon: Painter,
    temperature: String,
    clothesImages: List<String>,
    weatherDescription: String,
    precipitation: String,
    windSpeed: String,
    temperatureRange: String,
    timeRange: String,
    occasions: List<String>,
    comment: String
) {
    Box(
        modifier = Modifier
            .width(294.dp)
            .height(562.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x22000000),
                spotColor = Color(0x33000000)
            )
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.White, shape = RoundedCornerShape(16.dp))
    ){
        Column(
            modifier = Modifier
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        // 아이콘영역
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
            ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "cancel icon",
                //tint = Color(0xFF141B34),
                modifier = Modifier
                    .size(25.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE8F2FF),
                                Color(0xFFDDE4ED)
                            )
                        ),
                        shape = RoundedCornerShape(6.667.dp)
                    )
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(6.667.dp),
                        spotColor = Color(0x338CADCF)
                    )
                    .border(0.5.dp, Color(0x443A4B67), RoundedCornerShape(6.667.dp)
                    )
            )
        }

        // 날짜 및 날씨
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = weatherIcon,
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .size(30.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = temperature,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        // 옷 사진들
        ClothesGrid(clothesImages)

        // 기타 정보 영역(설명, 강수량, 풍속 등)
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 좌우: 강수량/풍속/온도범위/시간범위
            InfoLine("Weather Description", weatherDescription)
            InfoLine("Precipitation", precipitation)
            InfoLine("Wind Speed", windSpeed)
            InfoLine("Temperature Range", temperatureRange)
            InfoLine("Time Range", timeRange)
            // 행사/학교
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Occasion",
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E93),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                occasions.forEach { occasion ->
                    OccasionChip(text = occasion)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            InfoLine("Comment", comment)
        }
    }
}}

// 옷 사진
@Composable
fun ClothesGrid(clothesImages: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),           // 3열
        modifier = Modifier
            .width(294.dp)
            .height(162.dp),                   // 2행×각 71dp + 여백 포함 예시
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clothesImages.take(6)) { imageUrl ->
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Clothes Image",
                modifier = Modifier
                    .size(71.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(0.3.dp, Color.White, RoundedCornerShape(10.dp))
            )
        }
    }
}

// 정보를 보여주는 텍스트 행
@Composable
fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF8E8E93))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(35.dp))
    }
}

// 행사 칩
@Composable
fun OccasionChip(text: String) {
    Box(
        modifier = Modifier
            .height(26.333.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xB2FFD8D6), Color(0xB2F4B38A))
                ),
                shape = RoundedCornerShape(8.333.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, color = Color.Black)
    }
}


@Preview
@Composable
fun OutfitDataScreenPreview() {
    OutfitDataScreen(
        date = "2025-11-11",
        weatherIcon = painterResource(id = android.R.drawable.ic_menu_help), // 임시 아이콘
        temperature = "25°C",
        clothesImages = listOf("o1","o2"),
        weatherDescription = "Windy",
        precipitation = "Cold",
        windSpeed = "10m/s",
        temperatureRange = "10.0-11.0",
        timeRange = "10:30-11:30",
        occasions = listOf("School","Travel"),
        comment = "cold"
    )
}
