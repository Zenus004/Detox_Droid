package com.detox.detox_droid.presentation.screens

import android.content.Intent
import androidx.core.net.toUri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.detox.detox_droid.domain.models.AppUsage
import com.detox.detox_droid.presentation.viewmodels.DashboardViewModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.DividerSubtle
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OrangeAccent
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
import java.util.concurrent.TimeUnit

// ── Chart color palette
private val CHART_COLORS = listOf(
    Color(0xFF8BE9FD), // cyan
    Color(0xFFFF79C6), // pink
    Color(0xFF50FA7B), // green
    Color(0xFFFFB86C), // orange
    Color(0xFFBD93F9), // purple
    Color(0xFFFF5555), // red
)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionsAndFetchData()
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
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .blur(radius = 120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryNeonDim, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(radius = 100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ───────────────────────────────────────────────────────
            DashboardHeader()

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // ── Permission banners ────────────────────────────────────────
                if (!state.hasUsagePermission) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        UsagePermissionCard()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (!state.hasAccessibilityPermission) {
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                        AccessibilityPermissionCard()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (!state.hasOverlayPermission) {
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                        OverlayPermissionCard()
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Loading state ─────────────────────────────────────────────
                if (state.isLoading && state.topUsedApps.isEmpty() && state.hasUsagePermission) {
                    LoadingState()
                } else if (
                    state.hasUsagePermission &&
                    state.hasAccessibilityPermission &&
                    state.hasOverlayPermission
                ) {
                    // ── Detox status card ─────────────────────────────────────
                    DetoxStatusCard(isDetoxActive = state.isDetoxActive)
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Screen time donut ─────────────────────────────────────
                    ScreenTimeDonutCard(
                        totalScreenTimeMs = state.totalScreenTimeMs,
                        apps = state.topUsedApps
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Top apps breakdown ────────────────────────────────────
                    TopAppsBreakdownCard(
                        apps = state.topUsedApps,
                        totalMs = state.totalScreenTimeMs
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SecondaryNeon.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your digital wellbeing at a glance",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Neon divider
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryNeon, SecondaryNeon)
                        ),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

// ── Loading state ─────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = PrimaryNeon,
                strokeWidth = 2.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Loading usage data…",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DetoxStatusCard(isDetoxActive: Boolean) {
    val accentColor = if (isDetoxActive) PrimaryNeon else TextGray

    // Pulsing dot animation when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDetoxActive)
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryNeon.copy(alpha = 0.12f),
                            SecondaryNeon.copy(alpha = 0.06f)
                        )
                    )
                else
                    Brush.horizontalGradient(
                        colors = listOf(SurfaceVariant, SurfaceDark)
                    )
            )
            .border(
                width = 1.dp,
                brush = if (isDetoxActive)
                    Brush.horizontalGradient(listOf(PrimaryNeon.copy(alpha = 0.5f), SecondaryNeon.copy(alpha = 0.3f)))
                else
                    Brush.horizontalGradient(listOf(GlassBorder, GlassBorder)),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon with glow
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Focus Mode",
                        color = TextLight,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isDetoxActive) "Active & Enforced" else "Not running right now",
                        color = accentColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Pulsing status dot
            Box(
                modifier = Modifier
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                if (isDetoxActive) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = pulseAlpha * 0.2f))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDetoxActive) accentColor.copy(alpha = pulseAlpha)
                            else accentColor
                        )
                )
            }
        }
    }
}

