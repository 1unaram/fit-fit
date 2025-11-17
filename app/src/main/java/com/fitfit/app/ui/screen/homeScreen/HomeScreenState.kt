package com.fitfit.app.ui.screen.homeScreen

data class FilteredOutfit(
    val date: String,
    val name: String
)

data class HomeScreenState(
    val isLoading: Boolean = true,
    val currentWeatherIcon: String = "", // 1. 지금 날씨 아이콘
    val currentTemp: Double = 0.0,      // 2. 지금 온도
    val minTemp: Double = 0.0,          // 3. 오늘의 최저온도
    val maxTemp: Double = 0.0,          // 4. 오늘의 최고온도
    val weatherScript: String = "",     // 5. 날씨 script
    val precipitation: Double = 0.00,    // 6. 강수량 (mm)
    val windStrength: Double = 0.0,    // 7. 바람세기 (m/s)
    val isFilterSheetShown: Boolean = false, // 필터 창이 열렸는지
    val appliedFilters: FilterData = FilterData(), // "최종" 적용된 필터 데이터
    val filteredOutfits: List<FilteredOutfit> = emptyList()
)