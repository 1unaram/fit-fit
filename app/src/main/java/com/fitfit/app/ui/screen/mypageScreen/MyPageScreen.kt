package com.fitfit.app.ui.screen.mypageScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    /*

        currentUser.username

     */
}