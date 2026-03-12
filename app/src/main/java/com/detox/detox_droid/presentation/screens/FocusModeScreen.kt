package com.detox.detox_droid.presentation.screens

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.detox.detox_droid.presentation.viewmodels.FocusModeViewModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OrangeAccent
import com.detox.detox_droid.ui.theme.OverlineTextStyle
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PrimaryNeonDim
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SuccessGreen
import com.detox.detox_droid.ui.theme.SurfaceDark
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FocusModeScreen(
    viewModel: FocusModeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var selectedHours   by remember { mutableIntStateOf(0) }
    var selectedMinutes by remember { mutableIntStateOf(25) }

    val totalMinutes = selectedHours * 60 + selectedMinutes

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasDndPermission by remember {
        mutableStateOf(
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasDndPermission = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).isNotificationPolicyAccessGranted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest)
    ) {
        // ── Ambient background orbs ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .blur(140.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryNeonDim,
                            Color.Transparent
                        )
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
                        colors = listOf(
                            PurpleAccent.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ───────────────────────────────────────────────────────
            FocusModeHeader()

            // ── SETUP VIEW ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = !state.isDetoxActive,
                enter   = fadeIn() + scaleIn(initialScale = 0.95f),
                exit    = fadeOut() + scaleOut(targetScale = 0.95f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Duration picker card
                    DurationPickerCard(
                        selectedHours   = selectedHours,
                        selectedMinutes = selectedMinutes,
                        totalMinutes    = totalMinutes,
                        onHoursInc      = { if (selectedHours < 6) selectedHours++ },
                        onHoursDec      = { if (selectedHours > 0) selectedHours-- },
                        onMinsInc       = { selectedMinutes = (selectedMinutes + 5).let { if (it > 55) 0 else it } },
                        onMinsDec       = { selectedMinutes = (selectedMinutes - 5).let { if (it < 0) 55 else it } },
                        onPresetSelected = { preset ->
                            selectedHours   = preset / 60
                            selectedMinutes = preset % 60
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Start or Permission CTA
                    if (hasDndPermission) {
                        StartFocusButton(
                            totalMinutes = totalMinutes,
                            isLoading    = state.isLoading,
                            onClick      = { viewModel.startDetox(totalMinutes.toLong()) }
                        )
                    } else {
                        DndPermissionRequiredCard(
                            onClick = {
                                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // ── ACTIVE SESSION VIEW ──────────────────────────────────────────
            AnimatedVisibility(
                visible = state.isDetoxActive,
                enter   = fadeIn() + scaleIn(initialScale = 0.92f),
                exit    = fadeOut() + scaleOut(targetScale = 0.92f)
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated timer ring
                    ActiveTimerRing(
                        isTimedSession  = state.isTimedSession,
                        timeRemainingMs = state.timeRemainingMs,
                        totalDurationMs = state.totalDurationMs
                    )

                    // Pause / resume section
                    if (state.isPaused) {
                        PausedCard(
                            pauseRemainingMs = state.pauseRemainingMs,
                            onResume         = { viewModel.resumeSession() }
                        )
                    } else if (state.pausesRemainingToday > 0) {
                        EmergencyPauseSection(
                            pausesRemaining = state.pausesRemainingToday,
                            onPause         = { viewModel.pauseSession(it) }
                        )
                    } else {
                        NoPausesLeftBanner()
                    }

                    // End session button
                    EndSessionButton(onClick = { viewModel.stopDetox() })

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FocusModeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryNeon.copy(alpha = 0.09f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text       = "Focus Mode",
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Block distractions and enter deep work",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(listOf(PrimaryNeon, SecondaryNeon)),
                        RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Composable
private fun DurationPickerCard(
    selectedHours:    Int,
    selectedMinutes:  Int,
    totalMinutes:     Int,
    onHoursInc:       () -> Unit,
    onHoursDec:       () -> Unit,
    onMinsInc:        () -> Unit,
    onMinsDec:        () -> Unit,
    onPresetSelected: (Int) -> Unit
) {
    val durationLabel = when {
        totalMinutes == 0 -> "Indefinite session"
        selectedHours > 0 && selectedMinutes > 0 -> "${selectedHours}h ${selectedMinutes}m"
        selectedHours > 0 -> "${selectedHours}h"
        else -> "${selectedMinutes}m"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceVariant, SurfaceDark)
                )
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier            = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card header
            Row(
                modifier            = Modifier.fillMaxWidth(),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "SET DURATION",
                        style = OverlineTextStyle,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryNeon.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = PrimaryNeon,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Time pickers row ─────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                TimeUnitPicker(
                    value       = selectedHours,
                    label       = "hr",
                    onIncrement = onHoursInc,
                    onDecrement = onHoursDec
                )

                // Colon separator with neon glow
                Text(
                    text        = ":",
                    color       = PrimaryNeon,
                    style       = MaterialTheme.typography.displayLarge,
                    fontWeight  = FontWeight.Bold,
                    modifier    = Modifier.padding(bottom = 24.dp)
                )

                TimeUnitPicker(
                    value       = selectedMinutes,
                    label       = "min",
                    displayText = selectedMinutes.toString().padStart(2, '0'),
                    onIncrement = onMinsInc,
                    onDecrement = onMinsDec
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Duration label pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(PrimaryNeon.copy(alpha = 0.10f))
                    .border(
                        1.dp,
                        PrimaryNeon.copy(alpha = 0.35f),
                        RoundedCornerShape(percent = 50)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text       = durationLabel,
                    color      = PrimaryNeon,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Divider ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GlassBorder)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Preset chips ─────────────────────────────────────────────────
            Text(
                text  = "QUICK PRESETS",
                style = OverlineTextStyle,
                color = TextGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(15, 30, 60, 120).forEach { preset ->
                    PresetChip(
                        label   = if (preset < 60) "${preset}m" else "${preset / 60}h",
                        onClick = { onPresetSelected(preset) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StartFocusButton(
    totalMinutes: Int,
    isLoading:    Boolean,
    onClick:      () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (!isLoading)
                    Brush.horizontalGradient(listOf(PrimaryNeon, SecondaryNeon.copy(alpha = 0.85f)))
                else
                    Brush.horizontalGradient(listOf(TextGray, TextGray))
            )
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector    = Icons.Rounded.PlayArrow,
                contentDescription = "Start",
                modifier       = Modifier.size(26.dp),
                tint           = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text       = if (totalMinutes == 0) "START INDEFINITE" else "START FOCUS",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 17.sp,
                color      = Color.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun ActiveTimerRing(
    isTimedSession:  Boolean,
    timeRemainingMs: Long,
    totalDurationMs: Long
) {
    // Animate ring sweep progress
    val progress = if (isTimedSession && totalDurationMs > 0)
        (timeRemainingMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    else 1f

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animProgress.animateTo(progress, tween(600, easing = FastOutSlowInEasing))
    }

    // Pulsing outer glow
    val infiniteTransition = rememberInfiniteTransition(label = "timerGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.15f,
        targetValue   = 0.40f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val h = TimeUnit.MILLISECONDS.toHours(timeRemainingMs)
    val m = TimeUnit.MILLISECONDS.toMinutes(timeRemainingMs) % 60
    val s = TimeUnit.MILLISECONDS.toSeconds(timeRemainingMs) % 60
    val timeStr = if (isTimedSession) {
        if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    } else "∞"

    Box(
        modifier          = Modifier.size(280.dp),
        contentAlignment  = Alignment.Center
    ) {
        // Outermost ambient pulse glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .blur(32.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryNeon.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Progress arc canvas
        Canvas(modifier = Modifier.size(260.dp)) {
            val strokeWidth = 14.dp.toPx()
            val inset       = strokeWidth / 2
            val arcTopLeft  = Offset(inset, inset)
            val arcSize     = Size(size.width - strokeWidth, size.height - strokeWidth)

            // Background track ring
            drawArc(
                color      = Color.White.copy(alpha = 0.05f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = arcTopLeft,
                size       = arcSize,
                style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Secondary decorative ring
            drawArc(
                color      = PrimaryNeon.copy(alpha = 0.08f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = Offset(inset + 18.dp.toPx(), inset + 18.dp.toPx()),
                size       = Size(
                    size.width - strokeWidth - 36.dp.toPx(),
                    size.height - strokeWidth - 36.dp.toPx()
                ),
                style      = Stroke(width = 1.dp.toPx())
            )

            // Progress arc — neon gradient sweep
            if (animProgress.value > 0f) {
                drawArc(
                    brush      = Brush.sweepGradient(
                        colors = listOf(
                            SecondaryNeon,
                            PrimaryNeon,
                            PrimaryNeon.copy(alpha = 0.4f)
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animProgress.value,
                    useCenter  = false,
                    topLeft    = arcTopLeft,
                    size       = arcSize,
                    style      = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Rotating sparkle dot at arc tip
            if (isTimedSession && animProgress.value > 0.02f) {
                val angle     = Math.toRadians((-90f + 360f * animProgress.value).toDouble()).toFloat()
                val radius    = (size.width - strokeWidth) / 2f
                val dotCenter = Offset(
                    x = center.x + radius * cos(angle),
                    y = center.y + radius * sin(angle)
                )
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = dotCenter)
                drawCircle(
                    color  = PrimaryNeon.copy(alpha = 0.6f),
                    radius = 10.dp.toPx(),
                    center = dotCenter
                )
            }
        }

        // Inner circle content
        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SurfaceVariant, SurfaceDark)
                    )
                )
                .border(1.dp, GlassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text          = "FOCUSING",
                    color         = PrimaryNeon,
                    style         = MaterialTheme.typography.labelLarge,
                    fontWeight    = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = timeStr,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "timerText"
                ) { text ->
                    Text(
                        text       = text,
                        color      = TextLight,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = if (text.length > 5) 32.sp else 38.sp,
                        textAlign  = TextAlign.Center
                    )
                }

                if (isTimedSession) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "remaining",
                        color = TextGray,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 0.5.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Until you stop it",
                        color = TextGray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PausedCard(pauseRemainingMs: Long, onResume: () -> Unit) {
    val pauseMins = TimeUnit.MILLISECONDS.toMinutes(pauseRemainingMs)
    val pauseSecs = TimeUnit.MILLISECONDS.toSeconds(pauseRemainingMs) % 60

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SuccessGreen.copy(alpha = 0.08f))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(SuccessGreen.copy(alpha = 0.5f), SecondaryNeon.copy(alpha = 0.3f))
                ),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier            = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "⏸  Session Paused",
                color      = SuccessGreen,
                fontWeight = FontWeight.Bold,
                style      = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text  = "Resuming in %d:%02d".format(pauseMins, pauseSecs),
                color = SuccessGreen.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(SuccessGreen, SecondaryNeon.copy(alpha = 0.7f))
                        )
                    )
                    .clickable { onResume() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "Resume Now",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color.Black
                )
            }
        }
    }
}

@Composable
private fun EmergencyPauseSection(pausesRemaining: Int, onPause: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.padding(bottom = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Bolt,
                contentDescription = null,
                tint     = OrangeAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text  = "Emergency Pause  ·  $pausesRemaining left today",
                color = TextMuted,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.fillMaxWidth()
        ) {
            listOf(5, 15, 30).forEach { mins ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryNeon.copy(alpha = 0.08f))
                        .border(
                            1.dp,
                            PrimaryNeon.copy(alpha = 0.35f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onPause(mins) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "${mins}m",
                        color      = PrimaryNeon,
                        fontWeight = FontWeight.SemiBold,
                        style      = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun NoPausesLeftBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ErrorRed.copy(alpha = 0.07f))
            .border(1.dp, ErrorRed.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text      = "No emergency pauses remaining today.",
            color     = ErrorRed.copy(alpha = 0.85f),
            style     = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EndSessionButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(ErrorRed.copy(alpha = 0.10f))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(ErrorRed.copy(alpha = 0.4f), OrangeAccent.copy(alpha = 0.2f))
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint     = ErrorRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text       = "End Session Early",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = ErrorRed
            )
        }
    }
}

@Composable
private fun TimeUnitPicker(
    value:       Int,
    label:       String,
    displayText: String = value.toString(),
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Up button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PrimaryNeon.copy(alpha = 0.10f))
                .border(1.dp, PrimaryNeon.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Up",
                tint = PrimaryNeon,
                modifier = Modifier.size(24.dp)
            )
        }

        // Value display
        Text(
            text       = displayText,
            color      = TextLight,
            style      = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            modifier   = Modifier
                .width(80.dp)
                .padding(vertical = 12.dp)
        )

        // Down button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PrimaryNeon.copy(alpha = 0.10f))
                .border(1.dp, PrimaryNeon.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .clickable(onClick = onDecrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                tint = PrimaryNeon,
                modifier = Modifier.size(24.dp)
            )
        }

        // Unit label
        Text(
            text     = label,
            color    = TextMuted,
            style    = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun PresetChip(
    label:    String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceVariant, SurfaceDark)
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(GlassBorder, Color.Transparent)
                ),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = TextLight,
            fontWeight = FontWeight.SemiBold,
            style      = MaterialTheme.typography.bodyMedium,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
private fun DndPermissionRequiredCard(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark.copy(alpha = 0.6f))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsOff,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DND Access Required",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tap to grant Detox Droid permission to silence your phone during Focus sessions.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}