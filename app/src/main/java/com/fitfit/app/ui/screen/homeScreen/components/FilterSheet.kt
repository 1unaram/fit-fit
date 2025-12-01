

import android.R.attr.contentDescription
import android.R.attr.enabled
import android.R.attr.password
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fitfit.app.data.remote.model.Weather
import com.fitfit.app.ui.components.WeatherIcon
import kotlin.collections.setOf
import kotlin.math.roundToInt

data class FilterState(
    val temperature: Double? = null,
    val weather: String? = null,
    val occasion: List<String>? = null
)

@Composable
fun FilterSelectScreen(
    initialFilter: FilterState = FilterState(),
    onDismiss: () -> Unit,
    onSave: (Double?, String?, List<String>?) -> Unit
) {
    var selectedWeather by remember { mutableStateOf(initialFilter.weather) }
    var selectedOccasions by remember {
        mutableStateOf(initialFilter.occasion ?: emptyList()) }
    var selectedTemp by remember { mutableStateOf(initialFilter.temperature) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(top = 20.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CloseIcon(onDismiss = onDismiss)
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Main Content

                // Temperature Section
                TemperatureSectionCompact(
                    selectedValue = selectedTemp,
                    onValueChange = { newValue ->
                        selectedTemp = newValue
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Weather Section
                WeatherSectionCompact(
                    selectedWeather = selectedWeather,
                    onWeatherSelected = { weather ->
                        selectedWeather = if (selectedWeather == weather) null else weather
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Occasion Section
                OccasionSectionCompact(
                    selectedOccasions = selectedOccasions,
                    onOccasionToggle = { occasion ->
                        val current = selectedOccasions
                        if (current.contains(occasion)) {
                            // 이미 있으면 뺀다 (취소)
                            selectedOccasions = current - occasion
                        } else {
                            // 없으면 추가하는데, 3개 미만일 때만 추가
                            if (current.size < 3) {
                                selectedOccasions = current + occasion
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(36.dp))
                // Save Button
                SaveButton(
                    onClick = {
                        onSave(
                            selectedTemp,
                            selectedWeather,
                            selectedOccasions,
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AddFieldSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF8E8E93)
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureSectionCompact(
    selectedValue: Double?,           // 1.0 ~ 10.0
    onValueChange: (Double) -> Unit
) {
    var sliderPosition by remember(selectedValue) {
        mutableFloatStateOf(selectedValue?.toFloat() ?: 3f)
    }
    val valueRange = 1f..10f
    val steps = 8
    val thumbSize = 24.dp
    val labelHorizontalPadding = 12.dp

    AddFieldSection(label = "Temperature") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //    verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. 상단 라벨 영역 표시
            SliderLabels(
                currentValue = sliderPosition,
                range = valueRange,
                steps = steps,
                // 슬라이더 썸의 중심과 라벨 위치를 맞추기 위해 양옆 패딩 조정
                modifier = Modifier.padding(horizontal = labelHorizontalPadding)
            )

            // 2. 커스텀 슬라이더
            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    val snapped = newValue.roundToInt().coerceIn(1, 10)
                    sliderPosition = snapped.toFloat()
                    onValueChange(snapped.toDouble())
                },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth(),
                // 기본 트랙과 썸 색상을 투명하게 처리하여 커스텀 디자인 적용 준비
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                // 커스텀 트랙 디자인: 이미지처럼 얇고 균일한 회색 바
                track = { sliderState ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp) // 트랙 높이를 얇게 설정
                            .background(
                                color = Color(0xFFEFF4F8), // 아주 연한 회색 블루 톤
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                },
                // 커스텀 썸 디자인: 입체감 있는 흰색 원형
                thumb = {
                    Box(
                        Modifier
                            .size(thumbSize)
                            // 부드러운 그림자 효과
                            .shadow(
                                elevation = 3.dp,
                                shape = CircleShape,
                                ambientColor = Color.Black.copy(alpha = 0.1f),
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            )
                            .background(Color.White, CircleShape)
                            // 얇고 연한 회색 테두리
                            .border(0.5.dp, Color(0xFFE0E0E0), CircleShape)
                    )
                }
            )
        }
    }
}



@Composable
fun SliderLabels(
    currentValue: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier
) {
    val totalMarks = steps + 2 // 시작값 + 끝값 + 단계 수
    val roundedCurrentValue = currentValue.roundToInt()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // 라벨들을 양끝 정렬하여 균등 배치
    ) {
        for (i in 0 until totalMarks) {
            // 현재 인덱스에 해당하는 숫자 값 계산
            val markValue = range.start + i * (range.endInclusive - range.start) / (totalMarks - 1)
            val intMarkValue = markValue.roundToInt()
            // 현재 선택된 값인지 확인
            val isSelected = intMarkValue == roundedCurrentValue
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "±$intMarkValue", // "±" 접두사 추가
                    // 선택 여부에 따른 색상 및 폰트 굵기 변경
                    color = if (isSelected) Color.Black else Color(0xFFBDBDBD),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    // 텍스트 영역의 너비를 고정하여 정렬이 틀어지지 않게 함
                    modifier = Modifier.width(24.dp)
                )
            }
        }
    }
}



@Composable
fun WeatherSectionCompact(
    selectedWeather: String?,                 // ex: "Sunny" / "Cloud" / null
    onWeatherSelected: (String) -> Unit
) {
    AddFieldSection(label = "Weather") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFEFF4F8), // 연한 회색 블루 (움푹 파인 느낌)
                    shape = RoundedCornerShape(12.dp)
                )
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 8.dp), // 내부 여백
            horizontalArrangement = Arrangement.SpaceBetween, // 아이콘들을 양옆으로 균등 배치
            verticalAlignment = Alignment.CenterVertically
        ) {
            val weathers = listOf("Sunny", "Cloud", "Rain", "Snow")

            weathers.forEach { weather ->
                val isSelected = selectedWeather == weather

                WeatherIconCompact(
                    weather = weather,
                    isSelected = isSelected,
                    onClick = { onWeatherSelected(weather) }
                )
            }
        }
    }
}




