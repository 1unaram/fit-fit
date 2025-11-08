package com.fitfit.app.viewmodel

import android.R.attr.name
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
        repository = ClothesRepository(clothesDao, application)
        allClothes = repository.getAllClothes().asLiveData()
    }

    fun insertClothes(name: String, category: String) = viewModelScope.launch {
        repository.insertClothes(name, category)
    }

//    fun updateClothes(clothes: ClothesEntity) = viewModelScope.launch {
//        repository.updateClothes(clothes)
//    }
//
//    fun deleteClothes(clothes: ClothesEntity) = viewModelScope.launch {
//        repository.deleteClothes(clothes)
//    }
}