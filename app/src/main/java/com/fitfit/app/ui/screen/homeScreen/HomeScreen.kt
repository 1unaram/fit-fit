package com.fitfit.app.ui.screen.homeScreen

import FilterSelectScreen
import FilterState
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.fitfit.app.R
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.ui.screen.homeScreen.components.OutfitDataScreen
import com.fitfit.app.ui.screen.homeScreen.components.WeatherCard
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel
import com.fitfit.app.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    clothesViewModel: ClothesViewModel,
    outfitViewModel: OutfitViewModel,
    weatherViewModel: WeatherViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val outfitsWithClothes by outfitViewModel.outfitsWithClothes.collectAsState()
    var showFilter by remember { mutableStateOf(false) }
    var showOutfit by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<OutfitWithClothes?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var currentDate by remember {
        val now = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(formatter.format(now))
    }
    // 데이터 로드
    LaunchedEffect(currentUser) {
        currentUser?.let {
            clothesViewModel.loadClothes()
            outfitViewModel.loadOutfitsWithClothes()
        }
    }


    val weatherCardState by weatherViewModel.weatherCardState.collectAsState()
    val isLoading by weatherViewModel.isLoadingApi.collectAsState()


    // ================== ui ==============
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F2FF))){

        /* Section1. Weather Card */
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                //배경 이미지
                Image(
                    painter = painterResource(id = R.drawable.bg_weather_home),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    WeatherCard(state = weatherCardState)
                    Spacer(Modifier.height(8.dp))
                    DatePicker(
                        selectedDate = currentDate,
                        onDateSelected = { newDate ->
                            currentDate = newDate
                        }
                    )
                }
            }
        }


        /* Section2. Filter Button */
        item {
            FilterButtonSection(showFilter, onChange = { showFilter = it })
        }


        item {
            OutfitCardsListSection(
                outfitsWithClothes = outfitsWithClothes,
                onCardClick = { clickedOutfit ->
                    selectedOutfit = clickedOutfit
                    showOutfit = true
                }
            )
        }
    }

    // 다이얼로그들
    if (showOutfit && selectedOutfit != null) {
        OutfitDataScreen(
            outfitData = selectedOutfit!!,
            onDismiss = {
                showOutfit = false
            }
        )
    }
//    if (showDatePicker) {
//        DatePicker(
//            onDismissRequest = { showDatePicker = false },
//            onDateChange = { date ->
//                onDateSelected(date)
//                showDatePicker = false
//            },
//            initialDate = selectedDate
//        )
//    }
}


@Composable
fun DatePicker(    selectedDate: String,
                   onDateSelected: (String) -> Unit,
                   modifier: Modifier = Modifier
) {
    // 색상 정의
    val fitFitBlue = Color(0xFF4285F4) // 파란색
    val textBlack = Color(0xFF1E1E1E)  // 진한 검은색
    val buttonBackground = Color(0xFFF0F0F0) // 버튼 배경 (연한 회색)

    // 날짜 상태 관리
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    // 초기값
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH)
    var day = calendar.get(Calendar.DAY_OF_MONTH)

    try {
        val parts = selectedDate.split("-")
        if (parts.size == 3) {
            year = parts[0].toInt()
            month = parts[1].toInt() - 1 // 0-indexed 보정
            day = parts[2].toInt()
        }
    } catch (e: Exception) {
        // 파싱 실패 시 오늘 날짜 사용
    }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            val formattedDate = String.format("%d-%02d-%02d", y, m + 1, d)
            onDateSelected(formattedDate) // 부모에게 변경된 날짜 전달
        },
        year, month, day
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start // 왼쪽 정렬
    ) {
        // 1. 텍스트 부분 ("Check your Fit-Fit today")
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = textBlack)) { append("Check your ") }
                withStyle(style = SpanStyle(color = fitFitBlue)) { append("Fit-Fit") }
                withStyle(style = SpanStyle(color = textBlack)) { append(" today") }
            },
            fontSize = 22.sp, // 크기 키움
            fontWeight = FontWeight.Bold // 굵게
        )

        Spacer(modifier = Modifier.height(12.dp)) // 텍스트와 버튼 사이 간격

        // 2. 날짜 선택 버튼 부분
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp)) // 둥근 모서리
                .background(buttonBackground)    // 배경색
                .clickable { datePickerDialog.show() } // 클릭 시 달력 띄우기
                .padding(horizontal = 16.dp, vertical = 8.dp) // 내부 여백
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // 글자와 아이콘 사이 간격
            ) {
                // 날짜 텍스트
                Text(
                    text = selectedDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                // 화살표 아이콘
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Date",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FilterButtonSection(
    showFilter: Boolean, onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = { onChange(true) },
            modifier = Modifier.size(40.dp),) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filter",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showFilter) {
        Dialog(onDismissRequest = { onChange(false) }) {
            FilterSelectScreen(
                initialFilter = FilterState(3, "Sunny", "Casual"),
                onDismiss = { onChange(false) },
                onSave = {
                    // 로직 추가
                    onChange(false)
                })
        }
    }
}


