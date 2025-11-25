package com.fitfit.app.ui.screen.mypageScreen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()

//    LaunchedEffect(Unit) {
//        userViewModel.loadCurrentUser()
//    }

    Text(text = "My Page: ${currentUser?.username ?: "Guest"}")

    Button(onClick = {userViewModel.logout()}) { Text("Logout") }
}