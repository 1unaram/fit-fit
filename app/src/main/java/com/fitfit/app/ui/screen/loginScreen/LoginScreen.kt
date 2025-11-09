package com.fitfit.app.ui.screen.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.LoginState
import com.fitfit.app.viewmodel.RegisterState
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
) {
    val registerState by userViewModel.registerState.collectAsState()
    val loginState by userViewModel.loginState.collectAsState()

    // 회원가입 성공 시 자동 로그인
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            userViewModel.loginUser("testuser", "1234")
        }
    }

    // 로그인 성공 시 홈으로 이동
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FitFit 테스트",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 테스트용 회원가입 버튼
        Button(
            onClick = {
                userViewModel.registerUser("testuser", "1234")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is RegisterState.Loading && loginState !is LoginState.Loading
        ) {
            when (registerState) {
                is RegisterState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                else -> Text("테스트 회원가입 (testuser/1234)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 테스트용 로그인 버튼
        Button(
            onClick = {
                userViewModel.loginUser("testuser", "1234")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is RegisterState.Loading && loginState !is LoginState.Loading
        ) {
            when (loginState) {
                is LoginState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                else -> Text("테스트 로그인 (testuser/1234)")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 상태 메시지 표시
        when (registerState) {
            is RegisterState.Success -> {
                Text(
                    "회원가입 성공! 자동 로그인 중...",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is RegisterState.Failure -> {
                Text(
                    (registerState as RegisterState.Failure).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }

        when (loginState) {
            is LoginState.Success -> {
                Text(
                    "로그인 성공!",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is LoginState.Failure -> {
                Text(
                    (loginState as LoginState.Failure).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}