package com.fitfit.app.ui.screen.homeScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.data.util.formatTimestampToDate
import com.fitfit.app.ui.screen.outfitsScreen.components.OccasionChip

@Composable
fun OutfitDataScreen(
    outfitData: OutfitWithClothes,
    onDismiss: () -> Unit,
    ) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          //  .height(131.dp),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, Color.White)
    ){
        Column(
            modifier = Modifier
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        // 닫기 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
            ) {
            IconButton(onClick = { onDismiss() }) {
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
                        .border(
                            0.5.dp, Color(0x443A4B67), RoundedCornerShape(6.667.dp)
                        )
                )
            }
        }

        // 날짜 및 날씨
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimestampToDate(outfitData.outfit.wornStartTime),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(30.dp)){
                    WeatherIcon(outfitData.outfit.iconCode, "Weather Icon")}            }

                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${String.format("%.1f°C", outfitData.outfit.temperatureAvg) ?: "-"}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        // 옷 사진들
        ClothesGrid(outfitData.clothes)

        // 기타 정보 영역(설명, 강수량, 풍속 등)
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 좌우: 강수량/풍속/온도범위/시간범위
            InfoLine("Weather Description", outfitData.outfit.description ?: "-")
            InfoLine("Precipitation", "강수확률 ${outfitData.outfit.precipitation}%")
            InfoLine("Wind Speed", "풍속 ${String.format("%.1f", outfitData.outfit.windSpeed)} m/s")
            InfoLine(
                label = "Temperature Range",
                value = "최저 ${outfitData.outfit.temperatureMin ?: "-"}° / 최고 ${outfitData.outfit.temperatureMax ?: "-"}°"
            )
            InfoLine("Time Range", "${outfitData.outfit.wornStartTime ?: "-"} - ${outfitData.outfit.wornEndTime?: "-"}")
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
                outfitData.outfit.occasion.forEach { occasion ->
                    OccasionChip(text = occasion)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            InfoLine("Comment", outfitData.outfit.comment?: "-")
        }
    }
}

// 옷 사진
@Composable
fun ClothesGrid(clothesList: List<ClothesEntity>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),           // 3열
        modifier = Modifier
            .width(294.dp)
            .height(162.dp),                   // 2행×각 71dp + 여백 포함 예시
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clothesList) { clothesItem ->
            ClothesCard(clothes = clothesItem)
        }
    }
}



@Composable
fun ClothesCard(
    clothes: ClothesEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
// 옷 이미지
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                // imagePath 사용
                if (!clothes.imagePath.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(clothes.imagePath),
                        contentDescription = clothes.nickname,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 이미지 없을 때 아이콘 표시
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ImageNotSupported,
                            contentDescription = "No Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
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
fun OccasionChipTemp(text: String) {
    val backgroundColor = when (text) {
        "Workday" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FCE8ED), Color(0xB3F8B3C2))
        )
        "School" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FAEED9), Color(0xB3F6CC84))
        )
        "Date" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFD7DC), Color(0xB3F9B2B6))
        )
        "Normal" -> Brush.linearGradient(
            colors = listOf(Color(0xB3F3F6FC), Color(0xB3E1E6F8))
        )
        "Travel" -> Brush.linearGradient(
            colors = listOf(Color(0xB3D7FFEB), Color(0xB3BEEAD9))
        )
        "Wedding" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFE6FA), Color(0xB3E8B3F8))
        )
        "Workout" -> Brush.linearGradient(
            colors = listOf(Color(0xB3EAF9FC), Color(0xB3B3F8F6))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFFFFF), Color(0xB3E6E6E6))
        )
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(22.dp)
            .defaultMinSize(minWidth = 54.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.333.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, color = Color.Black)
    }
}




