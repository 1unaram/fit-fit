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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.fitfit.app.R
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.util.formatTimestampToDate
import com.fitfit.app.data.util.mapIconCodeToWeather
import com.fitfit.app.ui.components.WeatherIcon
import com.fitfit.app.ui.screen.homeScreen.components.WeatherCard
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsCard
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel
import com.fitfit.app.viewmodel.WeatherCardUiState
import com.fitfit.app.viewmodel.WeatherFilterUiState
import com.fitfit.app.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
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
    // 전체 코디 + 옷 목록 리스트(저장된 옷,온도 등등)
    val outfitsWithClothes by outfitViewModel.outfitsWithClothes.collectAsState()
    // 현재 날씨 정보(온도)
    val weatherCardState by weatherViewModel.weatherCardState.collectAsState()
    // 선택된 날짜의 날씨 정보(온도, 날씨)
    val weatherFilterState by weatherViewModel.weatherFilterState.collectAsState()
    val isLoading by weatherViewModel.isLoadingApi.collectAsState()


    // 필터 다이얼로그 표시 여부
    var showFilter by remember { mutableStateOf(false) }
    // 코디 상세 다이얼로그 표시 여부
    var showOutfit by remember { mutableStateOf(false) }
    // 현재 선택된 코디(카드 클릭 시 담김)
    var selectedOutfit by remember { mutableStateOf<OutfitWithClothes?>(null) }
    // 날짜 선택 다이얼로그 표시 여부
    var showDatePicker by remember { mutableStateOf(false) }


    // 현재 적용 중인 필터 상태(온도 오차, 날씨, 상황)
    var filterState by remember { mutableStateOf(
        FilterState(
            temperature = 3.0, //  ±3도 기본 오차
            weather = null,
            occasion = emptyList()
        )
    ) }    //기준 날짜
    var currentDate by remember {
        val now = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(formatter.format(now))
    }
    //기준 온도
    var currentTemp by remember {
        mutableStateOf<Double?>(null)
    }


    // 데이터 로드
    LaunchedEffect(currentUser) {
        currentUser?.let {
            clothesViewModel.loadClothes()
            outfitViewModel.loadOutfitsWithClothes()
        }
    }

    // weatherCardState 변경 시 currentTemp 업데이트
    LaunchedEffect(weatherCardState) {
        val success = weatherCardState as? WeatherCardUiState.Success
        if (currentTemp == null && success != null) {
            currentTemp = success.cardData.currentTemperature
        }
    }

    LaunchedEffect(weatherFilterState) {
        val success = weatherFilterState as? WeatherFilterUiState.Success
        val value = success?.weatherFilterState
        if (value != null) {
            currentTemp = value.temperature          // 기준 온도
            filterState = filterState.copy(
                weather = value.weather              // 날씨 조건만 여기서 세팅
            )
        }
    }


    // 날씨 데이터 갱신
    LaunchedEffect(Unit) {
        weatherViewModel.getWeatherCardData()
    }
    DisposableEffect(Unit) {
        onDispose {
            weatherViewModel.resetWeatherCardState()
        }
    }


    // 필터 적용된 코디 목록
    val filteredOutfits = remember(outfitsWithClothes, filterState, weatherCardState) {
        outfitsWithClothes.filter { outfitWithClothes ->
            val outfit = outfitWithClothes.outfit
            val avg = outfit.temperatureAvg
            val baseTemp = currentTemp
            val range = filterState.temperature
            val selectedOccasions = filterState.occasion

            val matchTemp = if (baseTemp != null && range != null && avg != null) {
                val min = baseTemp - range
                val max = baseTemp + range
                avg in min..max
            } else {
                true
            }

            val matchWeather =
                filterState.weather == null ||
                        mapIconCodeToWeather(outfit.iconCode) == filterState.weather

            val matchOccasion = selectedOccasions.isNullOrEmpty() ||
                    selectedOccasions.any { selected -> outfit.occasion.contains(selected) }

            matchWeather && matchOccasion && matchTemp
        }
    }


    // ================== ui ==============
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F2FF))
                .padding(bottom = 40.dp)
        ) {

            /* Section1. Weather Card section*/
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F2FF))
                ) {
                    //배경 이미지
                    Image(
                        painter = painterResource(id = R.drawable.bg_weather_home),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
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
                                runCatching {
                                    LocalDate.parse(newDate)
                                }.onSuccess { localDate ->
                                    weatherViewModel.getWeatherFilterData(localDate)
                                }
                            }
                        )
                    }
                }
            }


            /* Section2. Filter button section */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    FilterButtonSection(
                        showFilter,
                        onChange = { showFilter = it },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    )
                }
            }


            /* Section3. Outfit section */
            item {
                WeatherOutfitList(
                    outfitsWithClothes = filteredOutfits,
                    onCardClick = { outfit ->
                        selectedOutfit = outfit
                        showOutfit = true
                    }
                )
            }
        }


        // 다이얼로그들
        if (showOutfit && selectedOutfit != null) {
            Dialog(
                onDismissRequest = { showOutfit = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutfitsCard(
                        outfitWithClothes = selectedOutfit!!,
                        onDismiss = { showOutfit = false }
                    )
                }
            }
        }
        if (showFilter) {
            Dialog(onDismissRequest = { showFilter = false }) {
                FilterSelectScreen(
                    initialFilter = filterState,
                    onDismiss = { showFilter = false },
                    onSave = { temp, weather, occasion ->
                        filterState = filterState.copy(
                            temperature = temp,
                            weather = weather,
                            occasion = occasion
                        )
                        showFilter = false
                    }
                )
            }
        }
    }
}