@Composable
fun OutfitCardsListSection(
    outfitsWithClothes: List<OutfitWithClothes>, onCardClick: (OutfitWithClothes) -> Unit
) {
    WeatherOutfitList( outfitsWithClothes, onCardClick)
}


@Composable
fun WeatherOutfitList(
    outfitsWithClothes: List<OutfitWithClothes>, onCardClick: (OutfitWithClothes) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 33.dp)
    ) {
        if (outfitsWithClothes.isEmpty()) {
            Text(
                text = "데이터가 없습니다.", color = Color.Red, modifier = Modifier.padding(20.dp)
            )
        } else {
            outfitsWithClothes.forEach { outfitWithClothes ->
                WeatherOutfitCard(
                    outfitWithClothes, onClick = { onCardClick(outfitWithClothes)}
                )
                Spacer(Modifier.height(23.dp))
            }
        }

    }
}

@Composable
fun WeatherOutfitCard(
    outfitsWithClothes: OutfitWithClothes, onClick: (OutfitWithClothes) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
        //    .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(131.dp)
            .clickable { onClick(outfitsWithClothes) },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // date
                    Text(
                        text = formatTimestampToDate(outfitsWithClothes.outfit.wornStartTime),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // weather icon
                        Box(modifier = Modifier.size(20.dp)) {
                            WeatherIcon(outfitsWithClothes.outfit.iconCode, "Weather Icon")
                        }
                        Spacer(Modifier.width(8.dp))
                        // temperature
                        Text(
                            text = String.format(
                                "%.1f°C",
                                outfitsWithClothes.outfit.temperatureAvg),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. 표시할 아이템 개수 계산
                    // 옷이 4개 이하면 전부 다(size), 5개 이상이면 3개만 보여줌
                    val displayCount = if (outfitsWithClothes.clothes.size > 4) 3 else outfitsWithClothes.clothes.size

                    // 2. 계산된 개수만큼 앞에서부터 잘라서 보여줌
                    val visibleClothes = outfitsWithClothes.clothes.take(displayCount)

                    visibleClothes.forEach { clothes ->
                        // 썸네일 박스
                        Box(
                            modifier = Modifier
                                .size(46.dp) // 크기는 디자인에 맞게 조절
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5F5F5))
                        ) {
                            if (!clothes.imagePath.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(clothes.imagePath),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // 이미지가 없을 때
                                Icon(
                                    imageVector = Icons.Default.ImageNotSupported,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.Center).size(20.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    // 5개 이상일 경우, 더보기 박스 추가
                    if (outfitsWithClothes.clothes.size > 4) {
                        val remainingCount = outfitsWithClothes.clothes.size - 3

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$remainingCount", // 예: +2
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            // 만약 점 3개 아이콘을 원하면 아래 코드 사용
                            /*
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More",
                                tint = Color.DarkGray
                            )
                            */
                        }
                    }
                }

//                // Clothes images
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(4),           // 4열
//                    modifier = Modifier
//                        .padding(20.dp, 10.dp, 25.dp, 10.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//
//                ) {
//                    items(outfitsWithClothes.clothes) { clothesItem ->
//                        ClothesCard(clothes = clothesItem)
//                    }
//                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                outfitsWithClothes.outfit.occasion.forEach { it ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = when (it) {
                                        "Workday" -> listOf(
                                            Color(0xB3FCE8ED), Color(0xB3F8B3C2)
                                        )

                                        "School" -> listOf(
                                            Color(0xB3FAEED9), Color(0xB3F6CC84)
                                        )

                                        "Date" -> listOf(
                                            Color(0xB3FFD7DC), Color(0xB3F9B2B6)
                                        )

                                        "Normal" -> listOf(
                                            Color(0xB3F3F6FC), Color(0xB3E1E6F8)
                                        )

                                        "Travel" -> listOf(
                                            Color(0xB3D7FFEB), Color(0xB3BEEAD9)
                                        )

                                        "Wedding" -> listOf(
                                            Color(0xB3FFE6FA), Color(0xB3E8B3F8)
                                        )

                                        "Workout" -> listOf(
                                            Color(0xB3EAF9FC), Color(0xB3B3F8F6)
                                        )

                                        else -> listOf(Color(0xFFD6E9FF), Color(0xFFA8C5FE))
                                    }
                                ), shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

fun formatTimestampToDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun WeatherOutfitCardPreview() {
//    val sampleOutfit = OutfitEntity(
//        oid = "001",
//        ownerUid = "user01",
//        clothesIds = listOf("c1", "c2"),
//        createdAt = System.currentTimeMillis(),
//        isSynced = true,
//        lastModified = System.currentTimeMillis(),
//        wornStartTime = System.currentTimeMillis(),
//        wornEndTime = System.currentTimeMillis() + 2 * 60 * 60 * 1000, // 두시간 뒤
//        latitude = 37.5665,
//        longitude = 126.9780,
//        temperatureAvg = 18.2,
//        temperatureMin = 14.0,
//        temperatureMax = 23.0,
//        description = "맑음",
//        iconCode = "10d",
//        windSpeed = 4.2,
//        precipitation = 0.0,
//        weatherFetched = true
//    )

//    val mockClothesList = listOf(
//        ClothesEntity(
//            cid = "c1",
//            ownerUid = "user01",
//            imagePath = "https://via.placeholder.com/150",
//            category = "상의",
//            nickname = "티셔츠"
//        ),
//        ClothesEntity(
//            cid = "c2",
//            ownerUid = "user01",
//            imagePath = "https://via.placeholder.com/150",
//            category = "하의",
//            nickname = "청바지"
//        )
//    )
//
//    val clothesImages = sampleOutfit.clothesIds.mapNotNull { cid ->
//        mockClothesList.find { it.cid == cid }?.imagePath
//    }
//
//    WeatherOutfitCard(
//        showOutfit = true,
//        cardData = sampleOutfit.copy(
//        ),
//        onClick = {}
//    )
}
// 예시: 상단 날씨 섹션
//@Composable
//fun TopWeatherSection(
//    date: String,
//    temperature: String,
//    minMax: String,
//    weatherDesc: String,
//    precipitation: String,
//    windSpeed: String
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(291.dp)
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFFE8F2FF),
//                        Color(0xFFFFFFFF)
//                    )
//                )
//            )
//    ) {
////        Image(
////            painter = painterResource(id = R.drawable.background_sample), // 예시: 뒷 배경 지정
////            contentDescription = null,
////            modifier = Modifier.fillMaxSize(),
////            contentScale = ContentScale.Crop
////        )
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp, vertical = 22.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//
//            // 날씨 정보 행
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top,
//            ) {
//                // 아이콘+온도+최고/최저+간단정보
//                //  날씨 아이콘
//                Icon(
//                    imageVector = Icons.Default.Star, contentDescription = "Weather",
//                    tint = Color(0xFF212547),
//                    modifier = Modifier.size(36.dp)
//                )
//                Column(
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = temperature,
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF212547)
//                    )
//                    Text(
//                        text = minMax,
//                        fontSize = 13.sp,
//                        color = Color(0xFF8E8E93),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//                // 강수량, 풍속 등 정보
//                Column(
//                    horizontalAlignment = Alignment.End
//                ) {
//                    Text(
//                        text = weatherDesc,
//                        fontSize = 17.sp,
//                        fontWeight = FontWeight.ExtraBold,
//                        color = Color(0xFF3673E4)
//                    )
//                    Spacer(Modifier.height(4.dp))
//                    Row { // 강수량
//                        Text("Precipitation", fontSize = 13.sp, color = Color(0xFF8E8E93))
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            precipitation,
//                            fontSize = 14.sp,
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                    Row { // 풍속
//                        Text("Wind speed", fontSize = 13.sp, color = Color(0xFF8E8E93))
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            windSpeed,
//                            fontSize = 14.sp,
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Box(
//                    modifier = Modifier
//                        .background(
//                            color = Color.White,
//                            shape = RoundedCornerShape(10.dp)
//                        )
//                        .padding(horizontal = 16.dp, vertical = 9.dp)
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Row {
//                            Text("Check your ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//                            Text(
//                                "Fit-Fit",
//                                color = Color(0xFF3673E4),
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 18.sp
//                            )
//                            Text(" today", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//                        }
//                        Spacer(Modifier.height(5.dp))
//                        Text(
//                            text = date,
//                            fontSize = 15.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


//                DateSelector(
//                    selectedDate = selectedDate, // LocalDate 타입
//                    onDateSelected = { /* 날짜 갱신 로직 */ }
//                )

//@Composable
//fun DateSelector(
//    selectedDate: LocalDate,
//    onDateSelected: (LocalDate) -> Unit
//) {
//    var showFilter by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .background(Color.White, shape = RoundedCornerShape(10.dp))
//            .clickable { showFilter = true }
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
//    if (showFilter) {
//        DatePickerDialog(
//            onDismissRequest = { showFilter = false },
//            onDateChange = { date ->
//                onDateSelected(date)
//                showFilter = false
//            },
//            initialDate = selectedDate
//        )
//    }
//}


//    Box(modifier = Modifier.fillMaxSize()) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 35.dp, bottom = 74.dp)
//        ) {
//            // 상단 날씨·날짜 정보
//            item {
//                TopWeatherSection(
//                    date = "2025-11-02",
//                    temperature = "10.7°C",
//                    minMax = "5.3°C - 15.7°C",
//                    weatherDesc = "heavy intensity rain",
//                    precipitation = "0.85",
//                    windSpeed = "7 m/s"
//                )
//                Spacer(Modifier.height(18.dp))
//
//            }
//            item {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(Modifier.weight(1f))
//                    FloatingActionButton(
//                        onClick = { showFilter = true }
//                    ) {
//                        Icon(Icons.Default.Search, contentDescription = "Filter")
//                    }
//
//                    if (showFilter) {
//                        Dialog(onDismissRequest = { showFilter = false })
//                        {
//                            FilterSelectScreen(
//                                initialFilter = FilterState(3, "Sunny", "Casual"),
//                                onDismiss = { showFilter = false },
//                                onSave = {
//                                    // 로직 추가
//                                    showFilter = false
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//            item {
//            }
//            val filteredOutfits = listOf(
//                FilteredOutfit(date = "20251117", name = "Casual Look"),
//                FilteredOutfit(date = "20251114", name = "Business Style"),
//                FilteredOutfit(date = "20251112", name = "Party Dress"),
//                FilteredOutfit(date = "20251117", name = "Casual Look"),
//                FilteredOutfit(date = "20251114", name = "Business Style"),
//                FilteredOutfit(date = "20251112", name = "Party Dress")
//            )
//            // 날짜별 의상 추천 카드 리스트
//            items(filteredOutfits) { outfit ->
//                WeatherOutfitList(
//                    items = listOf(
//                        OutfitCardData(
//                            date = outfit.date,
//                            mainWeather = "cloud_and_rain",
//                            temperature = "9.3°C",
//                            occasions = listOf("Date", "School"),
//                            clothesImages = listOf(
//                                "https://api.builder.io/api/v1/image/assets/TEMP6b83aa181a37707ea2591d30a2c38fdce41e0c7c?width=100",
//                                "https://api.builder.io/api/v1/image/assets/TEMP63e4ca762a5ae150c81c300763c0b8289a276fab?width=100"
//                            )
//                        )
//                    ),
//                    onCardClick = { selectedOutfit = it }
//                )
//            }
//        }
//    }
//    if (selectedOutfit != null) {
//        Dialog(onDismissRequest = { selectedOutfit = null }) {
//            OutfitDataScreen(
//                date = selectedOutfit!!.date,
//                weatherIcon = painterResource(id = android.R.drawable.ic_menu_help),
//                temperature = selectedOutfit!!.temperature,
//                clothesImages = selectedOutfit!!.clothesImages,
//                weatherDescription = "날씨 정보",
//                precipitation = "0.85",
//                windSpeed = "7 m/s",
//                temperatureRange = "3 ~ 14°C",
//                timeRange = "오전/오후",
//                occasions = selectedOutfit!!.occasions,
//                comment = "코멘트 예시",
//                onDismiss = { selectedOutfit = null }
//            )
//        }
//    }
