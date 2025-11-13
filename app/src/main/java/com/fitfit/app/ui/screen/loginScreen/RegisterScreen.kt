package com.fitfit.app.ui.screen.loginScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    navController : NavController,
    userViewModel : UserViewModel = viewModel()
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

            // Sign Up Main Card
            RegisterMainCard()

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RegisterMainCard() {
    Card(
        modifier = Modifier
            .width(294.dp)
            .height(280.dp),
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
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Page Title
            RegisterTitle()

            // Sign Up Input Fields
            RegisterInputFields()

            // Sign Up Submit Button
            RegisterSubmitButton()
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
private fun RegisterInputFields() {
    Column(
        modifier = Modifier
            .width(247.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // User Name Input - LoginScreen의 InputField() 재사용
        InputField(
            label = "User Name",
            placeholder = ""
        )

        // Password Input - LoginScreen의 InputField() 재사용
        InputField(
            label = "Password",
            placeholder = "",
            isPassword = true
        )
    }
}

@Composable
private fun RegisterSubmitButton() {
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
            .clickable { /* Handle sign up */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sign Up",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3673E4)
        )
    }
}