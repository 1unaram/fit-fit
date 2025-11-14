package com.fitfit.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.BuildConfig
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.WeatherEntity
import com.fitfit.app.data.repository.OpenWeatherRepository
import com.fitfit.app.data.repository.OutfitRepository
import com.fitfit.app.data.repository.WeatherRepository
import com.fitfit.app.data.util.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    // ========= Repositories & LocationManager =========
    private val openWeatherRepository = OpenWeatherRepository(BuildConfig.OPENWEATHER_API_KEY)
    private val locationManager = LocationManager(application)
    private var outfitRepository: OutfitRepository? = null

    fun setOutfitRepository(repository: OutfitRepository) {
        this.outfitRepository = repository
    }

    // ========= State Flows =========
    private val _weatherCardState = MutableStateFlow<WeatherCardUiState>(WeatherCardUiState.Idle)
    val weatherCardState: StateFlow<WeatherCardUiState> = _weatherCardState.asStateFlow()

    private val _isLoadingApi = MutableStateFlow(false)
    val isLoadingApi: StateFlow<Boolean> = _isLoadingApi

    private val _currentLocation = MutableStateFlow<LocationManager.Coordinates?>(null)
    val currentLocation: StateFlow<LocationManager.Coordinates?> = _currentLocation


    // ========== Case 1: HomeScreen WeatherCard ==========
    fun getWeatherCardData() = viewModelScope.launch {
        _isLoadingApi.value = true
        _weatherCardState.value = WeatherCardUiState.Loading

        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->

            // 1. 현재 위치 가져오기
            val coordinates = LocationManager.Coordinates.fromLocation(location)
            _currentLocation.value = coordinates

            // 2. Weather Card용 날씨 정보 가져오기
            openWeatherRepository.getCurrentAndDailyWeather(
                coordinates.latitude, coordinates.longitude
            ).collect { result ->
                result.fold(onSuccess = { weatherResponse ->

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
                    _isLoadingApi.value = false
                }, onFailure = { exception ->
                    _weatherCardState.value = WeatherCardUiState.Failure(
                        exception.message ?: "Error occurred"
                    )
                    _isLoadingApi.value = false
                })
            }
        }.onFailure { exception ->
            _weatherCardState.value = WeatherCardUiState.Failure(
                exception.message ?: "Failed to get location"
            )
            _isLoadingApi.value = false
        }
    }

    // ========= Case 2: Outfit Screen - 날씨 자동 업데이트 =========
    fun updatePendingOutfitWeather() = viewModelScope.launch {
        val repository = outfitRepository ?: run {
            Log.e("WeatherViewModel", "OutfitRepository not set")
            return@launch
        }

        val pendingOutfits = repository.getPendingWeatherOutfits()

        pendingOutfits.forEach { outfit ->
            // 착용 시간대의 중간 시점 날씨 조회
            val midTime = (outfit.wornStartTime + outfit.wornEndTime) / 2
            getTimemachineWeather(outfit.oid, outfit.latitude, outfit.longitude, midTime)
        }
    }

    private fun getTimemachineWeather(
        oid: String, latitude: Double, longitude: Double, datetime: Long
    ) {
        viewModelScope.launch {
            val repository = outfitRepository ?: return@launch

            openWeatherRepository.getTimemachineWeather(
                latitude, longitude, datetime
            ).collect { result ->
                result.fold(onSuccess = { weatherResponse ->
                    val weatherData = weatherResponse.data.firstOrNull()

                    if (weatherData != null) {
                        repository.updateOutfitWeather(
                            oid = oid,
                            temperatureAvg = weatherData.temp,
                            temperatureMin = weatherData.temp,
                            temperatureMax = weatherData.temp,
                            description = weatherData.weather.firstOrNull()?.description ?: "",
                            iconCode = weatherData.weather.firstOrNull()?.icon ?: "",
                            windSpeed = weatherData.windSpeed,
                            precipitation = weatherData.rain?.`1h` ?: 0.0
                        )
                    }
                }, onFailure = { exception ->
                    Log.e(
                        "WeatherViewModel",
                        "Failed to fetch timemachine weather: ${exception.message}"
                    )
                })
            }
        }
    }

    // ========== 동기화 관련 ==========
    fun syncUnsyncedData() = viewModelScope.launch {
        weatherRepository.syncUnsyncedData()
    }

    fun startRealtimeSync(uid: String) {
        weatherRepository.startRealtimeSync(uid)
    }

    // ====== RoomDB 함수 ======
    // ### 현재 사용자의 날씨 목록 로드 ###
    fun loadWeathersFromDB() = viewModelScope.launch {
        weatherRepository.getWeatherByCurrentUser()?.catch { e ->
                e.printStackTrace()
                _weatherList.value = emptyList()
            }?.collect { weatherList ->
                _weatherList.value = weatherList
            }
    }

    // ====== 공통 기능 ======
    fun hasLocationPermission(): Boolean {
        return locationManager.hasLocationPermission()
    }

    fun getCurrentLocation() = viewModelScope.launch {
        val result = locationManager.getCurrentLocation()
        result.onSuccess { location ->
            _currentLocation.value = LocationManager.Coordinates.fromLocation(location)
        }
    }

    fun resetWeatherCardState() {
        _weatherCardState.value = WeatherCardUiState.Idle
    }
}

// =========== Open Weather API 상태 클래스 ===========
// 날씨 작업 상태
sealed class WeatherOperationState {
    object Idle : WeatherOperationState()
    object Loading : WeatherOperationState()
    data class Success(val message: String) : WeatherOperationState()
    data class Failure(val message: String) : WeatherOperationState()
}

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
    data class Failure(val message: String) : WeatherCardUiState()
}