package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.BuildConfig
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.WeatherEntity
import com.fitfit.app.data.repository.OpenWeatherRepository
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
    private val weatherDao = AppDatabase.getDatabase(application).weatherDao()
    private val weatherRepository = WeatherRepository(weatherDao, application)
    private val locationManager = LocationManager(application)

    // ========= State Flows =========

    // Case1: HomeScreen WeatherCard 상태 플로우 (현재 날씨 from OpenWeatherAPI)
    private val _weatherCardState = MutableStateFlow<WeatherCardUiState>(WeatherCardUiState.Idle)
    val weatherCardState: StateFlow<WeatherCardUiState> = _weatherCardState.asStateFlow()

    // Case2: OutfitAddCard 상태 플로우 (outfit 입었던 날씨 from OpenWeatherAPI)
//    private val _outfitWeatherState = MutableStateFlow<OutfitWeatherState>(OutfitWeatherState.Idle)
//    val outfitWeatherState: StateFlow<OutfitWeatherState> = _outfitWeatherState.asStateFlow()

    // Case3: OutfitCard
    private val _isLoadingApi = MutableStateFlow(false)

    val isLoadingApi: StateFlow<Boolean> = _isLoadingApi
    // ====== RoomDB 변수 및 상태 플로우 ======

    private val _weatherList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherList: StateFlow<List<WeatherEntity>> = _weatherList

    private val _weatherSaveToDBState =
        MutableStateFlow<WeatherOperationState>(WeatherOperationState.Idle)
    val weatherSaveToDBState: StateFlow<WeatherOperationState> = _weatherSaveToDBState

    // ====== Location Manager ======
    private val _currentLocation = MutableStateFlow<LocationManager.Coordinates?>(null)
    val currentLocation: StateFlow<LocationManager.Coordinates?> = _currentLocation

    // ### 현재 위치 가져오기 ###
    fun getCurrentLocation() = viewModelScope.launch {
        val result = locationManager.getCurrentLocation()

        result.onSuccess { location ->
            _currentLocation.value = LocationManager.Coordinates.fromLocation(location)
        }.onFailure { exception ->
            _weatherCardState.value = WeatherCardUiState.Failure(
                exception.message ?: "위치를 가져올 수 없습니다."
            )
        }
    }

    // ====== OpenWeatherAPI 함수 ======
    // ### Weather Card 날씨 정보 가져오기 ###
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
                coordinates.latitude,
                coordinates.longitude
            ).collect { result ->
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
                        _isLoadingApi.value = false
                    },
                    onFailure = { exception ->
                        _weatherCardState.value = WeatherCardUiState.Failure(
                            exception.message ?: "Error occurred"
                        )
                        _isLoadingApi.value = false
                    }
                )
            }
        }.onFailure { exception ->
            _weatherCardState.value = WeatherCardUiState.Failure(
                exception.message ?: "Failed to get location"
            )
            _isLoadingApi.value = false
        }
    }


    // ### 과거 날씨 정보 가져오기 ###
//    fun fetchTimemachineWeather(latitude: Double, longitude: Double, timestamp: Long) {
//        viewModelScope.launch {
//            _openWeatherState.value = OpenWeatherState.Loading
//
//            openWeatherRepository.getTimemachineWeather(latitude, longitude, timestamp)
//                .collect { result ->
//                    result.fold(
//                        onSuccess = { weatherResponse ->
//                            _openWeatherState.value = OpenWeatherState.Success(weatherResponse)
//                            _isLoadingApi.value = false
//                        },
//                        onFailure = { exception ->
//                            _openWeatherState.value = OpenWeatherState.Failure(
//                                exception.message ?: "Error occurred"
//                            )
//                            _isLoadingApi.value = false
//                        }
//                    )
//                }
//        }
//    }

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
        _weatherSaveToDBState.value = WeatherOperationState.Loading

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
            _weatherSaveToDBState.value = WeatherOperationState.Success("날씨 정보가 저장되었습니다.")
            loadWeathersFromDB()
        }.onFailure { exception ->
            _weatherSaveToDBState.value = WeatherOperationState.Failure(
                exception.message ?: "날씨 정보 저장 실패"
            )
        }

    }

    // ### 날씨 삭제 ###
    fun deleteWeather(wid: String) = viewModelScope.launch {
        val result = weatherRepository.deleteWeather(wid)
        result.onSuccess {
            loadWeathersFromDB()
        }
    }

}

// Open Weather API 상태 클래스


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