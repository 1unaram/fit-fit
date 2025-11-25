package com.fitfit.app.ui.screen.outfitsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.fitfit.app.data.local.entity.OutfitWithClothes

@Composable
fun OutfitsEditDialog(
    outfitWithClothes: OutfitWithClothes,
    onDismiss: () -> Unit,
    onSave: (updatedClothesIds: List<String>) -> Unit
) {
    // 기존 코디의 의류 ID 리스트를 State로 편집 가능하도록 복사
    var selectedClothesIds by remember { mutableStateOf(outfitWithClothes.clothes.map { it.cid }) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 4.dp) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("코디 수정", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "닫기") }
                }

                // 의류 편집 UI: 최대 6개까지 썸네일/플러스 버튼
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
                                    // 옷 변경/삭제 바텀시트 등 구현 가능
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (clothesId != null) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "옷 삭제",
                                    modifier = Modifier.size(28.dp)
                                    // 삭제 액션(예시)
                                    //.clickable { selectedClothesIds = selectedClothesIds.toMutableList().apply { removeAt(idx) } }
                                )
                                // 실제로는 썸네일 or 삭제버튼 등으로 구현
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

                // 기타 입력란(날짜/시간, 상황 등) 필요하면 추가

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("취소") }
                    Button(
                        onClick = { onSave(selectedClothesIds) }, // 수정된 ID 리스트 반환
                        modifier = Modifier.weight(1f)
                    ) { Text("저장") }
                }
            }
        }
    }
}