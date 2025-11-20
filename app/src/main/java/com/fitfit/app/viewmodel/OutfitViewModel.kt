package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : AndroidViewModel(application) {
    private var outfitRepository: OutfitRepository? = null
    fun setOutfitRepository(repo: OutfitRepository) {
        outfitRepository = repo
    }

    private val _outfitsList = MutableStateFlow<List<OutfitEntity>>(emptyList())
    val outfitsList: StateFlow<List<OutfitEntity>> = _outfitsList

    private val _outfitsWithClothes = MutableStateFlow<List<OutfitWithClothes>>(emptyList())
    val outfitsWithClothes: StateFlow<List<OutfitWithClothes>> = _outfitsWithClothes

    private val _createState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val createState: StateFlow<OutfitOperationState> = _createState

    private val _updateState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val updateState: StateFlow<OutfitOperationState> = _updateState

    private val _deleteState = MutableStateFlow<OutfitOperationState>(OutfitOperationState.Idle)
    val deleteState: StateFlow<OutfitOperationState> = _deleteState

    // 옷 정보 포함 코디 목록 로드
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

    // ====== 생성 / 수정 / 삭제 ======
    /**
     * 코디 생성
     *
     * @param clothesIds 포함된 옷 cid 리스트
     * @param wornStartTime 착용 시작 시간 (millis)
     * @param wornEndTime 착용 종료 시간 (millis)
     * @param latitude 착용 위치 위도
     * @param longitude 착용 위치 경도
     */
    fun createOutfit(
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long,
        latitude: Double,
        longitude: Double
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

        val result = outfitRepository?.createOutfit(
            clothesIds = clothesIds,
            occasion = occasion,
            comment = comment,
            wornStartTime = wornStartTime,
            wornEndTime = wornEndTime,
            latitude = latitude,
            longitude = longitude
        )

        result?.onSuccess {
            _createState.value = OutfitOperationState.Success("Successfully created outfit.")
            loadOutfitsWithClothes()
        }?.onFailure {
            _createState.value = OutfitOperationState.Failure(
                it.message ?: "Failed to create outfit."
            )
        }
    }

    /**
     * 코디 기본 정보 수정 (이름 + 옷 구성)
     * 착용 시간/위치/날씨 통계는 별도 화면에서 수정한다고 가정
     */
    fun updateOutfit(
        oid: String,
        clothesIds: List<String>
    ) = viewModelScope.launch {
        _updateState.value = OutfitOperationState.Loading

        if (clothesIds.isEmpty()) {
            _updateState.value = OutfitOperationState.Failure("Choose at least one piece of clothing.")
            return@launch
        }

        val result = outfitRepository?.updateOutfit(
            oid = oid,
            clothesIds = clothesIds
        )

        result?.onSuccess {
            _updateState.value = OutfitOperationState.Success("Successfully updated outfit.")
            loadOutfitsWithClothes()
        }?.onFailure {
            _updateState.value = OutfitOperationState.Failure(
                it.message ?: "Failed to update outfit."
            )
        }
    }

    /**
     * 코디 삭제
     */
    fun deleteOutfit(oid: String) = viewModelScope.launch {
        _deleteState.value = OutfitOperationState.Loading

        val result = outfitRepository?.deleteOutfit(oid)

        result?.onSuccess { _:Unit ->
            _deleteState.value = OutfitOperationState.Success("코디가 삭제되었습니다.")
            loadOutfitsWithClothes()
        }?.onFailure { e: Throwable ->
            _deleteState.value = OutfitOperationState.Failure(
                e.message ?: "코디 삭제에 실패했습니다."
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