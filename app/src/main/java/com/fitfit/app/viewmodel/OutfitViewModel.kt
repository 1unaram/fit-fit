package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.repository.OutfitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : AndroidViewModel(application) {
    private val outfitDao = AppDatabase.getDatabase(application).outfitDao()
    private val outfitClothesDao = AppDatabase.getDatabase(application).outfitClothesDao()
    private val repository = OutfitRepository(outfitDao, outfitClothesDao, application)

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

    /**
     * 현재 사용자의 코디 목록 로드
     */
    fun loadOutfits() = viewModelScope.launch {
        repository.getOutfitsByCurrentUser()
            ?.catch { e ->
                e.printStackTrace()
                _outfitsList.value = emptyList()
            }
            ?.collect { outfits ->
                _outfitsList.value = outfits
            }
    }

    /**
     * 옷 정보 포함 코디 목록 로드
     */
    fun loadOutfitsWithClothes() = viewModelScope.launch {
        repository.getOutfitsWithClothesByCurrentUser()
            ?.catch { e ->
                e.printStackTrace()
                _outfitsWithClothes.value = emptyList()
            }
            ?.collect { outfits ->
                _outfitsWithClothes.value = outfits
            }
    }

    /**
     * 코디 생성
     */
    fun createOutfit(name: String, clothesIds: List<String>) = viewModelScope.launch {
        _createState.value = OutfitOperationState.Loading

        // 입력 검증
        if (name.isBlank()) {
            _createState.value = OutfitOperationState.Failure("코디 이름을 입력해주세요.")
            return@launch
        }

        if (clothesIds.isEmpty()) {
            _createState.value = OutfitOperationState.Failure("최소 한 개 이상의 옷을 선택해주세요.")
            return@launch
        }

//        val result = repository.createOutfit(clothesIds)
//
//        result.onSuccess { oid ->
//            _createState.value = OutfitOperationState.Success("코디가 생성되었습니다.")
//            loadOutfits()
//            loadOutfitsWithClothes()
//        }.onFailure { exception ->
//            _createState.value = OutfitOperationState.Failure(
//                exception.message ?: "코디 생성 실패"
//            )
//        }
    }

    /**
     * 코디 수정
     */
    fun updateOutfit(oid: String, name: String, clothesIds: List<String>) = viewModelScope.launch {
        _updateState.value = OutfitOperationState.Loading

        if (name.isBlank()) {
            _updateState.value = OutfitOperationState.Failure("코디 이름을 입력해주세요.")
            return@launch
        }

        if (clothesIds.isEmpty()) {
            _updateState.value = OutfitOperationState.Failure("최소 한 개 이상의 옷을 선택해주세요.")
            return@launch
        }

//        val result = repository.updateOutfit(oid, name, clothesIds)
//
//        result.onSuccess {
//            _updateState.value = OutfitOperationState.Success("코디가 수정되었습니다.")
//            loadOutfits()
//            loadOutfitsWithClothes()
//        }.onFailure { exception ->
//            _updateState.value = OutfitOperationState.Failure(
//                exception.message ?: "코디 수정 실패"
//            )
//        }
    }

    /**
     * 코디 삭제
     */
    fun deleteOutfit(oid: String) = viewModelScope.launch {
        _deleteState.value = OutfitOperationState.Loading

        val result = repository.deleteOutfit(oid)

        result.onSuccess {
            _deleteState.value = OutfitOperationState.Success("코디가 삭제되었습니다.")
            loadOutfits()
            loadOutfitsWithClothes()
        }.onFailure { exception ->
            _deleteState.value = OutfitOperationState.Failure(
                exception.message ?: "코디 삭제 실패"
            )
        }
    }

    /**
     * 동기화되지 않은 데이터 재동기화
     */
    fun syncUnsyncedData() = viewModelScope.launch {
        repository.syncUnsyncedData()
    }

    /**
     * 실시간 동기화 시작
     */
    fun startRealtimeSync(uid: String) {
        repository.startRealtimeSync(uid)
    }

    /**
     * 상태 초기화
     */
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