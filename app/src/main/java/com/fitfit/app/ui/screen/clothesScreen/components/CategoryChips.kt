package com.fitfit.app.ui.screen.clothesScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Outerwear", "Tops", "Bottoms")

    // 최외부 Box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier.wrapContentWidth(Alignment.Start)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(11.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x99E8F2FF),
                                Color(0xCCE8F2FF)
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { category ->
                        CategoryChip(
                            text = category,
                            selected = category == selectedCategory,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Box(
            modifier = Modifier
                .height(26.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(11.dp)
                )
                .clip(RoundedCornerShape(11.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x6BFFFFFF),
                            Color(0x8FFFFFFF)
                        )
                    )
                )
                .border(
                    width = 0.2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(11.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Box(
            modifier = Modifier
                .height(26.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8E8E93),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE8F2FF, name = "Bottoms Selected")
@Composable
fun PreviewCategoryChips_Bottoms() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        CategoryChips(
            selectedCategory = "Bottoms",
            onCategorySelected = {}
        )
    }
}