package com.fitfit.app.ui.screen.outfitsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun OutfitsAddDialog(
    onDismiss: () -> Unit,
    onSave: (
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long
    ) -> Unit
) {
    // 상태 예시 - 실제 앱에서는 ViewModel/Flow 등에서 의류리스트, 위치 등 받아옴
    var selectedClothesIds by remember { mutableStateOf(listOf<String>()) }
    var occasionInput by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var wornStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var wornEndTime by remember { mutableStateOf(System.currentTimeMillis() + 60*60*1000) }
    // 예시 위도/경도(실제로는 권한 및 위치서비스 ViewModel 등에서 받아야 함)
    val latitude = 37.5665
    val longitude = 126.9780

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("코디 추가", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                // 옷 6개까지 썸네일/플러스 버튼
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(6) { idx ->
                        val clothesId = selectedClothesIds.getOrNull(idx)
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    // 옷 선택 - 실제로는 바텀시트 등으로 의류 리스트 띄워서 선택
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (clothesId != null) {
                                // 썸네일(대체)
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "옷 삭제",
                                    modifier = Modifier.size(28.dp)
                                    // 삭제 로직 추가 가능
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "옷 추가",
                                    tint = Color(0xFF3673E4),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                // 상황 입력
                OutlinedTextField(
                    value = occasionInput,
                    onValueChange = { occasionInput = it },
                    label = { Text("상황 태그 (쉼표로 구분)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 코멘트 입력
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("코멘트 (선택)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 70.dp)
                )

                // 착용 시작/종료 시간 (DateTimePicker 등 실제 앱 UX 맞게 구현)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("착용 시작: $wornStartTime")
                        // TODO: Date/Time picker UI 연동
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("착용 종료: $wornEndTime")
                        // TODO: Date/Time picker UI 연동
                    }
                }

                // 필요하다면 현재 위치 표시/변경

                // 하단 버튼
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("취소")
                    }
                    Button(
                        onClick = {
                            onSave(
                                selectedClothesIds,
                                occasionInput.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                comment.ifBlank { null },
                                wornStartTime,
                                wornEndTime
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("저장") }
                }
            }
        }
    }
}
