package com.fitfit.app.ui.screen.outfitsScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.ui.screen.homeScreen.components.OccasionChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OutfitsCard(
    outfitWithClothes: OutfitWithClothes,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) { // 열 1 . ( 날짜 및 (날씨아이콘 및 기온) )
            // 하나의 행 ; 날짜 및 (날씨아이콘 및 기온)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestampToDate(outfitWithClothes.outfit.wornStartTime),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                // 하나의 행 ; 날씨아이콘 및 기온
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherIcon(outfitWithClothes.outfit.iconCode, "Weather Icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${outfitWithClothes.outfit.temperatureAvg?.let { String.format("%.1f", it) } ?: "-"}°",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
            // 열 2 . 옷 썸네일 그리드
            ClothesGrid(outfitWithClothes.clothes)

            // 열 3 . contents
            InfoRow("Weather Description", outfitWithClothes.outfit.description ?: "-")
            InfoRow("Precipitation", "${outfitWithClothes.outfit.precipitation?.let { String.format("%.2f", it)} ?: "-"}%")
            InfoRow("Wind Speed", "${outfitWithClothes.outfit.windSpeed?.let { String.format("%.1f", it) } ?: "-"} m/s")
            InfoRow(
                "Temperature Range",
                "${outfitWithClothes.outfit.temperatureMin ?: "-"}° - ${outfitWithClothes.outfit.temperatureMax ?: "-"}°"
            )
            InfoRow(
                "Time Range",
                "${formatTimestampToDate(outfitWithClothes.outfit.wornStartTime)} - ${formatTimestampToDate(outfitWithClothes.outfit.wornEndTime)}"
            )
            // Occasion
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Occasion",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8E8E93),
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.weight(2f),
                    horizontalArrangement = Arrangement.End
                ) {
                    outfitWithClothes.outfit.occasion.forEach { occasion ->
                        OccasionChip(occasion)
                        Spacer(Modifier.width(6.dp))
                    }
                }
            }
            InfoRow("Comment", outfitWithClothes.outfit.comment ?: "-")
        }
    }
}

@Composable
fun ClothesGrid(clothesList: List<ClothesEntity>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 71.dp, max = 160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clothesList.size) { idx ->
            ClothesThumbnailCard(clothes = clothesList[idx])
        }
    }
}

@Composable
fun ClothesThumbnailCard(clothes: ClothesEntity) {
    Box(
        modifier = Modifier
            .size(71.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        if (!clothes.imagePath.isNullOrBlank()) {
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(clothes.imagePath),
                contentDescription = clothes.nickname,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.ImageNotSupported,
                contentDescription = "No Image",
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ClothesDetailDialog 스타일의 info row (소제목 좌, 내용 우)
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF8E8E93),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun OccasionChip(text: String) {
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
            .padding(horizontal = 4.dp, vertical = 3.dp)
            .height(22.dp)
            .defaultMinSize(minWidth = 54.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, color = Color.Black)
    }
}

// 날짜 포맷 함수(ClothesDetailDialog와 동일)
fun formatTimestampToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
