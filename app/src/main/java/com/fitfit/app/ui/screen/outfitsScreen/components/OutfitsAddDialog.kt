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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items

@Composable
fun OutfitsAddDialog(
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

    // 1. 선택된 옷(최대 6개)
    var selectedClothes by remember { mutableStateOf<List<ClothesEntity>>(emptyList()) }

    // 2. 날짜/시간
    var wornDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var wornStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var wornEndTime by remember { mutableStateOf(System.currentTimeMillis() + 60 * 60 * 1000) }

    // DatePicker/TimePicker 상태
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }

    // 3. Occasion 태그(최대 3개)
    val allOccasions = listOf("Wedding", "Workday", "Workout", "Travel", "Normal", "Date", "School")
    var selectedOccasions by remember { mutableStateOf<List<String>>(emptyList()) }

    // 4. Comment
    var comment by remember { mutableStateOf("") }

    // 5. 옷 선택 다이얼로그 상태
    var showClothesSelectDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )) {
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
                // 1. 상단 6개 옷 선택 슬롯
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
                                            contentDescription = "옷 추가",
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
                                            contentDescription = "옷 추가",
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

                // 2. Date 선택
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
                            // 왼쪽: 날짜 텍스트 또는 플레이스홀더
                            val dateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date(wornDate))

                            Text(
                                text = dateText,
                                fontSize = 17.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // 오른쪽: 드롭다운 화살표 아이콘
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
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = wornDate

                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val now = Calendar.getInstance()
                            val currentHour = now.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = now.get(Calendar.MINUTE)
                            calendar.set(y, m, d)
                            calendar.set(Calendar.HOUR_OF_DAY, currentHour)
                            calendar.set(Calendar.MINUTE, currentMinute)
                            calendar.set(Calendar.SECOND, 0)

                            wornDate = calendar.timeInMillis

                            wornStartTime = calendar.timeInMillis
                            wornEndTime = wornStartTime + 2 * 60 * 60 * 1000

                            showDatePicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }


                // 3. 시간 구간(Time Range)
                AddFieldSection(label = "Time Range") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // START TIME
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

                        // END TIME
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

                // START TIME PICKER
                if (showTimePickerStart) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = wornStartTime
                    }
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
                                wornEndTime = wornStartTime + 60 * 60 * 1000 // 최소 1시간
                            }
                            showTimePickerStart = false
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }

                // END TIME PICKER
                if (showTimePickerEnd) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = wornEndTime
                    }
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
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }

                // 4. Occasion 선택 (최대 3개)
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

                // 5. Comment (Optional)
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

                // 6. 하단 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    // Back
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

                    // Save
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
                                && wornDate > 0
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

// --------------- Clothes 선택 다이얼로그 ---------------
@Composable
fun ClothesMultiSelectDialog(
    allClothes: List<ClothesEntity>,
    selectedClothes: List<ClothesEntity>,
    onSelected: (List<ClothesEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Box {
                // 상단 X 버튼
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(24.dp)
                        .clickable { onDismiss() },
                    tint = Color(0xFF8E8E93)
                )

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text("Select Clothes", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(15.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        // [변경 3] forEach -> items() 함수 사용
                        items(allClothes) { clothes ->
                            val selected = selectedClothes.contains(clothes)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (selected) Color(0xFFE8F2FF) else Color.White,
                                        shape = RoundedCornerShape(9.dp)
                                    )
                                    .border(
                                        1.dp,
                                        color = if (selected) Color(0xFF3673E4) else Color(
                                            0xFFD1D1D6
                                        ),
                                        shape = RoundedCornerShape(9.dp)
                                    )
                                    .clickable {
                                        onSelected(
                                            if (selected)
                                                selectedClothes.toMutableList()
                                                    .apply { remove(clothes) }
                                            else
                                                selectedClothes.toMutableList()
                                                    .apply { add(clothes) }
                                        )
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .background(
                                            if (selected) Color(0xFF3673E4) else Color.Transparent,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            color = if (selected) Color(0xFF3673E4) else Color(
                                                0xFFD1D1D6
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                                Spacer(Modifier.width(10.dp))
                                AsyncImage(
                                    model = clothes.imagePath,
                                    contentDescription = clothes.nickname,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(9.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(clothes.nickname, fontSize = 15.sp, color = Color.Black)
                            }
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