package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    val allUsers: LiveData<List<UserEntity>>

    // Registration state management
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao, application)
        allUsers = repository.getAllUsers().asLiveData()
    }

    fun registerUser(username: String, password: String) = viewModelScope.launch {
        if (repository.isUsernameTaken(username)) {
            _registerState.value = RegisterState.Failure("중복된 아이디입니다.")
        } else {
            repository.insertUser(username, password)
            _registerState.value = RegisterState.Success
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Success : RegisterState()
    data class Failure(val message: String) : RegisterState()
}