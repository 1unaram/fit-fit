package com.fitfit.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// WeatherCard용 데이터 클래스
data class WeatherCardData(
    val todayWeatherIconCode: String?,
    val currentTemperature: Double,
    val todayMinTemperature: Double?,
    val todayMaxTemperature: Double?,
    val todayWeatherDescription: String?,
    val probabilityOfPrecipitation: Int,
    val windSpeed: Double?
)

sealed class WeatherCardUiState {
    object Idle : WeatherCardUiState()
    object Loading : WeatherCardUiState()
    data class Success(val cardData: WeatherCardData) : WeatherCardUiState()
    data class Error(val message: String) : WeatherCardUiState()
}

// 전체 날씨 정보용 UI 상태 클래스
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

     // # 전체 날씨 정보 가져오기 (현재 + 예보)
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
                                exception.message ?: "Error occurred"
                            )
                        }
                    )
                }
        }
    }

    // [+] Weather Card 날씨 정보 가져오기
    private val _weatherCardState = MutableStateFlow<WeatherCardUiState>(WeatherCardUiState.Idle)
    val weatherCardState: StateFlow<WeatherCardUiState> = _weatherCardState.asStateFlow()

    fun getWeatherCardData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherCardState.value = WeatherCardUiState.Loading

            repository.getCurrentAndDailyWeather(latitude, longitude)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            val cardData = WeatherCardData(
                                todayWeatherIconCode = weatherResponse.daily?.firstOrNull()?.weather?.firstOrNull()?.icon,
                                currentTemperature = weatherResponse.current.temp,
                                todayMinTemperature = weatherResponse.daily?.firstOrNull()?.temp?.min,
                                todayMaxTemperature = weatherResponse.daily?.firstOrNull()?.temp?.max,
                                todayWeatherDescription = weatherResponse.daily?.firstOrNull()?.weather?.firstOrNull()?.description,
                                probabilityOfPrecipitation = (weatherResponse.daily?.firstOrNull()?.pop?.times(
                                    100
                                ))?.toInt() ?: 0,
                                windSpeed = weatherResponse.daily?.firstOrNull()?.windSpeed
                            )
                            _weatherCardState.value = WeatherCardUiState.Success(cardData)
                        },
                        onFailure = { exception ->
                            _weatherCardState.value = WeatherCardUiState.Error(
                                exception.message ?: "Error occurred"
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
                                exception.message ?: "Error occurred"
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
                                exception.message ?: "Error occurred"
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
                                exception.message ?: "Error occurred"
                            )
                        }
                    )
                }
        }
    }
}
