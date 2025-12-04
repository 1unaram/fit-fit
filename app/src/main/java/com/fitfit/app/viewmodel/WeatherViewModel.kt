package com.fitfit.app.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.BuildConfig
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.WeatherUpdateStatus
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit


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

    private val _weatherFilterState = MutableStateFlow<WeatherFilterUiState>(WeatherFilterUiState.Idle)
    val weatherFilterState: StateFlow<WeatherFilterUiState> = _weatherFilterState.asStateFlow()

    private val _weatherScreenState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Idle)
    val weatherScreenState: StateFlow<WeatherScreenState> = _weatherScreenState.asStateFlow()

    private val _isLoadingApi = MutableStateFlow(false)
    val isLoadingApi: StateFlow<Boolean> = _isLoadingApi

    private val _locationName = MutableStateFlow<String?>(null)
    val locationName: StateFlow<String?> = _locationName.asStateFlow()


    // ========== Case 1: HomeScreen WeatherCard ==========
    // ### Weather Card용 날씨 정보 조회 ###
    @SuppressLint("DefaultLocale")
    fun getWeatherCardData() = viewModelScope.launch {
        _isLoadingApi.value = true
        _weatherCardState.value = WeatherCardUiState.Loading

        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->

            // 1. 현재 위치 가져오기
            val lat = location.latitude
            val lon = location.longitude

            // 2. Weather Card용 날씨 정보 가져오기
            // Weather API와 Geo(위치명) API 병렬로 요청
            val weatherDeferred = async {
                openWeatherRepository.getCurrentWeather(lat, lon).firstOrNull()
            }
            val locationNameDeferred = async {
                openWeatherRepository.getLocationName(lat, lon).firstOrNull()
                    ?.getOrNull()?.firstOrNull()?.let { geoResult ->
                        geoResult.name + (geoResult.state?.let { ", $it" } ?: "") + ", " + geoResult.country
                    }
            }

            val weatherResult = weatherDeferred.await()
            val fetchedLocationName = locationNameDeferred.await()
            _locationName.value = fetchedLocationName

            weatherResult?.fold(onSuccess = { weatherResponse ->

                val cardData = WeatherCardData(
                    todayWeatherIconCode = weatherResponse.daily?.firstOrNull()?.weather?.firstOrNull()?.icon,
                    currentTemperature = String.format("%.1f", weatherResponse.current.temp).toDouble(),
                    todayMinTemperature = weatherResponse.daily?.firstOrNull()?.temp?.min?.let {
                        String.format("%.1f", it).toDouble()
                    },
                    todayMaxTemperature = weatherResponse.daily?.firstOrNull()?.temp?.max?.let {
                        String.format("%.1f", it).toDouble()
                    },
                    todayWeatherDescription = weatherResponse.daily?.firstOrNull()?.weather?.firstOrNull()?.description,
                    probabilityOfPrecipitation = (weatherResponse.daily?.firstOrNull()?.pop?.times(100))?.toInt() ?: 0,
                    windSpeed = weatherResponse.daily?.firstOrNull()?.windSpeed?.let {
                        String.format("%.1f", it).toDouble()
                    },
                    locationName = fetchedLocationName ?: "Unknown Location"
                )
                _weatherCardState.value = WeatherCardUiState.Success(cardData)
                _isLoadingApi.value = false
            }, onFailure = { exception ->
                _weatherCardState.value = WeatherCardUiState.Failure(
                    exception.message ?: "Error occurred"
                )
                _isLoadingApi.value = false
            })
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
            // 1) 집계 시작 전에 상태를 UPDATING 으로 변경
            val updatingOutfit: OutfitEntity = outfit.copy(
                weatherUpdateStatus = WeatherUpdateStatus.UPDATING.name
            )
            repository.updateOutfitStatus(updatingOutfit)

            // 2) 실제 날씨 조회 + 집계
            fetchAndAggregateWeatherForOutfit(
                outfitId = outfit.oid,
                wornStartTime = outfit.wornStartTime,
                wornEndTime = outfit.wornEndTime,
                latitude = outfit.latitude,
                longitude = outfit.longitude
            )
        }
    }

    // ### 특정 Outfit의 시간 구간별 날씨를 조회하고 집계하여 업데이트 ###
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
            e.printStackTrace()
        }
    }

    // ### 특정 과거 시간의 날씨 조회 (단일 API 호출) ###
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
                    Log.d("WeatherViewModel", "API Response: lat=${response.lat}, lon=${response.lon}, data size=${response.data.size}")

                    // data 배열의 첫 번째 요소 사용
                    response.data.firstOrNull()?.let { data ->
                        WeatherAggregator.WeatherData(
                            temperature = data.temp,
                            weatherDescription = data.weather.firstOrNull()?.description,
                            weatherIcon = data.weather.firstOrNull()?.icon ?: "",
                            windSpeed = data.windSpeed,
                            precipitation = data.rain?.oneHour ?: 0.0
                        )
                    } ?: run {
                        Log.e("WeatherViewModel", "data array is empty or null")
                        null
                    }
                },
                onFailure = { exception ->
                    null
                }
            )
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Failed to fetch weather at timestamp $timestamp", e)
            null
        }
    }

    // ========= Case 3: Outfit Screen - Weather Filter =========
    // ### 특정 미래 날짜의 날씨 정보 조회 ###
    @SuppressLint("DefaultLocale")
    fun getWeatherFilterData(selectedDate: LocalDate) = viewModelScope.launch {
        _isLoadingApi.value = true
        _weatherFilterState.value = WeatherFilterUiState.Loading

        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->

            val lat = location.latitude
            val lon = location.longitude

            val weatherResult = openWeatherRepository.getForecastWeather(lat, lon).firstOrNull()

            weatherResult?.fold(onSuccess = { forecastResponse ->

                val today = LocalDate.now()
                val dayIndex = ChronoUnit.DAYS.between(today, selectedDate).toInt()

                // 유효한 범위 확인 (0 ~ 7)
                if (dayIndex in 0..7 && forecastResponse.daily != null &&
                    dayIndex < forecastResponse.daily.size) {

                    val targetDayWeather = forecastResponse.daily[dayIndex]

                val weatherFilterValue = WeatherFilterData(
                    temperature = targetDayWeather.temp.day.let {
                        String.format("%.1f", it).toDouble()
                    },
                    weather = targetDayWeather.weather.firstOrNull()?.main
                )

                    _weatherFilterState.value = WeatherFilterUiState.Success(weatherFilterValue)
                    _isLoadingApi.value = false
                } else {
                    _weatherFilterState.value = WeatherFilterUiState.Failure(
                        "Selected date is out of forecast range"
                    )
                    _isLoadingApi.value = false
                    return@launch
                }
            }, onFailure = { exception ->
                _weatherFilterState.value = WeatherFilterUiState.Failure(
                    exception.message ?: "Error occurred"
                )
                _isLoadingApi.value = false
            })
        }.onFailure { exception ->
            _weatherFilterState.value = WeatherFilterUiState.Failure(
                exception.message ?: "Failed to get location"
            )
            _isLoadingApi.value = false
        }
    }

    // ========= Case 4: Weather Screen =========
    fun getWeatherScreenData() = viewModelScope.launch {
        _weatherScreenState.value = WeatherScreenState.Loading

        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->
            val coordinates = LocationManager.Coordinates.fromLocation(location)

            openWeatherRepository.getTodayAndWeeklyWeather(
                coordinates.latitude,
                coordinates.longitude
            ).collect { result ->
                result.fold(
                    onSuccess = { weatherResponse ->
                        // 시간별 날씨 (오늘 24시간)
                        val now = System.currentTimeMillis() / 1000
                        val endOfDay = now + (24 * 3600)

                        val hourlyList = weatherResponse.hourly
                            ?.filter { it.dt in now..endOfDay }
                            ?.take(24)
                            ?.map { hourly ->
                                HourlyWeatherData(
                                    dt = hourly.dt,
                                    temp = hourly.temp,
                                    weatherDescription = hourly.weather.firstOrNull()?.description ?: "",
                                    weatherIcon = hourly.weather.firstOrNull()?.icon ?: "",
                                )
                            } ?: emptyList()

                        // 일별 날씨 (7일)
                        val dailyList = weatherResponse.daily
                            ?.take(7)
                            ?.map { daily ->
                                DailyWeatherData(
                                    dt = daily.dt,
                                    tempMin = daily.temp.min,
                                    tempMax = daily.temp.max,
                                    weatherIcon = daily.weather.firstOrNull()?.icon ?: "",
                                    pop = daily.pop
                                )
                            } ?: emptyList()

                        _weatherScreenState.value = WeatherScreenState.Success(
                            hourlyList = hourlyList,
                            dailyList = dailyList
                        )
                    },
                    onFailure = { exception ->
                        _weatherScreenState.value = WeatherScreenState.Failure(
                            exception.message ?: "Failed to get weather data"
                        )
                    }
                )
            }
        }.onFailure { exception ->
            _weatherScreenState.value = WeatherScreenState.Failure(
                exception.message ?: "Failed to get location"
            )
        }
    }

    // ========= Reset Handling =========
    fun resetWeatherCardState() {
        _weatherCardState.value = WeatherCardUiState.Idle
    }

    fun resetWeatherScreenState() {
        _weatherScreenState.value = WeatherScreenState.Idle
    }
}

