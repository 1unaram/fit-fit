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


    // ### 현재 사용자의 옷 목록 불러오기 ###
    fun loadClothes() = viewModelScope.launch {
        repository.getClothesByCurrentUser()
            ?.catch { _clothesList.value = emptyList() }
            ?.collect { _clothesList.value = it }
    }

    // ### 옷 추가 ###
    fun insertClothes(
        imagePath: String,
        category: String,
        nickname: String,
        storeUrl: String?
    ) = viewModelScope.launch {
        _insertState.value = ClothesOperationState.Loading

        if (imagePath.isBlank()) {
            _insertState.value = ClothesOperationState.Failure("Choose an image for the clothes.")
            return@launch
        }
        if (category.isBlank()) {
            _insertState.value = ClothesOperationState.Failure("Choose a category for the clothes.")
            return@launch
        }
        if (nickname.isBlank()) {
            _insertState.value = ClothesOperationState.Failure("Enter a nickname for the clothes.")
            return@launch
        }

        val result = repository.insertClothes(imagePath, category, nickname, storeUrl)
        result.onSuccess {
            _insertState.value = ClothesOperationState.Success("Successfully added clothes.")
            loadClothes()
        }.onFailure { e ->
            _insertState.value = ClothesOperationState.Failure(e.message ?: "Failed to add clothes.")
        }
    }

    // ### 옷 수정 ###
    fun updateClothes(
        cid: String,
        category: String,
        nickname: String,
        storeUrl: String?
    ) = viewModelScope.launch {
        repository.updateClothes(cid, category, nickname, storeUrl)
        loadClothes()
    }

    // ### 옷 삭제 ###
    fun deleteClothes(cid: String) = viewModelScope.launch {
        repository.deleteClothes(cid)
        loadClothes()
    }

    // 동기화되지 않은 데이터 재동기화
    fun syncUnsyncedData() = viewModelScope.launch {
        repository.syncUnsyncedData()
    }

    fun startRealtimeSync(uid: String) {
        repository.startRealtimeSync(uid)
    }

    fun resetInsertState() {
        _insertState.value = ClothesOperationState.Idle
    }
}

// 옷 작업 상태
sealed class ClothesOperationState {
    object Idle : ClothesOperationState()
    object Loading : ClothesOperationState()
    data class Success(val message: String) : ClothesOperationState()
    data class Failure(val message: String) : ClothesOperationState()
}