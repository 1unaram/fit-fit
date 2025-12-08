package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.repository.OutfitRepository
import com.fitfit.app.data.util.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : AndroidViewModel(application) {
    private var outfitRepository: OutfitRepository? = null
    fun setOutfitRepository(repo: OutfitRepository) {
        outfitRepository = repo
    }

    private val _outfitsWithClothes = MutableStateFlow<List<OutfitWithClothes>>(emptyList())
    val outfitsWithClothes: StateFlow<List<OutfitWithClothes>> = _outfitsWithClothes

    private val _createState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val createState: StateFlow<OutfitOperationState> = _createState

    private val _updateState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val updateState: StateFlow<OutfitOperationState> = _updateState

    private val _deleteState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val deleteState: StateFlow<OutfitOperationState> = _deleteState

    private val locationManager = LocationManager(application)

    // ### 옷 정보 포함 코디 목록 로드 ###
    fun loadOutfitsWithClothes() = viewModelScope.launch {
        val repo = outfitRepository ?: return@launch
        repo.getOutfitsWithClothesByCurrentUser()
            ?.catch {
                it.printStackTrace()
                _outfitsWithClothes.value = emptyList()
            }
            ?.collect { list ->
                _outfitsWithClothes.value = list
            }
    }

    // ### 코디 생성 ###
    fun createOutfit(
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long
    ) = viewModelScope.launch {
        _createState.value = OutfitOperationState.Loading

        if (clothesIds.isEmpty()) {
            _createState.value = OutfitOperationState.Failure("Choose at least one piece of clothing.")
            return@launch
        }
        if (wornEndTime <= wornStartTime) {
            _createState.value = OutfitOperationState.Failure("Invalid worn time range.")
            return@launch
        }

        // 위치 정보 가져오기
        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->
            val result = outfitRepository?.createOutfit(
                clothesIds = clothesIds,
                occasion = occasion,
                comment = comment,
                wornStartTime = wornStartTime,
                wornEndTime = wornEndTime,
                latitude = location.latitude,
                longitude = location.longitude
            )

            result?.onSuccess {
                _createState.value = OutfitOperationState.Success("Successfully created outfit.")
                loadOutfitsWithClothes()
            }?.onFailure {
                _createState.value = OutfitOperationState.Failure(
                    it.message ?: "Failed to create outfit."
                )
            }
        }.onFailure { exception ->
            _createState.value = OutfitOperationState.Failure(
                exception.message ?: "Failed to get location"
            )
        }
    }

    // ### 코디 수정 ###
    fun updateOutfit(
        oid: String,
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long
    ) = viewModelScope.launch {
        _updateState.value = OutfitOperationState.Loading

        if (clothesIds.isEmpty()) {
            _updateState.value = OutfitOperationState.Failure("Choose at least one piece of clothing.")
            return@launch
        }
        if (wornEndTime <= wornStartTime) {
            _updateState.value = OutfitOperationState.Failure("Invalid worn time range.")
            return@launch
        }

        // 위치 정보 가져오기
        val locationResult = locationManager.getCurrentLocation()

        locationResult.onSuccess { location ->
            val result = outfitRepository?.updateOutfit(
                oid = oid,
                clothesIds = clothesIds,
                occasion = occasion,
                comment = comment,
                wornStartTime = wornStartTime,
                wornEndTime = wornEndTime,
                latitude = location.latitude,
                longitude = location.longitude
            )

            result?.onSuccess {
                _updateState.value = OutfitOperationState.Success("Successfully updated outfit.")
                loadOutfitsWithClothes()
            }?.onFailure {
                _updateState.value =
                    OutfitOperationState.Failure(it.message ?: "Failed to update outfit.")
            }
        }.onFailure { e ->
            _updateState.value =
                OutfitOperationState.Failure(e.message ?: "Failed to get location")
        }
    }

    // ### 코디 삭제 ###
    fun deleteOutfit(oid: String) = viewModelScope.launch {
        _deleteState.value = OutfitOperationState.Loading

        val result = outfitRepository?.deleteOutfit(oid)

        result?.onSuccess { _:Unit ->
            _deleteState.value = OutfitOperationState.Success("Successfully deleted outfit.")
            loadOutfitsWithClothes()
        }?.onFailure { e: Throwable ->
            _deleteState.value = OutfitOperationState.Failure(
                e.message ?: "Failed to delete outfit."
            )
        }
    }


    // ====== Firebase 동기화 관련 ======
    fun syncUnsyncedData() = viewModelScope.launch {
        outfitRepository?.syncUnsyncedData()
    }

    fun startRealtimeSync(uid: String) {
        outfitRepository?.startRealtimeSync(uid)
    }

    // ====== State 초기화 ======
    fun resetCreateState() {
        _createState.value = OutfitOperationState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = OutfitOperationState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = OutfitOperationState.Idle
    }
}

// 코디 작업 상태
sealed class OutfitOperationState {
    object Idle : OutfitOperationState()
    object Loading : OutfitOperationState()
    data class Success(val message: String) : OutfitOperationState()
    data class Failure(val message: String) : OutfitOperationState()
}