@Composable
fun DatePicker(    selectedDate: String,
                   onDateSelected: (String) -> Unit,
                   modifier: Modifier = Modifier
) {
    // 색상 정의
    val fitFitBlue = Color(0xFF4285F4) // 파란색
    val textBlack = Color(0xFF1E1E1E)  // 진한 검은색
    val buttonBackground =  Color.White.copy(alpha = 0.5f) // 반투명 흰색 배경

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
            fontSize = 28.sp, // 크기 키움
            fontWeight = FontWeight.Bold, // 굵게
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
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
    showFilter: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .clickable { onChange(true) },        // 전체 박스 클릭 가능
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter),
            contentDescription = "Filter",
            modifier = Modifier.size(21.dp),
            tint = Color.Black
        )
    }
}



@Composable
fun WeatherOutfitList(
    outfitsWithClothes: List<OutfitWithClothes>, onCardClick: (OutfitWithClothes) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (outfitsWithClothes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No outfits found.",
                    color = Color(0xFF757575),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Try changing your filter or add a new outfit.",
                    color = Color(0xFFBDBDBD),
                    fontSize = 14.sp
                )
            }
        } else {
            outfitsWithClothes.forEach { outfitWithClothes ->
                WeatherOutfitCard(
                    outfitWithClothes, onClick = { onCardClick(outfitWithClothes)}
                )
    //            Spacer(Modifier.height(12.dp))
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
            .wrapContentHeight()
            //    .padding(horizontal = 24.dp)
        .clickable { onClick(outfitsWithClothes) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Column (
                modifier = Modifier
//                    .fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
                .padding(top = 18.dp),
                        //start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // date
                    Text(
                        text = formatTimestampToDate(outfitsWithClothes.outfit.wornStartTime),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // weather icon
                        Box(modifier = Modifier.size(24.dp)) {
                            WeatherIcon(outfitsWithClothes.outfit.iconCode, "Weather Icon")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        // temperature
                        Text(
                            text = String.format(
                                "%.1f°C",
                                outfitsWithClothes.outfit.temperatureAvg),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                Row(
                    //horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. 표시할 아이템 개수 계산
                    // 옷이 4개 이하면 전부 다(size), 5개 이상이면 3개만 보여줌
                    val totalSize = outfitsWithClothes.clothes.size
                    val displayCount = if (totalSize > 4) 3 else totalSize
                    // 2. 계산된 개수만큼 앞에서부터 잘라서 보여줌
                    val visibleClothes = outfitsWithClothes.clothes.take(displayCount)

                    visibleClothes.forEach { clothes ->
                        // 썸네일 박스
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
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
                                    modifier = Modifier.align(Alignment.Center).size(36.dp),
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
                                .weight(1f)
                                .aspectRatio(1f)
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
                        }
                    }
                    val usedSlots = displayCount + if (totalSize > 4) 1 else 0
                    if (usedSlots < 4) {
                        repeat(4 - usedSlots) {
                            Spacer(modifier = Modifier.weight(1f)) // 동일한 가중치를 주어 크기 유지
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
                    .align(Alignment.TopEnd),
                //    .padding(top = 10.dp, end = 16.dp),
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

