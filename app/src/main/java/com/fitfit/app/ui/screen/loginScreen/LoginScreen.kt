package com.fitfit.app.ui.screen.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.RegisterState
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
) {
    val registerState by userViewModel.registerState.collectAsState()

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier.fillMaxHeight().padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Logged in not yet!"
            )

            Button(
                onClick = {
                    userViewModel.registerUser("test12345", "12345")

                    when (registerState) {
                        is RegisterState.Success -> {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        is RegisterState.Failure -> {
                            val message = (registerState as RegisterState.Failure).message
                            println("Registration failed: $message")
                        }
                        RegisterState.Idle -> {/* 대기상태 */}
                    }
                }
            ) {
                Text("Login")
            }
        }
    }
}