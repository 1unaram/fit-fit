package com.fitfit.app.ui.screen.loginScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.RegisterState
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // 회원가입 상태 관찰
    val registerState by userViewModel.registerState.collectAsState()

    // 회원가입 성공 시 로그인 화면으로 이동
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
            userViewModel.resetRegisterState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F2FF))
    ) {

        BackButton(
            onClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 33.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(112.dp))

            // Sign Up Main Card
            RegisterMainCard(
                userViewModel = userViewModel,
                navController = navController,
                registerState = registerState
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 48.dp)
            .size(40.dp)
            .background(
                color = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back",
            tint = Color(0xFF111111),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun RegisterMainCard(
    userViewModel: UserViewModel,
    navController: NavController,
    registerState: RegisterState
) {
    // State Hoisting: 상태를 여기서 관리
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .width(294.dp)
            .height(380.dp), // 에러 메시지 공간
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // Page Title
            RegisterTitle()

            // Sign Up Input Fields
            RegisterInputFields(
                username = username,
                password = password,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it }
            )

            // 에러 메시지 표시
            if (registerState is RegisterState.Failure) {
                Text(
                    text = registerState.message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Sign Up Submit Button
            RegisterSubmitButton(
                username = username,
                password = password,
                userViewModel = userViewModel,
                isLoading = registerState is RegisterState.Loading
            )
        }
    }
}

@Composable
private fun RegisterTitle() {
    Text(
        text = "Sign Up",
        fontSize = 23.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp)
    )
}

@Composable
private fun RegisterInputFields(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(247.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // User Name Input
        RegisterInputField(
            label = "User Name",
            placeholder = "",
            value = username,
            onValueChange = onUsernameChange
        )

        // Password Input
        RegisterInputField(
            label = "Password",
            placeholder = "",
            value = password,
            onValueChange = onPasswordChange,
            isPassword = true
        )
    }
}

// RegisterScreen 전용 InputField - State Hoisting 적용
@Composable
private fun RegisterInputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93),
            modifier = Modifier.width(71.dp),
            textAlign = TextAlign.Center
        )

        // Input Field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(163.dp)
                .height(30.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x99E8F2FF),
                            Color(0xCCE8F2FF)
                        )
                    ),
                    shape = RoundedCornerShape(7.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0x26000000),
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                imeAction = if (isPassword) ImeAction.Done else ImeAction.Next
            )
        )
    }
}

@Composable
private fun RegisterSubmitButton(
    username: String,
    password: String,
    userViewModel: UserViewModel,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(40.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x99E8F2FF),
                        Color(0xCCE8F2FF)
                    )
                ),
                shape = RoundedCornerShape(13.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0x26000000),
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(enabled = !isLoading) {
                // Firebase 회원가입 처리
                userViewModel.registerUser(username, password)
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF3673E4),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Sign Up",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3673E4)
            )
        }
    }
}
