package com.fitfit.app.ui.screen.homeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeatherFilter(val icon: ImageVector) {
    SUNNY(Icons.Filled.Favorite),
    CLOUDY(Icons.Filled.Favorite),
    RAINY(Icons.Filled.Favorite),
    SNOWY(Icons.Filled.Favorite)
}

enum class OccasionFilter(val displayName: String) {
    WEDDING("Wedding"),
    WORKDAY("Workday"),
    WORKOUT("Workout"),
    TRAVEL("Travel"),
    NORMAL("Normal"),
    DATE("Date"),
    SCHOOL("School")
}

data class FilterData(
    val temperatureSlider: Float = 3.0f,
    val selectedWeather: WeatherFilter? = null,
    val selectedOccasions: Set<OccasionFilter> = emptySet()
)