package com.fitfit.app.ui.screen.clothesScreen.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.viewmodel.ClothesOperationState
import com.fitfit.app.viewmodel.ClothesViewModel

@Composable
fun ClothesEditDialog(
    clothesViewModel: ClothesViewModel,
    clothes: ClothesEntity,
    onDismiss: () -> Unit,
    onSave: (category: String, nickname: String, storeUrl: String?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(clothes.category) }
    var nickname by remember { mutableStateOf(clothes.nickname) }
    var storeUrl by remember { mutableStateOf(clothes.storeUrl ?: "") }

    val categories = listOf("Tops", "Bottoms", "Outerwear")

    val updateState by clothesViewModel.updateState.collectAsState()
    val context = LocalContext.current


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
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            // 닫기 버튼
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clickable { onDismiss() },
                tint = Color(0xFF8E8E93)
            )

            // 콘텐츠
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!clothes.imagePath.isNullOrBlank()) {
                        androidx.compose.foundation.Image(
                            painter = coil.compose.rememberAsyncImagePainter(clothes.imagePath),
                            contentDescription = clothes.nickname,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ImageNotSupported,
                            contentDescription = "No Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                // Category, Clothes Nickname: 읽기전용 (ClothesCard 부분과 통일)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Category (좌우배치)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Category",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8E8E93)
                        )
                        Box(
                            modifier = Modifier
                                .shadow(
                                    elevation = 3.dp,
                                    shape = RoundedCornerShape(8.dp),
                                    spotColor = Color(0x26000000),
                                    ambientColor = Color(0x26000000)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = getCategoryColor(clothes.category),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = clothes.category,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // Clothes Nickname (레이블 + 값, 위아래)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "Clothes Nickname",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8E8E93)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = clothes.nickname,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // Store URL (Optional, 편집가능)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Store URL (Optional)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E8E93)
                        )
                        EditTextField(
                            value = storeUrl,
                            onValueChange = { storeUrl = it },
                            placeholder = "Enter store URL"
                        )
                    }
                }

                // 저장 버튼
                Button(
                    onClick = {
                        onSave(
                            clothes.category,
                            clothes.nickname,
                            storeUrl.ifBlank { null }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(13.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = true // 항상 저장 가능
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

    // 옷 수정 시 Toast 알림
    LaunchedEffect(updateState) {
        when (updateState) {
            is ClothesOperationState.Success -> {
                Toast.makeText(
                    context,
                    (updateState as ClothesOperationState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                clothesViewModel.resetUpdateState()
                onDismiss()
            }
            is ClothesOperationState.Failure -> {
                Toast.makeText(
                    context,
                    (updateState as ClothesOperationState.Failure).message,
                    Toast.LENGTH_SHORT
                ).show()
                clothesViewModel.resetUpdateState()
            }
            else -> {}
        }
    }
}

@Composable
private fun EditFieldSection(
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
            color = Color(0xFF8E8E93)
        )
        content()
    }
}

@Composable
private fun CategoryEditChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFF3673E4) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF3673E4) else Color(0xFFD1D1D6),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF8E8E93)
        )
    }
}

@Composable
private fun EditTextField(
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0x99E8F2FF),
                                Color(0xCCE8F2FF)
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0x40000000),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 15.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
                innerTextField()
            }
        },
        singleLine = true
    )
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Tops", "Top" -> Color(0xB3FDE8FF)
        "Bottoms", "Bottom" -> Color(0xB3EAFFE8)
        "Outerwear" -> Color(0xB3FFE7BE)
        else -> Color(0xFF8E8E93)
    }
}