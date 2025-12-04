package com.fitfit.app.ui.screen.clothesScreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.fitfit.app.R
import com.fitfit.app.data.local.entity.ClothesEntity

@Composable
fun ClothesCard(
    clothes: ClothesEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),  //원래12
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 옷 이미지
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (clothes.imagePath.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(clothes.imagePath),
                        contentDescription = clothes.nickname,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ImageNotSupported,
                            contentDescription = "No Image",
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 옷 정보 : 카테고리 14.sp, 닉네임 17.sp
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 카테고리 라벨
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color(0x26000000), // rgba(0, 0, 0, 0.15)
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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = clothes.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = clothes.nickname,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
            }

            // 편집/삭제 아이콘
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(21.dp)
                        .clickable { onEdit() },
                    tint = Color(0xFF8E8E93)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(21.dp)
                        .clickable { onDelete() },
                    tint = Color(0xFF8E8E93)
                )
            }
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Tops", "Top" -> Color(0xB3FDE8FF)      // rgba(253, 232, 255, 0.70) - 연보라/핑크
        "Bottoms", "Bottom" -> Color(0xB3EAFFE8) // rgba(234, 255, 232, 0.70) - 연초록
        "Outerwear" -> Color(0xB3FFE7BE)         // rgba(255, 231, 190, 0.70) - 연노랑/피치
        else -> Color(0xFF8E8E93)
    }
}
