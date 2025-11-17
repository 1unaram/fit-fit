package com.fitfit.app.ui.screen.outfitsScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.OutfitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    navController: NavController,
    viewModel: OutfitViewModel = viewModel()
) {
    val outfitsWithClothes by viewModel.outfitsWithClothes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOutfitsWithClothes()
    }

}