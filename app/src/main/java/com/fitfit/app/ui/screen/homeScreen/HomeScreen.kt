package com.fitfit.app.ui.screen.homeScreen

import FilterSelectScreen
import FilterState
import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.data.model.Outfit
import com.fitfit.app.ui.screen.homeScreen.components.OutfitDataScreen
import com.fitfit.app.ui.screen.homeScreen.components.WeatherScreen
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    clothesViewModel: ClothesViewModel = viewModel(),
    outfitViewModel: OutfitViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val clothesList by clothesViewModel.clothesList.collectAsState()
    val outfitsList by outfitViewModel.outfitsList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<OutfitCardData?>(null) }

    // 데이터 로드
    LaunchedEffect(currentUser) {
        currentUser?.let {
            clothesViewModel.loadClothes()
            outfitViewModel.loadOutfits()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 74.dp)
        ) {
            // 상단 날씨·날짜 정보
             item {
                TopWeatherSection(
                date = "2025-11-02",
                temperature = "10.7°C",
                minMax = "5.3°C - 15.7°C",
                weatherDesc = "heavy intensity rain",
                precipitation = "0.85",
                windSpeed = "7 m/s"
            )
                Spacer(Modifier.height(18.dp))

            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    FloatingActionButton(
                        onClick = { showDialog = true }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Filter")
                    }

                    if (showDialog) {
                        Dialog(onDismissRequest = { showDialog = false })
                        {
                            FilterSelectScreen(
                                initialFilter = FilterState(3, "Sunny", "Casual"),
                                onDismiss = { showDialog = false },
                                onSave = {
                                    // 로직 추가
                                    showDialog = false
                                }
                            )
                        }
                    }
                }
            }
            item {
            }
            val filteredOutfits = listOf(
                FilteredOutfit(date = "20251117", name = "Casual Look"),
            FilteredOutfit(date = "20251114", name = "Business Style"),
            FilteredOutfit(date = "20251112", name = "Party Dress"),
            FilteredOutfit(date = "20251117", name = "Casual Look"),
            FilteredOutfit(date = "20251114", name = "Business Style"),
            FilteredOutfit(date = "20251112", name = "Party Dress")
            )
            // 날짜별 의상 추천 카드 리스트
            items(filteredOutfits) { outfit ->
                WeatherOutfitList(
                    items = listOf(
                        OutfitCardData(
                            date = outfit.date,
                            mainWeather = "cloud_and_rain",
                            temperature = "9.3°C",
                            occasions = listOf("Date", "School"),
                            clothesImages = listOf(
                                "https://api.builder.io/api/v1/image/assets/TEMP6b83aa181a37707ea2591d30a2c38fdce41e0c7c?width=100",
                                "https://api.builder.io/api/v1/image/assets/TEMP63e4ca762a5ae150c81c300763c0b8289a276fab?width=100"
                            )
                        )
                    ),
                    onCardClick = { selectedOutfit = it }
                )
            }
        }
    }
    if (selectedOutfit != null) {
        Dialog(onDismissRequest = { selectedOutfit = null }) {
            OutfitDataScreen(
                date = selectedOutfit!!.date,
                weatherIcon = painterResource(id = android.R.drawable.ic_menu_help),
                temperature = selectedOutfit!!.temperature,
                clothesImages = selectedOutfit!!.clothesImages,
                weatherDescription = "날씨 정보",
                precipitation = "0.85",
                windSpeed = "7 m/s",
                temperatureRange = "3 ~ 14°C",
                timeRange = "오전/오후",
                occasions = selectedOutfit!!.occasions,
                comment = "코멘트 예시",
                onDismiss = {selectedOutfit = null}
                )
        }
    }

}

