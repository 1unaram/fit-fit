package com.fitfit.app.ui.screen.loginScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
) {
    Scaffold { innerPadding ->
        Row(
            modifier = Modifier.fillMaxHeight().padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Logged in not yet!"
            )
        }
    }
}