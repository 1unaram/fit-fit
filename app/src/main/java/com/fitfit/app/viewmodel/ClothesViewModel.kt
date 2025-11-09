package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.repository.ClothesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ClothesViewModel(application: Application) : AndroidViewModel(application) {
    private val clothesDao = AppDatabase.getDatabase(application).clothesDao()
    private val repository = ClothesRepository(clothesDao, application)

    private val _clothesList = MutableStateFlow<List<ClothesEntity>>(emptyList())
    val clothesList: StateFlow<List<ClothesEntity>> = _clothesList

    private val _insertState = MutableStateFlow<ClothesOperationState>(ClothesOperationState.Idle)
    val insertState: StateFlow<ClothesOperationState> = _insertState

    private val _updateState = MutableStateFlow<ClothesOperationState>(ClothesOperationState.Idle)
    val updateState: StateFlow<ClothesOperationState> = _updateState

    private val _deleteState = MutableStateFlow<ClothesOperationState>(ClothesOperationState.Idle)
    val deleteState: StateFlow<ClothesOperationState> = _deleteState

    // ### 현재 사용자의 옷 목록 불러오기 ###
    fun loadClothes() = viewModelScope.launch {
        repository.getClothesByCurrentUser()
            ?.catch { e ->
                e.printStackTrace()
                _clothesList.value = emptyList()
            }
            ?.collect { clothes ->
                _clothesList.value = clothes
            }
    }

    /**
     * 옷 추가
     */
    fun insertClothes(
        name: String,
        category: String
    ) = viewModelScope.launch {
        _insertState.value = ClothesOperationState.Loading

        // 입력 검증
        if (name.isBlank()) {
            _insertState.value = ClothesOperationState.Failure("옷 이름을 입력해주세요.")
            return@launch
        }

        if (category.isBlank()) {
            _insertState.value = ClothesOperationState.Failure("카테고리를 선택해주세요.")
            return@launch
        }

        val result = repository.insertClothes(name, category)

        result.onSuccess { cid ->
            _insertState.value = ClothesOperationState.Success("옷이 추가되었습니다.")
            loadClothes() // 목록 새로고침
        }.onFailure { exception ->
            _insertState.value = ClothesOperationState.Failure(
                exception.message ?: "옷 추가 실패"
            )
        }
    }

    /**
     * 옷 수정
     */
    fun updateClothes(clothes: ClothesEntity) = viewModelScope.launch {
        _updateState.value = ClothesOperationState.Loading

        val result = repository.updateClothes(clothes)

        result.onSuccess {
            _updateState.value = ClothesOperationState.Success("옷이 수정되었습니다.")
            loadClothes()
        }.onFailure { exception ->
            _updateState.value = ClothesOperationState.Failure(
                exception.message ?: "옷 수정 실패"
            )
        }
    }

    /**
     * 옷 삭제
     */
    fun deleteClothes(cid: String) = viewModelScope.launch {
        _deleteState.value = ClothesOperationState.Loading

        val result = repository.deleteClothes(cid)

        result.onSuccess {
            _deleteState.value = ClothesOperationState.Success("옷이 삭제되었습니다.")
            loadClothes()
        }.onFailure { exception ->
            _deleteState.value = ClothesOperationState.Failure(
                exception.message ?: "옷 삭제 실패"
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
    fun resetInsertState() {
        _insertState.value = ClothesOperationState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = ClothesOperationState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = ClothesOperationState.Idle
    }
}

// 옷 작업 상태
sealed class ClothesOperationState {
    object Idle : ClothesOperationState()
    object Loading : ClothesOperationState()
    data class Success(val message: String) : ClothesOperationState()
    data class Failure(val message: String) : ClothesOperationState()
}