package com.fitfit.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitfit.app.data.local.database.AppDatabase
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.repository.UserRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class UserViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val repository = UserRepository(userDao, application)

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFahrenheit = MutableStateFlow(false)
    val isFahrenheit: StateFlow<Boolean> = _isFahrenheit.asStateFlow()

    // ### 온도 단위 토글 ###
    fun toggleTemperatureUnit() {
        _isFahrenheit.value = !_isFahrenheit.value

        // Toast 메시지 표시
        val unit = if (_isFahrenheit.value) "Fahrenheit" else "Celsius"
        showToast("Temperature unit set to $unit.")
    }


    init {
        loadCurrentUser()
    }

    // ### 현재 사용자 불러오기 ###
    fun loadCurrentUser() = viewModelScope.launch {
        _isLoading.value = true

        val user = repository.getCurrentUser()
        _currentUser.value = user
        // 사용자가 있으면 로그인 상태로 설정
        if (user != null) {
            _loginState.value = LoginState.Success(user)
        }

        _isLoading.value = false
    }

    // ### 회원 가입 ###
    fun registerUser(username: String, password: String) = viewModelScope.launch {
        _registerState.value = RegisterState.Loading

        // 입력 검증
        if (username.isBlank()) {
            _registerState.value = RegisterState.Failure("Fill in your username.")
            return@launch
        }

        if (password.isBlank()) {
            _registerState.value = RegisterState.Failure("Fill in your password.")
            return@launch
        }

        if (password.length < 4) {
            _registerState.value =
                RegisterState.Failure("Password must be at least 4 characters long.")
            return@launch
        }


        // 회원가입 시도
        val result = repository.registerUser(username, password)

        result.onSuccess { uid ->
            _registerState.value = RegisterState.Success(uid)
        }.onFailure { exception ->
            _registerState.value = RegisterState.Failure(exception.message ?: "Registration failed")
        }
    }

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // ### 로그인 ###
    fun loginUser(username: String, password: String) = viewModelScope.launch {
        _loginState.value = LoginState.Loading

        try {
            withTimeout(10000L) {
                // 입력 검증
                if (username.isBlank()) {
                    _loginState.value = LoginState.Failure("Fill in your username.")
                    return@withTimeout
                }

                if (password.isBlank()) {
                    _loginState.value = LoginState.Failure("Fill in your password.")
                    return@withTimeout
                }

                // 로그인 시도
                val result = repository.loginUser(username, password)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _loginState.value = LoginState.Success(user)
                }.onFailure { exception ->
                    _loginState.value = LoginState.Failure(exception.message ?: "Login failed")
                }
            }
        } catch (e: TimeoutCancellationException) {
            _loginState.value = LoginState.Failure("Connection timeout. Please try again.")
        } catch (e: Exception) {
            _loginState.value = LoginState.Failure("Network error. Please check your connection.")
        }
    }

    // ### 로그아웃 ###
    fun logout() = viewModelScope.launch {
        repository.logout()
        _currentUser.value = null
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }

    // 상태 초기화
    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    // 실시간 동기화 시작
    fun startRealtimeSync() {
        repository.startRealtimeSync()
    }
}

// 회원가입 상태
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val uid: String) : RegisterState()
    data class Failure(val message: String) : RegisterState()
}

// 로그인 상태
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserEntity) : LoginState()
    data class Failure(val message: String) : LoginState()
}