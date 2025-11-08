package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.OutfitEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.data.repository.OutfitRepository
import kotlinx.coroutines.launch

class OutfitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OutfitRepository
    val allOutfitsWithClothes: LiveData<List<OutfitWithClothes>>

    init {
        val outfitDao = AppDatabase.getDatabase(application).outfitDao()
        repository = OutfitRepository(outfitDao, application)
        allOutfitsWithClothes = repository.getAllOutfitsWithClothes().asLiveData()
    }

    fun createOutfit(name: String, clothesIds: List<String>) = viewModelScope.launch {
        repository.createOutfitWithClothes(name, clothesIds)
    }

    fun addClothesToOutfit(oid: String, cid: String) = viewModelScope.launch {
        repository.addClothesToOutfit(oid, cid)
    }

    fun removeClothesFromOutfit(oid: String, cid: String) = viewModelScope.launch {
        repository.removeClothesFromOutfit(oid, cid)
    }

    fun deleteOutfit(outfit: OutfitEntity) = viewModelScope.launch {
        repository.deleteOutfit(outfit)
    }

    fun deleteAllOutfits() = viewModelScope.launch {
        repository.deleteAllOutfits()
    }
}