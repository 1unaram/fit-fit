package com.fitfit.app.ui.screen.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.ui.screen.homeScreen.components.FilterButton
import com.fitfit.app.ui.screen.homeScreen.components.FilterSheet
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.HomeViewModel
import com.fitfit.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isFilterSheetShown) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onFilterOptionsToggled() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            FilterSheet(
                initialFilters = uiState.appliedFilters,
                onDismissRequest = { viewModel.onFilterOptionsToggled() },
                onApplyClicked = { finalFilters ->
                    viewModel.applyFilters(finalFilters)
                }
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FilterButton(
                onClick = { viewModel.onFilterOptionsToggled() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    WeatherCard(uiState = uiState)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "오늘의 의상", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutfitList(outfits = uiState.filteredOutfits)
                }
            }
        }
    }
}


@Composable
private fun WeatherCard(uiState: HomeScreenState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "현재 날씨 (임시)",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "현재 온도: ${uiState.currentTemp}°C " +
                        "(최저: ${uiState.minTemp}°C / 최고: ${uiState.maxTemp}°C)",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "날씨 설명: ${uiState.weatherScript}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun OutfitList(outfits: List<FilteredOutfit>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(outfits) { filteredOutfit ->
            OutfitListItem(item = filteredOutfit)
        }
    }
}


@Composable
private fun OutfitListItem(item: FilteredOutfit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO: item.outfit.imageUrl 로 이미지 로딩 (AsyncImage 등)
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                // (이미지 로딩 영역)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "날짜: ${item.date}", // date 포맷팅 필요
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}