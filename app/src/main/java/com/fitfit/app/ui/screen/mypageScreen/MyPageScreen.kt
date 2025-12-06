package com.fitfit.app.ui.screen.mypageScreen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitfit.app.R
import com.fitfit.app.data.local.entity.UserEntity
import com.fitfit.app.data.util.formatTimestampToDate
import com.fitfit.app.viewmodel.LoginState
import com.fitfit.app.viewmodel.UserViewModel


@Composable
fun MyPageScreen(
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsStateWithLifecycle()
    val loginState by userViewModel.loginState.collectAsStateWithLifecycle()
    val isLoading by userViewModel.isLoading.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F2FF))
    ) {
        MyPageTopBar()

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                // 로그인된 상태
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp) // 47dp에서 30dp로 조정
                ){

                    Spacer(Modifier.height(120.dp))

                    ProfileIcon()

                    Spacer(Modifier.height(12.dp))
                    currentUser?.let { user ->
                        UserDataCard(user = user)
                    }
                    if (loginState is LoginState.Failure) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = (loginState as LoginState.Failure).message,
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    LogoutButton(onLogoutClick = { userViewModel.logout() })

                }
            }
        }
    }
}


@Composable
fun ProfileIcon() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .size(80.dp)
            .clip(CircleShape),
        //    .border(3.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "Profile",
            modifier = Modifier.size(150.dp),
            tint = Color.Black
        )
    }
}


@Composable
private fun UserDataCard(user: UserEntity) {
    val createdAtText = formatTimestampToDate(user.createdAt)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 33.dp),
    shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. 사용자 이름 (두 줄 정렬 스타일)
            InfoColumn(
                label = "User Name",
                value = user.username
            )

            // 2. Created At
            InfoColumn(
                label = "Created At",
                value = createdAtText
            )
        }
    }
}

// 속성 ; 두 줄 정렬
@Composable
fun InfoColumn(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF8E8E93)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
@Composable
private fun LogoutButton(onLogoutClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier
            .width(200.dp)
            .height(40.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
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
            )
            .clickable(onClick = onLogoutClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Logout",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3673E4)
            )
        }
    }
}