@Composable
fun ScreenTimeDonutCard(totalScreenTimeMs: Long, apps: List<AppUsage>) {
    val hours   = TimeUnit.MILLISECONDS.toHours(totalScreenTimeMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalScreenTimeMs) % 60

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(apps) {
        animProgress.animateTo(1f, tween(durationMillis = 1200, easing = LinearOutSlowInEasing))
    }

    var selectedIndex by remember(apps) { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceVariant, SurfaceDark)
                )
            )
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Card header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "SCREEN TIME TODAY",
                        style = OverlineTextStyle,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap a slice to highlight",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                }
                // Total time pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(PrimaryNeon.copy(alpha = 0.12f))
                        .border(
                            1.dp,
                            PrimaryNeon.copy(alpha = 0.3f),
                            RoundedCornerShape(percent = 50)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ── Donut chart
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        apps = apps,
                        totalMs = totalScreenTimeMs,
                        animProgress = animProgress.value,
                        selectedIndex = selectedIndex,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Center label
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val centerLabel = if (selectedIndex >= 0 && selectedIndex < apps.size) {
                            val app = apps[selectedIndex]
                            val h = TimeUnit.MILLISECONDS.toHours(app.totalTimeInForeground)
                            val m = TimeUnit.MILLISECONDS.toMinutes(app.totalTimeInForeground) % 60
                            if (h > 0) "${h}h ${m}m" else "${m}m"
                        } else {
                            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
                        }
                        val centerName = if (selectedIndex >= 0 && selectedIndex < apps.size)
                            apps[selectedIndex].appName.take(9)
                        else "Total"

                        Text(
                            text = centerLabel,
                            color = TextLight,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = centerName,
                            color = TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // ── Legend ───────────────────────────────────────────────────
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    apps.take(CHART_COLORS.size).forEachIndexed { index, app ->
                        val color = CHART_COLORS[index % CHART_COLORS.size]
                        val isSelected = selectedIndex == index
                        LegendRow(
                            color = color,
                            appName = app.appName,
                            isHighlighted = isSelected,
                            onClick = {
                                selectedIndex = if (selectedIndex == index) -1 else index
                            }
                        )
                    }
                }
            }
        }
    }
}

// ── Donut Chart Canvas (logic unchanged, visuals enhanced) ────────────────────