@Composable
fun WeatherIconCompact(
    weather: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconCode = when (weather) {
        "Sunny" -> "01d"
        "Cloud" -> "02d"
        "Rain" -> "09d"
        "Snow" -> "13d"
        else -> "01d"
    }

    val boxModifier = if (isSelected) {
        Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x1A000000) // 아주 연한 그림자
            )
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
    } else {
        Modifier.background(color = Color.Transparent) // 평소에는 배경 없음
    }

    val iconColor = if (isSelected) Color.Black else Color(0xFF9E9E9E)

    Box(
        modifier = Modifier
            .size(48.dp)
            .then(boxModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides iconColor) {
            WeatherIcon(
                iconCode = iconCode,
                contentDescription = weather
            )
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OccasionSectionCompact(
    selectedOccasions: List<String>?,
    onOccasionToggle: (String) -> Unit
) {
    val allOccasions = listOf(
        "Workday", "School", "Date", "Normal", "Travel", "Wedding", "Workout"
    )

    AddFieldSection(label = "Occasion (up to 3)") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE8F2FF),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                allOccasions.forEach { occasion ->
                    val selected = selectedOccasions?.contains(occasion) == true

                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(8.dp),
                                spotColor = Color(0x26000000),
                                ambientColor = Color(0x26000000)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                brush = if (selected) getOccasionBrush(occasion)
                                else Brush.linearGradient(
                                    listOf(Color.White, Color(0xFFE6E6E6))
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onOccasionToggle(occasion)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = occasion,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


private fun getOccasionBrush(occasion: String): Brush {
    return when (occasion) {
        "Workday" -> Brush.linearGradient(listOf(Color(0xB3FCE8ED), Color(0xB3F8B3C2)))
        "School" -> Brush.linearGradient(listOf(Color(0xB3FAEED9), Color(0xB3F6CC84)))
        "Date" -> Brush.linearGradient(listOf(Color(0xB3FFD7DC), Color(0xB3F9B2B6)))
        "Normal" -> Brush.linearGradient(listOf(Color(0xB3F3F6FC), Color(0xB3E1E6F8)))
        "Travel" -> Brush.linearGradient(listOf(Color(0xB3D7FFEB), Color(0xB3BEEAD9)))
        "Wedding" -> Brush.linearGradient(listOf(Color(0xB3FFE6FA), Color(0xB3E8B3F8)))
        "Workout" -> Brush.linearGradient(listOf(Color(0xB3EAF9FC), Color(0xB3B3F8F6)))
        else -> Brush.linearGradient(listOf(Color(0xB3FFFFFF), Color(0xB3E6E6E6)))
    }
}

@Composable
private fun CloseIcon(
    onDismiss: () -> Unit,
) {
    IconButton(onClick = onDismiss) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier
                .size(21.dp)
                .clickable { onDismiss() },
            tint = Color(0xFF8E8E93)
        )
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
//    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(40.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x99E8F2FF),
                        Color(0xCCE8F2FF)
                    )
                ),
                shape = RoundedCornerShape(13.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0x26000000),
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
        //    enabled = enabled
    ) {
        Text(
            text = "Save",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3673E4)
        )
    }
}

