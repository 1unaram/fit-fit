package com.fitfit.app.ui.screen.outfitsScreen

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitfit.app.data.local.entity.OutfitWithClothes
import com.fitfit.app.viewmodel.ClothesViewModel
import com.fitfit.app.viewmodel.OutfitOperationState
import com.fitfit.app.viewmodel.OutfitViewModel
import com.fitfit.app.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreenExample(
    navController: NavController,
    outfitViewModel: OutfitViewModel,
    clothesViewModel: ClothesViewModel = viewModel(),
    weatherViewModel: WeatherViewModel
) {
    val outfitsWithClothes by outfitViewModel.outfitsWithClothes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        outfitViewModel.loadOutfitsWithClothes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 코디") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "코디 추가")
            }
        }
    ) { paddingValues ->
        if (outfitsWithClothes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "등록된 코디가 없습니다.\n+ 버튼을 눌러 코디를 추가해보세요.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(outfitsWithClothes, key = { it.outfit.oid }) { outfitWithClothes ->
                    OutfitCardItem(
                        outfitWithClothes = outfitWithClothes,
                        onClick = {
                            // TODO: 상세 화면 이동 등
                            // navController.navigate("outfit_detail/${outfitWithClothes.outfit.oid}")
                        },
                        onDelete = {
                            outfitViewModel.deleteOutfit(outfitWithClothes.outfit.oid)
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            OutfitAddDialog(
                onDismiss = { showAddDialog = false },
                outfitViewModel = outfitViewModel,
                weatherViewModel = weatherViewModel
            )
        }

    }

}

@Composable
fun OutfitCardItem(
    outfitWithClothes: OutfitWithClothes,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val outfit = outfitWithClothes.outfit
    val clothes = outfitWithClothes.clothes

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "옷 ${clothes.size}개 포함",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "착용 시간: ${formatTime(outfit.wornStartTime)} ~ ${formatTime(outfit.wornEndTime)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 날씨 정보가 있는 경우 요약 표시
            if (outfit.weatherFetched && outfit.description != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "날씨: ${outfit.description}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "온도: " +
                                "${outfit.temperatureMin?.let { String.format("%.1f", it) }}°C ~ " +
                                "${outfit.temperatureMax?.let { String.format("%.1f", it) }}°C" +
                                " (평균 ${outfit.temperatureAvg?.let { String.format("%.1f", it) }}°C)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    outfit.windSpeed?.let {
                        Text(
                            text = "평균 풍속: ${String.format("%.1f", it)} m/s",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    outfit.precipitation?.let {
                        Text(
                            text = "평균 강수량: ${String.format("%.1f", it)} mm",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else if (outfit.wornEndTime > System.currentTimeMillis()) {
                Text(
                    "착용이 끝난 후 날씨 정보가 자동으로 채워집니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(
                    "날씨 정보 계산 중...",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }
}

fun formatTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

@Composable
fun OutfitAddDialog(
    onDismiss: () -> Unit,
    outfitViewModel: OutfitViewModel,
    clothesViewModel: ClothesViewModel = viewModel(),
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current

    val clothesList by clothesViewModel.clothesList.collectAsState()
    val createState by outfitViewModel.createState.collectAsState()
    val currentLocation by weatherViewModel.currentLocation.collectAsState()

    var selectedClothesIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var occasionInput by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    // 시작/종료 시간 상태
    var wornStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var wornEndTime by remember { mutableStateOf(System.currentTimeMillis() + 60 * 60 * 1000) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            weatherViewModel.getCurrentLocation()
        } else {
            Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        clothesViewModel.loadClothes()
        if (weatherViewModel.hasLocationPermission()) {
            weatherViewModel.getCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(createState) {
        when (createState) {
            is OutfitOperationState.Success -> {
                Toast.makeText(
                    context,
                    (createState as OutfitOperationState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                outfitViewModel.resetCreateState()
                onDismiss()
            }
            is OutfitOperationState.Failure -> {
                Toast.makeText(
                    context,
                    (createState as OutfitOperationState.Failure).message,
                    Toast.LENGTH_SHORT
                ).show()
                outfitViewModel.resetCreateState()
            }
            else -> {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("코디 추가", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                Divider()

                OutlinedTextField(
                    value = occasionInput,
                    onValueChange = { occasionInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("상황 태그 (쉼표로 구분, 예: 데이트, 출근)") }
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    label = { Text("코멘트 (선택)") },
                    maxLines = 4
                )

                // 위치 정보
                Text(
                    text = currentLocation?.let {
                        "위치: ${it.latitude}, ${it.longitude}"
                    } ?: "위치 정보를 가져오는 중...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                // 시작 시간
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("착용 시작", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = formatDateTime(wornStartTime),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedButton(onClick = {
                        showDateTimePicker(
                            context = context,
                            initialMillis = wornStartTime
                        ) { selected ->
                            wornStartTime = selected
                            // 시작시간 변경 시, 종료시간이 그보다 빠르면 같이 밀어주기
                            if (wornEndTime <= wornStartTime) {
                                wornEndTime = wornStartTime + 60 * 60 * 1000
                            }
                        }
                    }) {
                        Text("시작 시간 변경")
                    }
                }

                // 종료 시간
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("착용 종료", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = formatDateTime(wornEndTime),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedButton(onClick = {
                        showDateTimePicker(
                            context = context,
                            initialMillis = wornEndTime
                        ) { selected ->
                            wornEndTime = selected
                        }
                    }) {
                        Text("종료 시간 변경")
                    }
                }

                Divider()

                Text("옷 선택", style = MaterialTheme.typography.titleMedium)

                if (clothesList.isEmpty()) {
                    Text(
                        "등록된 옷이 없습니다.\n먼저 옷을 추가해주세요.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(clothesList, key = { it.cid }) { clothes ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedClothesIds.contains(clothes.cid),
                                    onCheckedChange = { checked ->
                                        selectedClothesIds =
                                            if (checked) selectedClothesIds + clothes.cid
                                            else selectedClothesIds - clothes.cid
                                    }
                                )
                                Text(
                                    text = "${clothes.nickname} (${clothes.category})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 하단 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("취소")
                    }
                    Button(
                        onClick = {
                            val loc = currentLocation
                            if (loc == null) {
                                Toast.makeText(context, "위치 정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val occasionList = occasionInput
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }

                            outfitViewModel.createOutfit(
                                clothesIds = selectedClothesIds.toList(),
                                occasion = occasionList,
                                comment = comment.ifBlank { null },
                                wornStartTime = wornStartTime,
                                wornEndTime = wornEndTime,
                                latitude = loc.latitude, //삭제 -> 일단 빈값으로 넘겨주기
                                longitude = loc.longitude //삭제 -> 일단 빈값으로 넘겨주기
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = createState !is OutfitOperationState.Loading
                                && selectedClothesIds.isNotEmpty()
                    ) {
                        if (createState is OutfitOperationState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("저장")
                        }
                    }
                }
            }
        }
    }
}

fun showDateTimePicker(
    context: Context,
    initialMillis: Long,
    onDateTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = initialMillis
    }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    DatePickerDialog(
        context,
        { _, y, m, d ->
            // 날짜 선택되면 다시 TimePicker 띄우기
            TimePickerDialog(
                context,
                { _, h, min ->
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, y)
                        set(Calendar.MONTH, m)
                        set(Calendar.DAY_OF_MONTH, d)
                        set(Calendar.HOUR_OF_DAY, h)
                        set(Calendar.MINUTE, min)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateTimeSelected(cal.timeInMillis)
                },
                hour,
                minute,
                true
            ).show()
        },
        year,
        month,
        day
    ).show()
}

fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