// 예시: 상단 날씨 섹션
@Composable
fun TopWeatherSection(
    date: String,
    temperature: String,
    minMax: String,
    weatherDesc: String,
    precipitation: String,
    windSpeed: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(291.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F2FF),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.background_sample), // 예시: 뒷 배경 지정
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        )  {

            // 날씨 정보 행
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                // 아이콘+온도+최고/최저+간단정보
                   //  날씨 아이콘
                    Icon(
                        imageVector = Icons.Default.Star,                        contentDescription = "Weather",
                        tint = Color(0xFF212547),
                        modifier = Modifier.size(36.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = temperature,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212547)
                        )
                        Text(
                            text = minMax,
                            fontSize = 13.sp,
                            color = Color(0xFF8E8E93),
                            fontWeight = FontWeight.Medium
                        )
                    }
                // 강수량, 풍속 등 정보
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = weatherDesc,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3673E4)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row { // 강수량
                        Text("Precipitation", fontSize = 13.sp, color = Color(0xFF8E8E93))
                        Spacer(Modifier.width(8.dp))
                        Text(precipitation, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Row { // 풍속
                        Text("Wind speed", fontSize = 13.sp, color = Color(0xFF8E8E93))
                        Spacer(Modifier.width(8.dp))
                        Text(windSpeed, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            Text("Check your ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Text("Fit-Fit", color = Color(0xFF3673E4), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(" today", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = date,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


//                DateSelector(
//                    selectedDate = selectedDate, // LocalDate 타입
//                    onDateSelected = { /* 날짜 갱신 로직 */ }
//                )

//@Composable
//fun DateSelector(
//    selectedDate: LocalDate,
//    onDateSelected: (LocalDate) -> Unit
//) {
//    var showDialog by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .background(Color.White, shape = RoundedCornerShape(10.dp))
//            .clickable { showDialog = true }
//            .padding(horizontal = 14.dp, vertical = 7.dp)
//    ) {
//        Text(
//            text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
//            fontSize = 13.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black
//        )
//    }
//
//    if (showDialog) {
//        DatePickerDialog(
//            onDismissRequest = { showDialog = false },
//            onDateChange = { date ->
//                onDateSelected(date)
//                showDialog = false
//            },
//            initialDate = selectedDate
//        )
//    }
//}

// 예시: 날짜별 의상 카드 리스트
data class OutfitCardData(
    val date: String,
    val mainWeather: String,
    val temperature: String,
    val occasions: List<String>,
    val clothesImages: List<String>
)

@Composable
fun WeatherOutfitList(
    items: List<OutfitCardData>,
    onCardClick: (OutfitCardData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 33.dp)
    ) {
        items.forEach { item ->
            WeatherOutfitCard(
                cardData = item,
                onClick = { onCardClick(item) }
            )
            Spacer(Modifier.height(23.dp))
        }
    }
}

@Composable
fun WeatherOutfitCard(cardData: OutfitCardData, onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cardData.occasions.forEach { occasion ->
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        colors = when (occasion) {
                                            "Workday" -> listOf(Color(0xB3FCE8ED), Color(0xB3F8B3C2))
                                            "School" -> listOf(Color(0xB3FAEED9), Color(0xB3F6CC84))
                                            "Date" -> listOf(Color(0xB3FFD7DC), Color(0xB3F9B2B6))
                                            "Normal" -> listOf(Color(0xB3F3F6FC), Color(0xB3E1E6F8))
                                            "Travel" -> listOf(Color(0xB3D7FFEB), Color(0xB3BEEAD9))
                                            "Wedding" -> listOf(Color(0xB3FFE6FA), Color(0xB3E8B3F8))
                                            "Workout" -> listOf(Color(0xB3EAF9FC), Color(0xB3B3F8F6))
                                            else -> listOf(Color(0xFFD6E9FF), Color(0xFFA8C5FE))
                                        }
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
//                            Text(
//                                text = occasion,
//                                fontSize = 10.sp,
//                                color = Color(0xFF141B34),
//                                fontWeight = FontWeight.Bold
//                            )
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(cardData.date, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = cardData.mainWeather,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(cardData.temperature, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // Right row: Clothes images
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cardData.clothesImages.forEach { imgUrl ->
                    // TODO: Use Coil or Glide for image loading in production
                    // Example using Coil:
                    // AsyncImage(model = imgUrl, contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)))
                    Spacer(Modifier.width(8.dp)) // Remove or replace with image loader above
                }
            }
        }
    }
    Modifier.clickable { onClick() }
}

