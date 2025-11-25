package com.fitfit.app.ui.screen.mypageScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()

    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(text = "My Page: ${currentUser?.username ?: "Guest"}")
        Button(onClick = {userViewModel.logout()}) { Text("Logout") }
    }


}