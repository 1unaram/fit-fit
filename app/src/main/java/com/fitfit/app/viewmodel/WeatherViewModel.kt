package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.BuildConfig
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.WeatherEntity
import com.fitfit.app.data.remote.model.OneCallWeatherResponse
import com.fitfit.app.data.repository.OpenWeatherRepository
import com.fitfit.app.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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


class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    // ====== OpenWeatherAPI 변수 및 상태 플로우 ======
    private val openWeatherRepository = OpenWeatherRepository(BuildConfig.OPENWEATHER_API_KEY)

    private val _weatherState = MutableStateFlow<OpenWeatherState>(OpenWeatherState.Idle)
    val weatherState: StateFlow<OpenWeatherState> = _weatherState.asStateFlow()

    private val _weatherCardState = MutableStateFlow<WeatherCardUiState>(WeatherCardUiState.Idle)
    val weatherCardState: StateFlow<WeatherCardUiState> = _weatherCardState.asStateFlow()

    // ====== RoomDB 변수 및 상태 플로우 ======
    private val weatherDao = AppDatabase.getDatabase(application).weatherDao()
    private val weatherRepository = WeatherRepository(weatherDao, application)

    private val _weatherList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherList: StateFlow<List<WeatherEntity>> = _weatherList

    private val _saveState = MutableStateFlow<WeatherOperationState>(WeatherOperationState.Idle)
    val saveState: StateFlow<WeatherOperationState> = _saveState


    // ====== OpenWeatherAPI 함수 ======
    // ### 전체 날씨 정보 가져오기 (현재 + 예보) ###
    fun fetchOneCallWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = OpenWeatherState.Loading

            openWeatherRepository.getOneCallWeather(latitude, longitude)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = OpenWeatherState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = OpenWeatherState.Error(
                                exception.message ?: "Error occurred"
                            )
                        }
                    )
                }
        }
    }

    // ### Weather Card 날씨 정보 가져오기 ###
    fun getWeatherCardData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherCardState.value = WeatherCardUiState.Loading

            openWeatherRepository.getCurrentAndDailyWeather(latitude, longitude)
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

    // ### 과거 날씨 정보 가져오기 ###
    fun fetchTimemachineWeather(latitude: Double, longitude: Double, timestamp: Long) {
        viewModelScope.launch {
            _weatherState.value = OpenWeatherState.Loading

            openWeatherRepository.getTimemachineWeather(latitude, longitude, timestamp)
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherResponse ->
                            _weatherState.value = OpenWeatherState.Success(weatherResponse)
                        },
                        onFailure = { exception ->
                            _weatherState.value = OpenWeatherState.Error(
                                exception.message ?: "Error occurred"
                            )
                        }
                    )
                }
        }
    }

    // ====== RoomDB 함수 ======
    // ### 현재 사용자의 날씨 목록 로드 ###
    fun loadWeathers() = viewModelScope.launch {
        weatherRepository.getWeatherByCurrentUser()
            ?.catch { e ->
                e.printStackTrace()
                _weatherList.value = emptyList()
            }
            ?.collect { weatherList ->
                _weatherList.value = weatherList
            }
    }

    // ### 날씨 저장 ###
    fun insertWeather(
        datetime: Long,
        description: String,
        temperatureAvg: Double,
        temperatureMin: Double,
        temperatureMax: Double,
        precipitation: Double,
        windSpeed: Double,
        iconCode: String
    ) = viewModelScope.launch {
        _saveState.value = WeatherOperationState.Loading

        val result = weatherRepository.insertWeather(
            datetime,
            description,
            temperatureAvg,
            temperatureMin,
            temperatureMax,
            precipitation,
            windSpeed,
            iconCode
        )

        result.onSuccess {
            _saveState.value = WeatherOperationState.Success("날씨 정보가 저장되었습니다.")
            loadWeathers()
        }.onFailure { exception ->
            _saveState.value = WeatherOperationState.Failure(
                exception.message ?: "날씨 정보 저장 실패"
            )
        }

    }

    // ### 날씨 삭제 ###
    fun deleteWeather(wid: String) = viewModelScope.launch {
        val result = weatherRepository.deleteWeather(wid)
        result.onSuccess {
            loadWeathers()
        }
    }

    // ========== 동기화 관련 ==========
    fun syncUnsyncedData() = viewModelScope.launch {
        weatherRepository.syncUnsyncedData()
    }

    fun startRealtimeSync(uid: String) {
        weatherRepository.startRealtimeSync(uid)
    }
}

// Open Weather API 상태 클래스
sealed class OpenWeatherState {
    object Idle : OpenWeatherState()
    object Loading : OpenWeatherState()
    data class Success(val weatherData: OneCallWeatherResponse) : OpenWeatherState()
    data class Error(val message: String) : OpenWeatherState()
}


// 날씨 작업 상태
sealed class WeatherOperationState {
    object Idle : WeatherOperationState()
    object Loading : WeatherOperationState()
    data class Success(val message: String) : WeatherOperationState()
    data class Failure(val message: String) : WeatherOperationState()
}
