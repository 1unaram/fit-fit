package com.fitfit.app.ui.screen.clothesScreen.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fitfit.app.R
import java.io.File

@Composable
fun ClothesAddDialog(
    onDismiss: () -> Unit,
    onSave: (imageUri: Uri?, category: String, nickname: String, storeUrl: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var storeUrl by remember { mutableStateOf("") }

    val categories = listOf("Outerwear", "Tops", "Bottoms")

    // 이미지 선택 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val filePath = copyImageToAppStorage(context, it)
            selectedImageUri = filePath?.toUri()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 34.dp, horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                // 이미지 선택 영역
                AddImageSection(
                    selectedImageUri = selectedImageUri,
                    onImageClick = { imagePickerLauncher.launch("image/*") }
                )

                // 카테고리 선택
                AddFieldSection(label = "Category") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        categories.forEach { category ->
                            AddCategoryChip(
                                text = category,
                                selected = category == selectedCategory,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }

                // 닉네임 입력
                AddFieldSection(label = "Clothes Nickname") {
                    AddTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        placeholder = ""
                    )
                }

                // Store URL 입력
                AddFieldSection(label = "Store URL") {
                    AddTextField(
                        value = storeUrl,
                        onValueChange = { storeUrl = it },
                        placeholder = ""
                    )
                }

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    // Back 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .width(67.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(13.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0x99E8F2FF),
                                            Color(0xCCE8F2FF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(13.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0x26000000),
                                    shape = RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }

                    // Save 버튼
                    Button(
                        onClick = {
                            onSave(
                                selectedImageUri,
                                selectedCategory,
                                nickname,
                                storeUrl.ifBlank { null }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(13.dp),
                        contentPadding = PaddingValues(0.dp),
                        enabled = selectedImageUri != null && selectedCategory.isNotBlank() && nickname.isNotBlank()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0x99E8F2FF),
                                            Color(0xCCE8F2FF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(13.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0x26000000),
                                    shape = RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3673E4)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddImageSection(
    selectedImageUri: Uri?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onImageClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color.White,
                        Color(0xFFE6E6E6)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 0.33.dp,
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add image",
                tint = Color(0xFF3673E4),
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
private fun AddFieldSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3673E4)
        )
        content()
    }
}

@Composable
private fun AddCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xB3FFFFFF),
                        Color(0xB3E6E6E6)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 0.33.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF3673E4),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.Black else Color(0xFF8E8E93)
        )
    }
}

@Composable
private fun AddTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = Color.Black
        ),
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 15.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }
                        innerTextField()
                    }
                    // 편집 아이콘 (Optional)
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(13.dp)
                    )
                }
                // 밑줄
                Divider(
                    color = Color(0xFF8E8E93),
                    thickness = 1.dp
                )
            }
        },
        singleLine = true
    )
}

// 이미지 저장 권한 문제 해결을 위한 파일 복사 함수
fun copyImageToAppStorage(context: Context, uri: Uri): String? {
    return try {
        val fileName = "clothes_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    } catch (e: Exception) {
        Log.e("ClothesAddDialog", "Failed to copy image", e)
        null
    }
}
