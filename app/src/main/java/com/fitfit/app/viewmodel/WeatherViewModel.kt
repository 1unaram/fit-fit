package com.fitfit.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val weatherData: OneCallWeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel(
    private val apiKey: String
) : ViewModel() {

    private val repository = WeatherRepository(apiKey)

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    /**
     * 전체 날씨 정보 가져오기 (현재 + 예보)
     */
    fun fetchOneCallWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading

            repository.getOneCallWeather(latitude, longitude)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = WeatherUiState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = WeatherUiState.Error(
                                exception.message ?: "알 수 없는 오류가 발생했습니다"
                            )
                        }
                    )
                }
        }
    }

    /**
     * 현재 날씨와 일별 예보만 가져오기
     */
    fun fetchCurrentAndDailyWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading

            repository.getCurrentAndDailyWeather(latitude, longitude)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = WeatherUiState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = WeatherUiState.Error(
                                exception.message ?: "알 수 없는 오류가 발생했습니다"
                            )
                        }
                    )
                }
        }
    }

    /**
     * 현재 날씨만 가져오기
     */
    fun fetchCurrentWeatherOnly(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading

            repository.getCurrentWeatherOnly(latitude, longitude)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = WeatherUiState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = WeatherUiState.Error(
                                exception.message ?: "알 수 없는 오류가 발생했습니다"
                            )
                        }
                    )
                }
        }
    }

    fun fetchTimemachineWeather(latitude: Double, longitude: Double, timestamp: Long) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading

            repository.getTimemachineWeather(latitude, longitude, timestamp)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = WeatherUiState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = WeatherUiState.Error(
                                exception.message ?: "알 수 없는 오류가 발생했습니다"
                            )
                        }
                    )
                }
        }
    }
}
