package com.fitfit.app.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitfit.app.R

@Composable
fun BottomNavBar(
    selectedIndex: Int = 0,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavBarTab("clothes", R.drawable.ic_clothes),
        NavBarTab("outfits", R.drawable.ic_outfits),
        NavBarTab("home", R.drawable.ic_home),
        NavBarTab("weather", R.drawable.ic_weather),
        NavBarTab("my page", R.drawable.ic_mypage),
    )

    // 배경 + 그림자
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 1.0f), Color.White.copy(alpha = 1.0f))
                ),
                shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
            )
            .padding(bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, tab ->
                NavBarIcon(
                    label = tab.label,
                    iconRes = tab.iconRes,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

// 데이터 클래스
data class NavBarTab(val label: String, val iconRes: Int)

@Composable
fun NavBarIcon(label: String, iconRes: Int, selected: Boolean, onClick: () -> Unit) {
    val textColor = if (selected) Color(0xFF171D1B) else Color(0xFF8E8E93)
    val iconColor = if (selected) Color(0xFF171D1B) else Color(0xFF8E8E93)

    Column(
        modifier = Modifier
            .width(72.dp)
            .height(53.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
            letterSpacing = 0.2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavBar() {
    BottomNavBar(selectedIndex = 2, onTabSelected = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewVectorIcon() {
    Image(
        painter = painterResource(R.drawable.ic_clothes),
        contentDescription = "Clothes icon",
        modifier = Modifier.size(48.dp)
    )
}