// =========== State & Class ===========
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
    val windSpeed: Double?,
    val locationName: String? = null
)

sealed class WeatherScreenState {
    object Idle : WeatherScreenState()
    object Loading : WeatherScreenState()
    data class Success(
        val hourlyList: List<HourlyWeatherData>,
        val dailyList: List<DailyWeatherData>
    ) : WeatherScreenState()
    data class Failure(val message: String) : WeatherScreenState()
}

data class HourlyWeatherData(
    val dt: Long,
    val temp: Double,
    val weatherDescription: String,
    val weatherIcon: String
)

data class DailyWeatherData(
    val dt: Long,
    val tempMin: Double,
    val tempMax: Double,
    val weatherIcon: String,
    val pop: Double
)

data class WeatherFilterData(
    val temperature: Double?,
    val weather: String?,
)

sealed class WeatherCardUiState {
    object Idle : WeatherCardUiState()
    object Loading : WeatherCardUiState()
    data class Success(val cardData: WeatherCardData) : WeatherCardUiState()
    data class Failure(val message: String) : WeatherCardUiState()
}

sealed class WeatherFilterUiState {
    object Idle : WeatherFilterUiState()
    object Loading : WeatherFilterUiState()
    data class Success(val weatherFilterState: WeatherFilterData) : WeatherFilterUiState()
    data class Failure(val message: String) : WeatherFilterUiState()
}