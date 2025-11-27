package com.fitfit.app.ui.screen.outfitsScreen

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsAddDialog
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsCard
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsEditDialog
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsFloatingButton
import com.fitfit.app.ui.screen.outfitsScreen.components.OutfitsTopBar
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    navController: NavController,
    outfitViewModel: OutfitViewModel,
    clothesViewModel: ClothesViewModel,
    weatherViewModel: WeatherViewModel
) {
    val outfits by outfitViewModel.outfitsWithClothes.collectAsState()
    val createState by outfitViewModel.createState.collectAsState()
    val updateState by outfitViewModel.updateState.collectAsState()
    val deleteState by outfitViewModel.deleteState.collectAsState()
    val clothesList by clothesViewModel.clothesList.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingOutfit by remember { mutableStateOf<OutfitWithClothes?>(null) }

    // 코디 목록 항상 새로고침
    LaunchedEffect(Unit) {
        clothesViewModel.loadClothes()              // 옷 리스트 불러오기
        outfitViewModel.loadOutfitsWithClothes()    // 코디 리스트 불러오기
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
            OutfitsTopBar()

            // 2. 스크롤 가능한 콘텐츠
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 아웃핏 카드 리스트
                items(outfits, key = { it.outfit.oid }) { outfitWithClothes ->
                    OutfitsCard(
                        outfitWithClothes = outfitWithClothes,
                        onEdit = { editingOutfit = outfitWithClothes },
                        onDelete = { outfitViewModel.deleteOutfit(outfitWithClothes.outfit.oid) }
                    )
                }

                // Add 버튼 공간 확보
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // 3. 고정된 Add 버튼
        OutfitsFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { showAddDialog = true
            }
        )

        // 코디 추가 다이얼로그
        if (showAddDialog) {
            OutfitsAddDialog(
                allClothes = clothesList,
                onDismiss = { showAddDialog = false },
                onSave = { clothesIds, occasion, comment, wornStartTime, wornEndTime ->
                    outfitViewModel.createOutfit(
                        clothesIds = clothesIds,
                        occasion = occasion,
                        comment = comment,
                        wornStartTime = wornStartTime,
                        wornEndTime = wornEndTime
                    )
                    showAddDialog = false
                }
            )
        }

        editingOutfit?.let { editing ->
            OutfitsEditDialog(
                outfitWithClothes = editing,
                onDismiss = { editingOutfit = null },
                onSave = { updatedClothesIds ->
                    outfitViewModel.updateOutfit(
                        oid = editing.outfit.oid,
                        clothesIds = updatedClothesIds
                    )
                    editingOutfit = null
                }
            )
        }
    }
}