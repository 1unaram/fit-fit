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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F2FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 33.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(112.dp))

            // Login Main Card
            LoginMainCard(userViewModel)

            Spacer(modifier = Modifier.height(38.dp))

            // Sign Up Button
            RegisterButton(navController = navController)

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LoginMainCard(userViewModel: UserViewModel) {
    Card(
        modifier = Modifier
            .width(294.dp)
            .height(377.dp),
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
                .padding(vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(47.dp)
        ) {
            // Login Title
            LoginTitle()

            // Login Input Fields
            LoginInputFields()

            // Login Button
            LoginButton(userViewModel)
        }
    }
}

@Composable
fun LoginTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // "Get Your Fit On!" Text
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("Get Your ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF3673E4))) {
                    append("Fit")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(" On!")
                }
            },
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        // "Fit-Fit" Text
        Text(
            text = "Fit-Fit",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3673E4),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginInputFields() {
    Column(
        modifier = Modifier
            .width(247.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // User Name Input
        InputField(
            label = "User Name",
            placeholder = ""
        )

        // Password Input
        InputField(
            label = "Password",
            placeholder = "",
            isPassword = true
        )
    }
}

@Composable
fun InputField(
    label: String,
    placeholder: String,
    isPassword: Boolean = false
) {
    var text by remember { mutableStateOf("") }

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
            value = text,
            onValueChange = { text = it },
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
fun LoginButton(userViewModel: UserViewModel) {
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
            .clickable {
                // userViewModel.loginUser()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Login",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3673E4)
        )
    }
}

@Composable
fun RegisterButton(navController: NavController) {
    Card(
        modifier = Modifier
            .width(294.dp)
            .height(56.dp)
            .clickable { navController.navigate("register") },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign Up",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3673E4),
                textAlign = TextAlign.Center
            )
        }
    }
}