package com.fitfit.app.ui.screen.clothesScreen

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import com.fitfit.app.viewmodel.ClothesOperationState
import com.fitfit.app.viewmodel.ClothesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreenExample (
    navController: NavController,
    viewModel: ClothesViewModel = viewModel()
) {
    val context = LocalContext.current
    val insertState by viewModel.insertState.collectAsState()
    val clothesList by viewModel.clothesList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClothes()
    }

    // 이미지 URI 상태
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 입력 필드 상태
    var category by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var storeUrl by remember { mutableStateOf("") }

    // 갤러리에서 이미지 선택 런처
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // URI 영구 접근 권한 부여
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
        }
    }

    // 저장 성공 시 뒤로가기 / 토스트 등 처리
    LaunchedEffect(insertState) {
        when (insertState) {
            is ClothesOperationState.Success -> {
                Toast.makeText(
                    context,
                    (insertState as ClothesOperationState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetInsertState()
                navController.navigateUp()
            }
            is ClothesOperationState.Failure -> {
                Toast.makeText(
                    context,
                    (insertState as ClothesOperationState.Failure).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetInsertState()
            }
            else -> { /* Idle, Loading 은 화면에서 표시만 */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("옷 추가") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1) 이미지 선택 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable {
                        pickImageLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("사진을 선택해주세요")
                    }
                }
            }

            // 2) 카테고리 입력 (단순 TextField 또는 Dropdown)
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("카테고리 (예: Top, Bottom)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 3) 닉네임 입력
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임 (필수)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 4) Store URL (선택)
            OutlinedTextField(
                value = storeUrl,
                onValueChange = { storeUrl = it },
                label = { Text("Store URL (선택)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5) 저장 버튼
            Button(
                onClick = {
                    val imagePath = imageUri?.toString() ?: ""
                    val url = storeUrl.ifBlank { null }

                    viewModel.insertClothes(
                        imagePath = imagePath,
                        category = category.trim(),
                        nickname = nickname.trim(),
                        storeUrl = url
                    )

                    Log.d("ClothesScreen", "Insert Clothes: imagePath=$imagePath, category=$category, nickname=$nickname, storeUrl=$url")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = insertState !is ClothesOperationState.Loading
            ) {
                if (insertState is ClothesOperationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("저장하기")
                }
            }

            // 저장된 이미지 목록
            if (clothesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("등록된 옷이 없습니다.\n+ 버튼을 눌러 옷을 추가해보세요.", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clothesList, key = { it.cid }) { clothes ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 이미지 영역
                                if (clothes.imagePath.isNotBlank()) {
                                    Log.d("ClothesScreen", "Loaded image: ${clothes.imagePath}")

                                    val painter = rememberAsyncImagePainter(
                                        model = clothes.imagePath,
                                        imageLoader = LocalContext.current.imageLoader,
                                        onError = { error ->
                                            Log.e("ClothesScreen", "Image load failed: ${error.result.throwable}")
                                        }
                                    )

                                    Image(
                                        painter = painter,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Log.d("ClothesScreen", "No image available for clothes id: ${clothes.cid}")
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ImageNotSupported,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // 텍스트 정보
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = clothes.nickname,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "카테고리: ${clothes.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    clothes.storeUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                        Text(
                                            text = url,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "삭제",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
