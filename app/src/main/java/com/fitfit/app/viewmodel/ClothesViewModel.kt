package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.repository.ClothesRepository
import kotlinx.coroutines.launch

class ClothesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClothesRepository
    val allClothes: LiveData<List<ClothesEntity>>

    init {
        val clothesDao = AppDatabase.getDatabase(application).clothesDao()
        repository = ClothesRepository(clothesDao)
        allClothes = repository.getAllClothes().asLiveData()
    }

    fun insertClothes(cid: String, name: String, category: String) = viewModelScope.launch {
        val clothes = ClothesEntity(
            cid = cid,
            name = name,
            category = category
        )
        repository.insertClothes(clothes)
    }

//    fun updateClothes(clothes: ClothesEntity) = viewModelScope.launch {
//        repository.updateClothes(clothes)
//    }
//
//    fun deleteClothes(clothes: ClothesEntity) = viewModelScope.launch {
//        repository.deleteClothes(clothes)
//    }
}