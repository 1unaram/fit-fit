import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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

    Card(
        modifier = Modifier
            .width(294.dp)
            .height(373.dp),
        shape = RoundedCornerShape(16.67.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.33.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .size(22.dp)
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
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF141B34),
                    modifier = Modifier.size(15.dp)
                )
            }

            // Main Content
            Column(
                modifier = Modifier
                    .width(252.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 33.dp, start = 21.dp, end = 21.dp),
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

@Composable
fun TemperatureSectionCompact(
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
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
                        color = if (temp == selectedValue) Color.Black else Color(0xFF8E8E93),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onValueChange(temp) }
                    )
                }
            }

            // Slider
            Box(
                modifier = Modifier
                    .width(252.dp)
                    .height(25.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Slider Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .align(Alignment.Center)
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

                // Slider Ball
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .offset(x = ((selectedValue - 1) * (252 - 25) / 9).dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFE6E6E6)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(0.33.dp, Color.White, CircleShape)
                        .shadow(1.33.dp, CircleShape)
                        .clickable { }
                )
            }
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
                .padding(vertical = 13.dp, horizontal = 7.67.dp)
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
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(43.33.dp)
    ) {
        // Weather icon placeholder
        Icon(
            imageVector = Icons.Default.Close, // Replace with actual weather icons
            contentDescription = weather,
            tint = Color(0xFF8E8E93),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun OccasionSectionCompact(
    selectedOccasion: String?,
    onOccasionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Occasion",
            fontSize = 13.33.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(7.67.dp)
        ) {
            // First Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.33.dp)
            ) {
                OccasionChipCompact("Wedding", selectedOccasion == "Wedding", onOccasionSelected)
                OccasionChipCompact("Workday", selectedOccasion == "Workday", onOccasionSelected, true)
                OccasionChipCompact("Workout", selectedOccasion == "Workout", onOccasionSelected)
            }

            // Second Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.33.dp)
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
