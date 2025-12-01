

import android.R.attr.contentDescription
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.math.roundToInt

data class FilterState(
    val temperature: Double = 3.0,
    val weather: String? = null,
    val occasion: String? = null
)

@Composable
fun FilterSelectScreen(
    initialFilter: FilterState = FilterState(),
    onDismiss: () -> Unit,
    onSave: (Double, String?, String?) -> Unit
) {
    var filterState by remember { mutableStateOf(initialFilter) }

    var selectedWeather by remember { mutableStateOf<String?>(null) }
    var selectedOccasion by remember { mutableStateOf<String?>(null) }
    var selectedTemp by remember { mutableStateOf(3.0) } // 초기값 5


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 34.dp, horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                // Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CloseIcon(
                        onDismiss = onDismiss
                    )
                }


                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //verticalArrangement = Arrangement.spacedBy(17.67.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Temperature Section
                    TemperatureSectionCompact(
                        selectedValue = selectedTemp,
                        onValueChange = { newValue ->
                            selectedTemp = newValue
                            // 필요하면 viewModel.updateTemperature(newValue) 등
                        }
                    )

                    // Weather Section
                    WeatherSectionCompact(
                        selectedWeather = selectedWeather,
                        onWeatherSelected = { weather ->
                            selectedWeather = weather   // 여기서 값 저장
                        }
                    )

                    // Occasion Section
                    OccasionSectionCompact(
                        selectedOccasion = selectedOccasion,
                        onOccasionSelected = { occasion ->
                            selectedOccasion =
                                if (selectedOccasion == occasion) null   // 다시 누르면 해제하고 싶으면
                                else occasion                             // 아니면 새 값 선택
                        }
                    )

                    // Save Button
                    SaveButton(
                        onClick = {
                            onSave(selectedTemp,
                                selectedWeather,
                                selectedOccasion,
                            )
                        },
                        enabled =
                                selectedWeather != null &&
                                selectedOccasion != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureSectionCompact(
    selectedValue: Double,           // 1.0 ~ 10.0
    onValueChange: (Double) -> Unit
) {
    var sliderPosition by remember(selectedValue) {
        mutableFloatStateOf(selectedValue.toFloat())
    }

    AddFieldSection(label = "Temperature") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..10).forEach { temp ->
                    val isSelected = temp == sliderPosition.roundToInt()
                    Text(
                        text = temp.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color(0xFF8E8E93),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Slider(
                value = sliderPosition,
                onValueChange = { value ->
                    sliderPosition = value
                    val d = value
                        .roundToInt()
                        .coerceIn(1, 10)
                        .toDouble()
                    onValueChange(d)
                },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.fillMaxWidth(),
                track = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0x99E8F2FF),
                                        Color(0xCCE8F2FF)
                                    )
                                ),
                                shape = RoundedCornerShape(13.33.dp)
                            )
                            .shadow(1.33.dp, RoundedCornerShape(13.33.dp))
                    )
                },
                thumb = {
                    Box(
                        Modifier
                            .size(25.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.White, Color(0xFFE6E6E6))
                                ),
                                shape = CircleShape
                            )
                            .border(0.33.dp, Color.White, CircleShape)
                            .shadow(1.33.dp, CircleShape)
                    )
                }
            )
        }
    }
}



@Composable
fun WeatherSectionCompact(
    selectedWeather: String?,                 // ex: "Sunny" / "Cloud" / null
    onWeatherSelected: (String) -> Unit
) {
    AddFieldSection(label = "Weather") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
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

    Box(
        modifier = Modifier
            .size(48.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0x26000000),
                ambientColor = Color(0x26000000)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF3673E4) else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isSelected) Color(0xFFE8F2FF) else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        WeatherIcon(
            iconCode = iconCode,
            contentDescription = weather
        )
    }
}

@Composable
private fun AddFieldSection(
    label: String,
    labelColor: Color = Color(0xFF3673E4),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor
        )
        content()
    }
}

@Composable
fun OccasionSectionCompact(
    selectedOccasion: String?,
    onOccasionSelected: (String) -> Unit
) {
    val allOccasions = listOf(
        "Workday", "School", "Date", "Normal", "Travel", "Wedding", "Workout"
    )

    AddFieldSection(label = "Occasion (up to 3)") {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            allOccasions.forEach { occasion ->
                val selected = selectedOccasion == occasion

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
                            onOccasionSelected(occasion)
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
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(13.dp),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color(0xFF3673E4) else Color(0x803673E4)
            )
        }
    }
}

