package com.fitfit.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.fitfit.app.data.repository.WeatherRepository
import com.fitfit.app.data.repository.ClothesRepository
import com.fitfit.app.ui.screen.homeScreen.FilterData
import com.fitfit.app.ui.screen.homeScreen.FilteredOutfit
import com.fitfit.app.ui.screen.homeScreen.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
   // private val weatherRepository: WeatherRepository,
    private val clothesRepository: ClothesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState(isLoading = true))
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        loadHomeScreenData()
    }
    private fun loadHomeScreenData(newFilters: FilterData = FilterData()) {
        _uiState.update { it.copy(isLoading = true)}

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

//            val weatherResult = weatherRepository.getCurrentWeather()
            val outfitsResult = listOf(
                FilteredOutfit(date = 20251110, name = "스타일 1"),
                FilteredOutfit(date = 20251110, name = "스타일 2"),
                FilteredOutfit(date = 20251110, name = "스타일 3")
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
//                    currentWeatherIcon = weatherResult.icon,
//                    currentTemp = weatherResult.currentTemp,
//                    minTemp = weatherResult.minTemp,
//                    maxTemp = weatherResult.maxTemp,
//                    weatherScript = weatherResult.script,
//                    precipitation = weatherResult.precipitation,
//                    windStrength = weatherResult.windStrength,
                    filteredOutfits = outfitsResult
                )
            }
        }
    }

    fun onFilterOptionsToggled() {
        _uiState.update { it.copy(isFilterSheetShown = !it.isFilterSheetShown) }
    }

    fun applyFilters(selectedOptions: FilterData) {
        _uiState.update { it.copy(isFilterSheetShown = false) }

        loadHomeScreenData(newFilters = selectedOptions)
    }
}