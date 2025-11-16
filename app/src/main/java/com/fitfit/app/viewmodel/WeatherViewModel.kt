package com.fitfit.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.BuildConfig
import com.fitfit.app.data.repository.OpenWeatherRepository
import com.fitfit.app.data.repository.OutfitRepository
import com.fitfit.app.data.util.LocationManager
import com.fitfit.app.data.util.WeatherAggregator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
            fetchAndAggregateWeatherForOutfit(
                outfitId = outfit.oid,
                wornStartTime = outfit.wornStartTime,
                wornEndTime = outfit.wornEndTime,
                latitude = outfit.latitude,
                longitude = outfit.longitude
            )
        }
    }

    /**
     * 특정 Outfit의 시간 구간별 날씨를 조회하고 집계하여 업데이트
     */
    private fun fetchAndAggregateWeatherForOutfit(
        outfitId: String,
        wornStartTime: Long,
        wornEndTime: Long,
        latitude: Double,
        longitude: Double
    ) = viewModelScope.launch {
        val repository = outfitRepository ?: return@launch

        try {
            // 1. 1시간 간격 타임스탬프 생성
            val timestamps = WeatherAggregator.generateHourlyTimestamps(wornStartTime, wornEndTime)

            // 2. 각 타임스탬프별로 병렬로 날씨 조회
            val weatherDataList = timestamps.map { timestamp ->
                async {
                    fetchWeatherAtTimestamp(latitude, longitude, timestamp)
                }
            }.awaitAll().filterNotNull()

            if (weatherDataList.isEmpty()) {
                return@launch
            }

            // 3. 날씨 데이터 집계
            val aggregated = WeatherAggregator.aggregateWeatherData(weatherDataList)

            if (aggregated != null) {
                // 4. DB 업데이트
                repository.updateOutfitWeather(
                    oid = outfitId,
                    temperatureAvg = aggregated.temperatureAvg,
                    temperatureMin = aggregated.temperatureMin,
                    temperatureMax = aggregated.temperatureMax,
                    description = aggregated.mostCommonDescription,
                    iconCode = aggregated.mostCommonIcon,
                    windSpeed = aggregated.windSpeedAvg,
                    precipitation = aggregated.precipitationAvg
                )
            }

        } catch (e: Exception) {
        }
    }

    /**
     * 특정 시간의 날씨 조회 (단일 API 호출)
     */
    private suspend fun fetchWeatherAtTimestamp(
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): WeatherAggregator.WeatherData? {
        return try {
            val result = openWeatherRepository.getTimemachineWeather(
                latitude,
                longitude,
                timestamp / 1000 // 초 단위로 변환
            ).first()

            result.fold(
                onSuccess = { response ->
                    Log.d("WeatherViewModel", "API Response: lat=${response.lat}, lon=${response.lon}, data size=${response.data?.size}")

                    // data 배열의 첫 번째 요소 사용
                    response.data?.firstOrNull()?.let { data ->
                        Log.d("WeatherViewModel", """
                            Weather data found:
                            - temp: ${data.temp}
                            - weather: ${data.weather?.firstOrNull()?.description}
                            - windSpeed: ${data.windSpeed}
                            - rain: ${data.rain}
                        """.trimIndent())

                        WeatherAggregator.WeatherData(
                            temperature = data.temp ?: 0.0,
                            weatherDescription = data.weather?.firstOrNull()?.description ?: "알 수 없음",
                            weatherIcon = data.weather?.firstOrNull()?.icon ?: "",
                            windSpeed = data.windSpeed ?: 0.0,
                            precipitation = data.rain?.oneHour ?: 0.0  // 수정된 방식
                        )
                    } ?: run {
                        Log.e("WeatherViewModel", "data array is empty or null")
                        null
                    }
                },
                onFailure = { exception ->
//                    Log.e("WeatherViewModel", "API call failed at timestamp $timestampInSeconds", exception)
                    null
                }
            )
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Failed to fetch weather at timestamp $timestamp", e)
            null
        }
    }


    // ====== RoomDB 함수 ======

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