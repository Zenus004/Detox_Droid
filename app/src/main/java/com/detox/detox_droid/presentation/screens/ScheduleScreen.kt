package com.detox.detox_droid.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity
import com.detox.detox_droid.presentation.viewmodels.ScheduleViewModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OverlineTextStyle
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PrimaryNeonDim
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceDark
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val (showAddSheet, setShowAddSheet) = remember { mutableStateOf(false) }
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope        = rememberCoroutineScope()

    Scaffold(
        containerColor = BackgroundDeepest,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(listOf(PrimaryNeon, SecondaryNeon))
                    )
                    .clickable { setShowAddSheet(true) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add Schedule",
                    tint     = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDeepest)
                .padding(innerPadding)
        ) {
            // ── Ambient orbs ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.TopEnd)
                    .blur(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PurpleAccent.copy(alpha = 0.12f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomStart)
                    .blur(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PrimaryNeonDim, Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // ── Header ───────────────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        PurpleAccent.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 28.dp)
                    ) {
                        Column {
                            Text(
                                text       = "Automated Detox",
                                color      = TextLight,
                                style      = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text  = "Schedules auto-start Focus Mode at set times",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(2.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(PurpleAccent, PrimaryNeon)
                                        ),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }

                // ── Loading ───────────────────────────────────────────────────
                if (state.isLoading) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color       = PrimaryNeon,
                                strokeWidth = 2.dp,
                                modifier    = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // ── Empty state ───────────────────────────────────────────────
                else if (state.schedules.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(PurpleAccent.copy(alpha = 0.10f))
                                        .border(1.dp, PurpleAccent.copy(alpha = 0.22f), RoundedCornerShape(22.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth,
                                        contentDescription = null,
                                        tint     = PurpleAccent,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    "No schedules yet",
                                    color      = TextLight,
                                    style      = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Tap + to automate Focus Mode",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // ── Schedule list ─────────────────────────────────────────────
                else {
                    item {
                        Row(
                            modifier              = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text  = "YOUR SCHEDULES",
                                style = OverlineTextStyle,
                                color = TextMuted
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(PurpleAccent.copy(alpha = 0.10f))
                                    .border(
                                        1.dp,
                                        PurpleAccent.copy(alpha = 0.25f),
                                        RoundedCornerShape(percent = 50)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text  = "${state.schedules.size} total",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PurpleAccent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(state.schedules, key = { it.id }) { schedule ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ScheduleItemCard(
                                schedule = schedule,
                                onToggle = { viewModel.toggleSchedule(schedule, it) },
                                onDelete = { viewModel.deleteSchedule(schedule) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // ── Add Schedule Bottom Sheet ─────────────────────────────────────────
        if (showAddSheet) {
            ModalBottomSheet(
                onDismissRequest = { setShowAddSheet(false) },
                sheetState       = sheetState,
                containerColor   = SurfaceDark,
                modifier         = Modifier.wrapContentHeight()
            ) {
                AddScheduleSheet(
                    onConfirm = { startMs, endMs, days ->
                        viewModel.addSchedule(startMs, endMs, days)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            setShowAddSheet(false)
                        }
                    },
                    onCancel = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            setShowAddSheet(false)
                        }
                    }
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SCHEDULE ITEM CARD  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ScheduleItemCard(
    schedule: DetoxScheduleEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val formatter  = SimpleDateFormat("h:mm a", Locale.getDefault())
    val startLabel = formatter.format(Date(schedule.startTimeInMillis))
    val endLabel   = formatter.format(Date(schedule.endTimeInMillis))

    val accentColor by animateColorAsState(
        targetValue   = if (schedule.isActive) PrimaryNeon else TextGray,
        animationSpec = tween(300),
        label         = "scheduleAccent"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (schedule.isActive)
                    Brush.horizontalGradient(
                        listOf(PrimaryNeon.copy(alpha = 0.07f), PurpleAccent.copy(alpha = 0.04f))
                    )
                else
                    Brush.horizontalGradient(listOf(SurfaceVariant, SurfaceDark))
            )
            .border(
                width = 1.dp,
                brush = if (schedule.isActive)
                    Brush.horizontalGradient(
                        listOf(PrimaryNeon.copy(alpha = 0.40f), PurpleAccent.copy(alpha = 0.25f))
                    )
                else
                    Brush.horizontalGradient(listOf(GlassBorder, GlassBorder)),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        // Left accent stripe
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(80.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor, accentColor.copy(alpha = 0.3f))
                    )
                )
        )

        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Time range with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint     = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text       = "$startLabel – $endLabel",
                        color      = TextLight,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Days as mini pills
                DaysPillRow(daysString = schedule.daysOfWeek, activeColor = accentColor)

                Spacer(modifier = Modifier.height(6.dp))

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.10f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text  = if (schedule.isActive) "Active" else "Disabled",
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked         = schedule.isActive,
                    onCheckedChange = onToggle,
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor   = Color.Black,
                        checkedTrackColor   = PrimaryNeon,
                        uncheckedThumbColor = TextGray,
                        uncheckedTrackColor = SurfaceVariant
                    )
                )
                // Delete button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorRed.copy(alpha = 0.08f))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint     = ErrorRed.copy(alpha = 0.75f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ── Days pill row ─────────────────────────────────────────────────────────────

@Composable
private fun DaysPillRow(daysString: String, activeColor: Color) {
    val allDays    = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val activeDays = daysString.split(",").map { it.trim() }.toSet()

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        allDays.forEach { day ->
            val isActive = day in activeDays
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        if (isActive) activeColor.copy(alpha = 0.14f)
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (isActive) activeColor.copy(alpha = 0.35f) else GlassBorder,
                        RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                Text(
                    text       = day.take(1),
                    style      = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color      = if (isActive) activeColor else TextGray,
                    textAlign  = TextAlign.Center
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ADD SCHEDULE BOTTOM SHEET  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddScheduleSheet(
    onConfirm: (Long, Long, String) -> Unit,
    onCancel:  () -> Unit
) {
    val days         = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDays by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri")) }

    var startHour   by remember { mutableIntStateOf(9) }
    var startMinute by remember { mutableIntStateOf(0) }
    var endHour     by remember { mutableIntStateOf(10) }
    var endMinute   by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
    ) {
        // Sheet drag handle styled
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GlassBorder)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Sheet header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(PurpleAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint     = PurpleAccent,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Add Detox Schedule",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = TextLight
                )
                Text(
                    "Focus Mode activates automatically in this window",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Time pickers ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark)))
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimePickerColumn(
                    label          = "Start",
                    hour           = startHour,
                    minute         = startMinute,
                    onHourChange   = { startHour = it },
                    onMinuteChange = { startMinute = it },
                    modifier       = Modifier.weight(1f)
                )

                // Arrow separator
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PurpleAccent.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text      = "→",
                        color     = PurpleAccent,
                        fontSize  = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                TimePickerColumn(
                    label          = "End",
                    hour           = endHour,
                    minute         = endMinute,
                    onHourChange   = { endHour = it },
                    onMinuteChange = { endMinute = it },
                    modifier       = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Day selector ──────────────────────────────────────────────────────
        Text(
            "ACTIVE DAYS",
            style    = OverlineTextStyle,
            color    = TextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val selected = day in selectedDays
                val bgColor by animateColorAsState(
                    targetValue   = if (selected) PrimaryNeon.copy(alpha = 0.15f) else SurfaceVariant,
                    animationSpec = tween(200),
                    label         = "dayBg"
                )
                val borderColor by animateColorAsState(
                    targetValue   = if (selected) PrimaryNeon.copy(alpha = 0.5f) else GlassBorder,
                    animationSpec = tween(200),
                    label         = "dayBorder"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                        .clickable {
                            selectedDays = if (selected) selectedDays - day else selectedDays + day
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = day.take(1),
                        color      = if (selected) PrimaryNeon else TextGray,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        style      = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Action buttons ────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Cancel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariant)
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Cancel",
                    color      = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    style      = MaterialTheme.typography.labelLarge
                )
            }

            // Save
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (selectedDays.isNotEmpty())
                            Brush.horizontalGradient(listOf(PrimaryNeon, SecondaryNeon.copy(alpha = 0.8f)))
                        else
                            Brush.horizontalGradient(listOf(TextGray, TextGray))
                    )
                    .clickable {
                        if (selectedDays.isEmpty()) return@clickable
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, startHour)
                        cal.set(Calendar.MINUTE, startMinute)
                        cal.set(Calendar.SECOND, 0)
                        val startMs = cal.timeInMillis
                        cal.set(Calendar.HOUR_OF_DAY, endHour)
                        cal.set(Calendar.MINUTE, endMinute)
                        val endMs   = cal.timeInMillis
                        val daysStr = days.filter { it in selectedDays }.joinToString(",")
                        onConfirm(startMs, endMs, daysStr)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint     = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Save Schedule",
                        color      = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TIME PICKER COLUMN  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TimePickerColumn(
    label:          String,
    hour:           Int,
    minute:         Int,
    onHourChange:   (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier:       Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style  = OverlineTextStyle,
            color  = TextMuted,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Hour picker
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NudgeButton(icon = Icons.Rounded.KeyboardArrowUp) {
                onHourChange((hour + 1) % 24)
            }
            Text(
                text       = "%02d".format(hour),
                style      = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color      = PrimaryNeon,
                fontFamily = FontFamily.Monospace,
                modifier   = Modifier.padding(vertical = 4.dp)
            )
            NudgeButton(icon = Icons.Rounded.KeyboardArrowDown) {
                onHourChange((hour - 1 + 24) % 24)
            }
        }

        // Colon
        Text(
            ":",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = PrimaryNeon.copy(alpha = 0.6f),
            modifier   = Modifier.padding(vertical = 2.dp)
        )

        // Minute picker
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NudgeButton(icon = Icons.Rounded.KeyboardArrowUp) {
                onMinuteChange((minute + 5) % 60)
            }
            Text(
                text       = "%02d".format(minute),
                style      = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color      = PrimaryNeon,
                fontFamily = FontFamily.Monospace,
                modifier   = Modifier.padding(vertical = 4.dp)
            )
            NudgeButton(icon = Icons.Rounded.KeyboardArrowDown) {
                onMinuteChange((minute - 5 + 60) % 60)
            }
        }
    }
}

// ── Nudge button (up/down arrow) ──────────────────────────────────────────────

@Composable
private fun NudgeButton(
    icon:    androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryNeon.copy(alpha = 0.08f))
            .border(1.dp, PrimaryNeon.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint     = PrimaryNeon,
            modifier = Modifier.size(20.dp)
        )
    }
}