import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitfit.app.ui.components.WeatherIcon
import kotlin.math.roundToInt

data class FilterState(
    val temperature: Int = 3,
    val weather: String? = null,
    val occasion: String? = null
)

@Composable
fun FilterSelectScreen(
    initialFilter: FilterState = FilterState(),
    onDismiss: () -> Unit,
    onSave: (FilterState) -> Unit
) {
    var filterState by remember { mutableStateOf(initialFilter) }

    Box(
        modifier = Modifier
            .width(294.dp)
            .height(373.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x22000000),
                spotColor = Color(0x33000000)
            )
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.White, shape = RoundedCornerShape(16.dp))
    ) {
        Column( modifier = Modifier
            .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            // Close Button
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
                        .border(
                            0.5.dp, Color(0x443A4B67), RoundedCornerShape(6.667.dp)
                        )
                )
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(17.67.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Temperature Section
                TemperatureSectionCompact(
                    selectedValue = filterState.temperature,
                    onValueChange = { filterState = filterState.copy(temperature = it) }
                )

                // Weather Section
                WeatherSectionCompact(
                    selectedWeather = filterState.weather,
                    onWeatherSelected = { filterState = filterState.copy(weather = it) }
                )

                // Occasion Section
                OccasionSectionCompact(
                    selectedOccasion = filterState.occasion,
                    onOccasionSelected = { filterState = filterState.copy(occasion = it) }
                )

                // Save Button
                Button(
                    onClick = { onSave(filterState) },
                    modifier = Modifier
                        .width(200.dp)
                        .height(40.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(13.33.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x99E8F2FF)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 1.33.dp
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = 16.67.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3673E4)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureSectionCompact(
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Temperature",
            fontSize = 13.33.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.33.dp)
        ) {
            // Temperature Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..10).forEach { temp ->
                    Text(
                        text = temp.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (temp == sliderPosition.roundToInt()) Color.Black else Color(0xFF8E8E93),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }

            // Slider
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 1f..10f,
                steps = 8, // (10 - 1) - 1 = 8 (10개의 점)
                modifier = Modifier.width(252.dp),
                track = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .background(
                                Brush.linearGradient(
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
                                Brush.linearGradient(
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
    selectedWeather: String?,
    onWeatherSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Weather",
            fontSize = 13.33.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(71.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x99E8F2FF),
                            Color(0xCCE8F2FF)
                        )
                    ),
                    shape = RoundedCornerShape(6.67.dp)
                )
                .shadow(1.33.dp, RoundedCornerShape(6.67.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Sunny", "Cloud", "Rain", "Snow").forEach { weather ->
                WeatherIconCompact(
                    weather = weather,
                    isSelected = selectedWeather == weather,
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

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(43.33.dp)
    ) {
        WeatherIcon(
            iconCode = iconCode,
            contentDescription = weather)
    }
}

@Composable
fun OccasionSectionCompact(
    selectedOccasion: String?,
    onOccasionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = "Occasion",
            fontSize = 13.33.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x99E8F2FF),
                            Color(0xCCE8F2FF)
                        )
                    ),
                    shape = RoundedCornerShape(6.67.dp)
                )
                .shadow(1.33.dp, RoundedCornerShape(6.67.dp))
                .padding(vertical = 10.dp, horizontal = 8.dp),
        ) {
            // First Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OccasionChipCompact("Wedding", selectedOccasion == "Wedding", onOccasionSelected)
                OccasionChipCompact("Workday", selectedOccasion == "Workday", onOccasionSelected, true)
                OccasionChipCompact("Workout", selectedOccasion == "Workout", onOccasionSelected)
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Second Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OccasionChipCompact("Travel", selectedOccasion == "Travel", onOccasionSelected)
                OccasionChipCompact("Normal", selectedOccasion == "Normal", onOccasionSelected)
                OccasionChipCompact("Date", selectedOccasion == "Date", onOccasionSelected)
                OccasionChipCompact("School", selectedOccasion == "School", onOccasionSelected, true)
            }
        }
    }
}

@Composable
fun OccasionChipCompact(
    text: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit,
    isHighlighted: Boolean = false
) {
    val backgroundColor = when {
        isHighlighted && text == "Workday" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FCE8ED), Color(0xB3F8B3C2))
        )
        isHighlighted && text == "School" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FAEED9), Color(0xB3F6CC84))
        )
        isHighlighted && text == "Date" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFD7DC), Color(0xB3F9B2B6))
        )
        isHighlighted && text == "Normal" -> Brush.linearGradient(
            colors = listOf(Color(0xB3F3F6FC), Color(0xB3E1E6F8))
        )
        isHighlighted && text == "Travel" -> Brush.linearGradient(
            colors = listOf(Color(0xB3D7FFEB), Color(0xB3BEEAD9))
        )
        isHighlighted && text == "Wedding" -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFE6FA), Color(0xB3E8B3F8))
        )
        isHighlighted && text == "Workout" -> Brush.linearGradient(
            colors = listOf(Color(0xB3EAF9FC), Color(0xB3B3F8F6))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xB3FFFFFF), Color(0xB3E6E6E6))
        )
    }

    val textColor = if (isHighlighted) Color.Black else Color(0xFF8E8E93)

    Box(
        modifier = Modifier
            .height(26.33.dp)
            .background(backgroundColor, RoundedCornerShape(8.33.dp))
            .border(0.33.dp, Color.White, RoundedCornerShape(8.33.dp))
            .shadow(if (isHighlighted) 1.67.dp else 0.67.dp, RoundedCornerShape(8.33.dp))
            .clickable { onSelected(text) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.33.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun FilterSelectScreenPreview() {
    FilterSelectScreen(
        initialFilter = FilterState(3, "Sunny", "Casual"),
        onDismiss = {},
        onSave = {}
    )
}