@Composable
private fun DonutChart(
    apps: List<AppUsage>,
    totalMs: Long,
    animProgress: Float,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    val strokeWidth = 38f
    val gapAngle    = 3f

    Canvas(modifier = modifier) {
        val diameter = minOf(size.width, size.height) - strokeWidth
        val topLeft  = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize  = Size(diameter, diameter)

        if (apps.isEmpty() || totalMs == 0L) {
            drawArc(
                color      = Color.White.copy(alpha = 0.06f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            return@Canvas
        }

        // Track ring background
        drawArc(
            color      = Color.White.copy(alpha = 0.04f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter  = false,
            topLeft    = topLeft,
            size       = arcSize,
            style      = Stroke(width = strokeWidth + 4f, cap = StrokeCap.Butt)
        )

        var startAngle = -90f

        apps.take(CHART_COLORS.size).forEachIndexed { index, app ->
            val fraction   = app.totalTimeInForeground.toFloat() / totalMs.toFloat()
            val sweepAngle = (fraction * 360f - gapAngle).coerceAtLeast(0f) * animProgress
            val color      = CHART_COLORS[index % CHART_COLORS.size]
            val isSelected = selectedIndex == index

            // Glow layer behind selected slice
            if (isSelected) {
                drawArc(
                    color      = color.copy(alpha = 0.25f),
                    startAngle = startAngle - 2f,
                    sweepAngle = sweepAngle + 4f,
                    useCenter  = false,
                    topLeft    = Offset(topLeft.x - 10f, topLeft.y - 10f),
                    size       = Size(arcSize.width + 20f, arcSize.height + 20f),
                    style      = Stroke(width = strokeWidth + 18f, cap = StrokeCap.Butt)
                )
            }

            drawArc(
                color      = if (isSelected) color
                else color.copy(alpha = if (selectedIndex == -1) 0.85f else 0.30f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter  = false,
                topLeft    = if (isSelected) Offset(topLeft.x - 6f, topLeft.y - 6f) else topLeft,
                size       = if (isSelected) Size(arcSize.width + 12f, arcSize.height + 12f) else arcSize,
                style      = Stroke(
                    width = if (isSelected) strokeWidth + 8f else strokeWidth,
                    cap   = StrokeCap.Butt
                )
            )
            startAngle += sweepAngle + gapAngle
        }
    }
}

// ── Legend Row ────────────────────────────────────────────────────────────────

@Composable
private fun LegendRow(
    color: Color,
    appName: String,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isHighlighted) color.copy(alpha = 0.12f) else Color.Transparent
            )
            .then(
                if (isHighlighted) Modifier.border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text       = appName.take(14),
            style      = MaterialTheme.typography.labelSmall,
            color      = if (isHighlighted) color else TextMuted,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            maxLines   = 1
        )
    }
}

@Composable
fun TopAppsBreakdownCard(apps: List<AppUsage>, totalMs: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SurfaceVariant, SurfaceDark)
                )
            )
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Card header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TOP APPS",
                        style = OverlineTextStyle,
                        color = TextMuted
                    )
                    Text(
                        text = "Most used today",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryNeon.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${apps.take(CHART_COLORS.size).size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No usage data available yet.",
                        color = TextGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    apps.take(CHART_COLORS.size).forEachIndexed { index, app ->
                        AppUsageBar(
                            app     = app,
                            totalMs = totalMs,
                            color   = CHART_COLORS[index % CHART_COLORS.size],
                            rank    = index + 1
                        )
                        if (index < apps.take(CHART_COLORS.size).lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(DividerSubtle)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── App Usage Bar

@Composable
private fun AppUsageBar(app: AppUsage, totalMs: Long, color: Color, rank: Int = 0) {
    val hrs      = TimeUnit.MILLISECONDS.toHours(app.totalTimeInForeground)
    val mins     = TimeUnit.MILLISECONDS.toMinutes(app.totalTimeInForeground) % 60
    val secs     = TimeUnit.MILLISECONDS.toSeconds(app.totalTimeInForeground) % 60
    val timeLabel = when {
        hrs > 0  -> "${hrs}h ${mins}m"
        mins > 0 -> "${mins}m ${secs}s"
        else     -> "${secs}s"
    }
    val fraction   = if (totalMs > 0) app.totalTimeInForeground.toFloat() / totalMs.toFloat() else 0f
    val pct        = (fraction * 100).toInt()

    val animFraction = remember { Animatable(0f) }
    LaunchedEffect(fraction) {
        animFraction.animateTo(fraction, tween(durationMillis = 900, easing = LinearOutSlowInEasing))
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank badge
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rank",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = app.appName,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextLight
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = timeLabel,
                    style      = MaterialTheme.typography.bodySmall,
                    color      = color,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text  = "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Enhanced progress bar with rounded track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.10f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animFraction.value)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.6f))
                        )
                    )
            )
        }
    }
}


@Composable
fun UsagePermissionCard() {
    val context = LocalContext.current
    BasePermissionCard(
        title       = "Usage Access Required",
        description = "DetoxDroid needs Usage Access to track screen time.",
        onClick     = {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try { context.startActivity(intent) } catch (_: Exception) {}
        }
    )
}

@Composable
fun AccessibilityPermissionCard() {
    val context = LocalContext.current
    BasePermissionCard(
        title       = "Accessibility Required",
        description = "Required to detect when you open a blocked app.",
        onClick     = {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try { context.startActivity(intent) } catch (_: Exception) {}
        }
    )
}

@Composable
fun OverlayPermissionCard() {
    val context = LocalContext.current
    BasePermissionCard(
        title       = "Display Over Other Apps",
        description = "Required to show the blocking screen over distracting apps.",
        onClick     = {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    )
}

@Composable
private fun BasePermissionCard(title: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ErrorRed.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(ErrorRed.copy(alpha = 0.5f), OrangeAccent.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text       = title,
                    color      = ErrorRed,
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text  = description,
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grant permission button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(ErrorRed, OrangeAccent.copy(alpha = 0.8f))
                        )
                    )
                    .clickable { onClick() }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Grant Permission",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style      = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ── Offset extension helper
private fun Modifier.offset(x: Dp = 0.dp, y: Dp = 0.dp): Modifier =
    this.padding(start = x.coerceAtLeast(0.dp), top = y.coerceAtLeast(0.dp))