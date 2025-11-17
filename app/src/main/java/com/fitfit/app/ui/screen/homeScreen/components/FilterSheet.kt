package com.fitfit.app.ui.screen.homeScreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitfit.app.ui.screen.homeScreen.FilterData
import com.fitfit.app.ui.screen.homeScreen.OccasionFilter
import com.fitfit.app.ui.screen.homeScreen.WeatherFilter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    initialFilters: FilterData,
    onDismissRequest: () -> Unit,
    onApplyClicked: (FilterData) -> Unit
) {
    var tempTemperature by remember { mutableStateOf(initialFilters.temperatureSlider) }
    var tempWeather by remember { mutableStateOf(initialFilters.selectedWeather) }
    var tempOccasions by remember { mutableStateOf(initialFilters.selectedOccasions) }

    val defaultFilters = FilterData()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text ="tempTemperature")
            Text(text = "±${tempTemperature.roundToInt()}", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = tempTemperature,
                onValueChange = { tempTemperature = it },
                valueRange = 1f..10f,
                steps = 8 // (10 - 1) - 1 = 8 (10개의 점)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Weather")
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherFilter.values().forEach { weather ->
                        IconToggleButton(
                            checked = (tempWeather == weather),
                            onCheckedChange = {
                                tempWeather = if (it) weather else null
                            }
                        ) {
                            Icon(
                                imageVector = weather.icon,
                                contentDescription = weather.name,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Occasion")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OccasionFilter.values().forEach { occasion ->
                    FilterChip(
                        selected = tempOccasions.contains(occasion),
                        onClick = {
                            tempOccasions = if (tempOccasions.contains(occasion)) {
                                tempOccasions - occasion
                            } else {
                                tempOccasions + occasion
                            }
                        },
                        label = { Text(occasion.displayName) }
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //초기화
                OutlinedButton(
                    onClick = {
                        tempTemperature = defaultFilters.temperatureSlider
                        tempWeather = defaultFilters.selectedWeather
                        tempOccasions = defaultFilters.selectedOccasions
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                //적용
                Button(
                    onClick = {
                        val finalFilters = FilterData(
                            temperatureSlider = tempTemperature,
                            selectedWeather = tempWeather,
                            selectedOccasions = tempOccasions
                        )
                        onApplyClicked(finalFilters)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterSheet() {
    val sampleFilter = FilterData(
        temperatureSlider = 5f,
        selectedWeather = WeatherFilter.SUNNY,
        selectedOccasions = setOf(OccasionFilter.NORMAL, OccasionFilter.SCHOOL)
    )

    MaterialTheme {
        FilterSheet(
            initialFilters = sampleFilter,
            onDismissRequest = {  },
            onApplyClicked = { updatedFilters ->
                println("Applied Filters: $updatedFilters")
            }
        )
    }
}