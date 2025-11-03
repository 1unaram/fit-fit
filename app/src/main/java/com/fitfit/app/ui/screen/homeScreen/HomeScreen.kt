package com.fitfit.app.ui.screen.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    clothesViewModel: ClothesViewModel = viewModel()
) { Scaffold { innerPadding ->
        Row(
            modifier = Modifier.fillMaxHeight().padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text="Home Screen!"
                )

                // Test
                Button(
                    onClick = {
                        userViewModel.addUser("U1", "Test User")
                    }
                ) {
                    Text("Add User")
                }

                Button(
                    onClick = {
                        clothesViewModel.insertClothes("T1", "Test Clothes", "Top")
                    }
                ) {
                    Text("Add Clothes")
                }


            }
        }

    }
}