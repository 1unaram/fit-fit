package com.fitfit.app.ui.screen.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.fitfit.app.ui.screen.homeScreen.components.WeatherScreen
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    clothesViewModel: ClothesViewModel = viewModel(),
    outfitViewModel: OutfitViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val clothesList by clothesViewModel.clothesList.collectAsState()
    val outfitsList by outfitViewModel.outfitsList.collectAsState()

    // 데이터 로드
    LaunchedEffect(currentUser) {
        currentUser?.let {
            clothesViewModel.loadClothes()
            outfitViewModel.loadOutfits()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FitFit 홈") },
                actions = {
                    TextButton(onClick = {
                        userViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("로그아웃")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 사용자 정보
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "로그인 정보",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("UID: ${currentUser?.uid ?: "없음"}")
                    Text("사용자명: ${currentUser?.username ?: "없음"}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 옷 목록 카드
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "내 옷",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "${clothesList.size}개",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("clothes") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("옷 관리하기")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 코디 목록 카드
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "내 코디",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "${outfitsList.size}개",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("outfits") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("코디 관리하기")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 테스트용 데이터 추가 버튼들
            Text(
                "테스트 기능",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    clothesViewModel.insertClothes(
                        name = "테스트 티셔츠",
                        category = "상의"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("테스트 옷 추가")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (clothesList.isNotEmpty()) {
                        outfitViewModel.createOutfit(
                            name = "테스트 코디",
                            clothesIds = clothesList.take(2).map { it.cid }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = clothesList.isNotEmpty()
            ) {
                Text("테스트 코디 생성")
            }

            WeatherScreen()
        }
    }
}