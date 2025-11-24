package com.fitfit.app.ui.screen.clothesScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.ui.screen.clothesScreen.components.CategoryChips
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesAddDialog
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesCard
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesDetailDialog
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesEditDialog
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesFloatingButton
import com.fitfit.app.ui.screen.clothesScreen.components.ClothesTopBar
import com.fitfit.app.viewmodel.ClothesViewModel

@Composable
fun ClothesScreen(
    navController: NavController,
    clothesViewModel: ClothesViewModel = viewModel()
) {
    val clothesList by clothesViewModel.clothesList.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedClothes by remember { mutableStateOf<ClothesEntity?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // 화면 진입 시 옷 목록 로드
    LaunchedEffect(Unit) {
        clothesViewModel.loadClothes()
    }

    LaunchedEffect(clothesList.size) {
        Log.d("ClothesScreen", "Clothes count changed: ${clothesList.size}")
        clothesList.forEachIndexed { index, clothes ->
            Log.d("ClothesScreen", "[$index] CID: ${clothes.cid}, Path: ${clothes.imagePath}")
        }
    }

    // 카테고리 필터링
    val filteredClothes = if (selectedCategory == "All") {
        clothesList
    } else {
        clothesList.filter { it.category == selectedCategory }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F2FF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 고정된 상단 타이틀
            ClothesTopBar()

            // 2. 스크롤 가능한 콘텐츠
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 카테고리 버튼들
                item {
                    CategoryChips(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
                // 옷 카드 리스트
                items(filteredClothes) { clothes ->
                    ClothesCard(
                        clothes = clothes,
                        onEdit = {
                            selectedClothes = clothes
                            showEditDialog = true
                        },
                        onDelete = {
                            clothesViewModel.deleteClothes(clothes.cid)
                        },
                        onClick = {
                            selectedClothes = clothes
                            showDetailDialog = true
                        }
                    )
                }

                // Add 버튼 공간 확보
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // 3. 고정된 Add 버튼
        ClothesFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                showAddDialog = true
            }
        )
    }

    // 다이얼로그들
    if (showDetailDialog && selectedClothes != null) {
        ClothesDetailDialog(
            clothes = selectedClothes!!,
            onDismiss = {
                showDetailDialog = false
                selectedClothes = null
            }
        )
    }

    if (showEditDialog && selectedClothes != null) {
        ClothesEditDialog(
            clothes = selectedClothes!!,
            onDismiss = {
                showEditDialog = false
                selectedClothes = null
            },
            onSave = { category, nickname, storeUrl ->
                clothesViewModel.updateClothes(
                    cid = selectedClothes!!.cid,
                    imagePath = selectedClothes!!.imagePath,
                    category = category,
                    nickname = nickname,
                    storeUrl = storeUrl
                )
                showEditDialog = false
                selectedClothes = null
                clothesViewModel.loadClothes()
            }
        )
    }

    if (showAddDialog) {
        ClothesAddDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { imageUri, category, nickname, storeUrl ->
                imageUri?.let {
                    clothesViewModel.insertClothes(
                        imagePath = it.toString(),
                        category = category,
                        nickname = nickname,
                        storeUrl = storeUrl
                    )

                    clothesViewModel.loadClothes()
                }
                showAddDialog = false
            }
        )
    }
}