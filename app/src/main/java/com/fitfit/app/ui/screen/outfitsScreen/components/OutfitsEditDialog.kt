package com.fitfit.app.ui.screen.outfitsScreen.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.fitfit.app.data.local.entity.ClothesEntity
import com.fitfit.app.data.local.entity.OutfitWithClothes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun OutfitsEditDialog(
    outfitWithClothes: OutfitWithClothes,
    allClothes: List<ClothesEntity>,
    onDismiss: () -> Unit,
    onSave: (
        clothesIds: List<String>,
        occasion: List<String>,
        comment: String?,
        wornStartTime: Long,
        wornEndTime: Long
    ) -> Unit
) {
    val context = LocalContext.current

    // 1. 선택된 옷(최대 6개) - 기존 카드의 옷들로 초기화
    var selectedClothes by remember {
        mutableStateOf(
            allClothes.filter { c -> outfitWithClothes.clothes.any { it.cid == c.cid } }
                .take(6)
        )
    }

    // 2. 날짜/시간 - 기존 코디의 시간으로 초기화
    var wornStartTime by remember { mutableStateOf(outfitWithClothes.outfit.wornStartTime) }
    var wornEndTime by remember { mutableStateOf(outfitWithClothes.outfit.wornEndTime) }
    var wornDate by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = outfitWithClothes.outfit.wornStartTime
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }

    // DatePicker/TimePicker 상태
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }

    // 3. Occasion 태그(최대 3개) - 기존 값으로 초기화
    val allOccasions = listOf("Wedding", "Workday", "Workout", "Travel", "Normal", "Date", "School")
    var selectedOccasions by remember {
        mutableStateOf(outfitWithClothes.outfit.occasion.take(3))
    }

    // 4. Comment - 기존 코멘트로 초기화
    var comment by remember { mutableStateOf(outfitWithClothes.outfit.comment.orEmpty()) }

    // 5. 옷 선택 다이얼로그 상태
    var showClothesSelectDialog by remember { mutableStateOf(false) }

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
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                // 1. 상단 6개 옷 선택 슬롯 (Add와 동일)
                val maxClothes = 6
                val columns = 3
                val cellSize = 96.dp

                val clothesCount = selectedClothes.size
                val showAddButton = clothesCount < maxClothes
                val totalCells = clothesCount + if (showAddButton) 1 else 0

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 첫 번째 행 (0~2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (index in 0 until columns) {
                            val globalIndex = index
                            if (globalIndex < totalCells) {
                                if (globalIndex < clothesCount) {
                                    val clothes = selectedClothes[globalIndex]
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .clickable {
                                                selectedClothes =
                                                    selectedClothes.toMutableList().apply { removeAt(globalIndex) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = clothes.imagePath,
                                            contentDescription = clothes.nickname,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                } else if (showAddButton) {
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .border(
                                                width = 1.5.dp,
                                                color = Color(0xFF3673E4),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { showClothesSelectDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add clothes",
                                            tint = Color(0xFF3673E4),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 두 번째 행 (3~5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (index in 0 until columns) {
                            val globalIndex = columns + index
                            if (globalIndex < totalCells) {
                                if (globalIndex < clothesCount) {
                                    val clothes = selectedClothes[globalIndex]
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .clickable {
                                                selectedClothes =
                                                    selectedClothes.toMutableList().apply { removeAt(globalIndex) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = clothes.imagePath,
                                            contentDescription = clothes.nickname,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                } else if (showAddButton) {
                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .border(
                                                width = 1.5.dp,
                                                color = Color(0xFF3673E4),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { showClothesSelectDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add clothes",
                                            tint = Color(0xFF3673E4),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 옷 선택 팝업
                if (showClothesSelectDialog) {
                    ClothesMultiSelectDialog(
                        allClothes = allClothes,
                        selectedClothes = selectedClothes,
                        onSelected = { newSelection ->
                            selectedClothes = newSelection.take(6)
                            showClothesSelectDialog = false
                        },
                        onDismiss = { showClothesSelectDialog = false }
                    )
                }

                // 2. Date 선택 (기존 스타일 그대로, 값만 초기화됨)
                AddFieldSection(label = "Date") {
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
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date(wornDate))

                            Text(
                                text = dateText,
                                fontSize = 17.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select a date",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (showDatePicker) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = wornDate
                    }
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            calendar.set(y, m, d)
                            wornDate = calendar.timeInMillis

                            // 날짜는 바꾸되, 시간은 기존 wornStartTime/wornEndTime의 시각을 유지
                            val startCal = Calendar.getInstance().apply {
                                timeInMillis = wornStartTime
                                set(Calendar.YEAR, y)
                                set(Calendar.MONTH, m)
                                set(Calendar.DAY_OF_MONTH, d)
                            }
                            val endCal = Calendar.getInstance().apply {
                                timeInMillis = wornEndTime
                                set(Calendar.YEAR, y)
                                set(Calendar.MONTH, m)
                                set(Calendar.DAY_OF_MONTH, d)
                            }
                            wornStartTime = startCal.timeInMillis
                            wornEndTime = endCal.timeInMillis

                            showDatePicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                // 3. 시간 구간(Time Range) – AddDialog와 동일 로직
                AddFieldSection(label = "Time Range") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // START
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                                .clickable { showTimePickerStart = true }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val startText = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(Date(wornStartTime))

                                Text(
                                    text = startText.ifBlank { "Start time" },
                                    fontSize = 15.sp,
                                    color = if (startText.isBlank()) Color(0xFF8E8E93) else Color.Black
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select start time",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // END
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                                .clickable { showTimePickerEnd = true }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val endText = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(Date(wornEndTime))

                                Text(
                                    text = endText.ifBlank { "End time" },
                                    fontSize = 15.sp,
                                    color = if (endText.isBlank()) Color(0xFF8E8E93) else Color.Black
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select end time",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                if (showTimePickerStart) {
                    val cal = Calendar.getInstance().apply { timeInMillis = wornStartTime }
                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            val tempCal = Calendar.getInstance().apply {
                                timeInMillis = wornStartTime
                                set(Calendar.HOUR_OF_DAY, h)
                                set(Calendar.MINUTE, m)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            wornStartTime = tempCal.timeInMillis
                            if (wornEndTime <= wornStartTime) {
                                wornEndTime = wornStartTime + 60 * 60 * 1000
                            }
                            showTimePickerStart = false
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }

                if (showTimePickerEnd) {
                    val cal = Calendar.getInstance().apply { timeInMillis = wornEndTime }
                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            val tempCal = Calendar.getInstance().apply {
                                timeInMillis = wornEndTime
                                set(Calendar.HOUR_OF_DAY, h)
                                set(Calendar.MINUTE, m)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            wornEndTime = tempCal.timeInMillis
                            if (wornEndTime <= wornStartTime) {
                                wornEndTime = wornStartTime + 60 * 60 * 1000
                            }
                            showTimePickerEnd = false
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                }

                // 4. Occasion (기존 선택값 + 재선택 가능)
                AddFieldSection(label = "Occasion (up to 3)") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allOccasions.forEach { occasion ->
                            val selected = selectedOccasions.contains(occasion)
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
                                        brush = if (selected) getOccasionBrush(occasion)
                                        else Brush.linearGradient(listOf(Color.White, Color(0xFFE6E6E6))),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = selectedOccasions.size < 3 || selected) {
                                        selectedOccasions = if (selected)
                                            selectedOccasions - occasion
                                        else
                                            selectedOccasions + occasion
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = occasion,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // 5. Comment
                AddFieldSection(label = "Comment (Optional)", labelColor = Color(0xFF8E8E93)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
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
                        androidx.compose.foundation.text.BasicTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (comment.isBlank()) {
                                    Text(
                                        text = "Enter comment",
                                        fontSize = 15.sp,
                                        color = Color(0xFF8E8E93)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // 6. 하단 버튼 (Back / Save)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
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

                    Button(
                        onClick = {
                            onSave(
                                selectedClothes.map { it.cid },
                                selectedOccasions,
                                comment.ifBlank { null },
                                wornStartTime,
                                wornEndTime
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(13.dp),
                        contentPadding = PaddingValues(0.dp),
                        enabled = selectedClothes.isNotEmpty()
                                && selectedOccasions.isNotEmpty()
                                && wornStartTime > 0
                                && wornEndTime > wornStartTime
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

// Occasion 라벨 색상 함수
private fun getOccasionBrush(occasion: String): Brush {
    return when (occasion) {
        "Workday" -> Brush.linearGradient(listOf(Color(0xB3FCE8ED), Color(0xB3F8B3C2)))
        "School" -> Brush.linearGradient(listOf(Color(0xB3FAEED9), Color(0xB3F6CC84)))
        "Date" -> Brush.linearGradient(listOf(Color(0xB3FFD7DC), Color(0xB3F9B2B6)))
        "Normal" -> Brush.linearGradient(listOf(Color(0xB3F3F6FC), Color(0xB3E1E6F8)))
        "Travel" -> Brush.linearGradient(listOf(Color(0xB3D7FFEB), Color(0xB3BEEAD9)))
        "Wedding" -> Brush.linearGradient(listOf(Color(0xB3FFE6FA), Color(0xB3E8B3F8)))
        "Workout" -> Brush.linearGradient(listOf(Color(0xB3EAF9FC), Color(0xB3B3F8F6)))
        else -> Brush.linearGradient(listOf(Color(0xB3FFFFFF), Color(0xB3E6E6E6)))
    }
}

@Composable
private fun AddFieldSection(
    label: String,
    labelColor: Color = Color(0xFF3673E4),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor
        )
        content()
    }
}