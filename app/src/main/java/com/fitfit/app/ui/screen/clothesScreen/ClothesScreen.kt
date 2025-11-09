package com.fitfit.app.ui.screen.clothesScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.viewmodel.ClothesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    navController: NavController,
    clothesViewModel: ClothesViewModel = viewModel()
) {
    val clothesList by clothesViewModel.clothesList.collectAsState()

    LaunchedEffect(Unit) {
        clothesViewModel.loadClothes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 옷") },
                navigationIcon = {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Text("뒤로")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (clothesList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("등록된 옷이 없습니다.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = clothesList,
                        key = {it.cid}
                    ) { clothes ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = clothes.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "카테고리: ${clothes.category}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "동기화: ${if (clothes.isSynced) "완료" else "대기중"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (clothes.isSynced)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.error
                                    )
                                }
                                IconButton(onClick = {
                                    clothesViewModel.deleteClothes(clothes.cid)
                                }) {
                                    Icon(Icons.Default.Delete, "삭제")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}