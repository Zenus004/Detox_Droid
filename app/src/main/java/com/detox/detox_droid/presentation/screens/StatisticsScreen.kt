package com.detox.detox_droid.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.presentation.viewmodels.StatisticsViewModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.ChartCyan
import com.detox.detox_droid.ui.theme.ChartGreen
import com.detox.detox_droid.ui.theme.ChartOrange
import com.detox.detox_droid.ui.theme.ChartPink
import com.detox.detox_droid.ui.theme.ChartPurple
import com.detox.detox_droid.ui.theme.ChartRed
import com.detox.detox_droid.ui.theme.DividerSubtle
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

// Chart colors reused from Dashboard for visual consistency
private val WEEKLY_CHART_COLORS = listOf(
    ChartCyan, ChartPink, ChartGreen, ChartOrange, ChartPurple, ChartRed
)


@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest)
    ) {
        // ── Ambient orbs ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopEnd)
                .blur(130.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryNeonDim, Color.Transparent)
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
                        colors = listOf(PurpleAccent.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── Header ───────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    PrimaryNeon.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column {
                        Text(
                            text       = "Insights & Trends",
                            color      = TextLight,
                            style      = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Your weekly screen time breakdown",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(listOf(PrimaryNeon, OrangeAccent)),
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }

            // ── Loading ───────────────────────────────────────────────────────
            if (state.isLoading) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(360.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color       = PrimaryNeon,
                                strokeWidth = 2.dp,
                                modifier    = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Crunching your data…",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {

                // ── Total weekly screen time hero card ────────────────────────
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        WeeklyHeroCard(
                            totalScreenTimeMs = state.totalWeeklyTimeMs,
                            previousWeeklyTimeMs = state.previousWeeklyTimeMs
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── Quick stat chips ──────────────────────────────────────────
                item {
                    val totalHours = TimeUnit.MILLISECONDS.toHours(state.totalWeeklyTimeMs)
                    val dailyAvgMs = state.totalWeeklyTimeMs / 7
                    val topApp     = state.weeklyUsage.firstOrNull()

                    Row(
                        modifier              = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickStatCard(
                            icon        = Icons.Rounded.Timer,
                            iconColor   = PrimaryNeon,
                            label       = "Daily Avg",
                            value       = run {
                                val h = TimeUnit.MILLISECONDS.toHours(dailyAvgMs)
                                val m = TimeUnit.MILLISECONDS.toMinutes(dailyAvgMs) % 60
                                if (h > 0) "${h}h ${m}m" else "${m}m"
                            },
                            modifier    = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            icon        = Icons.Rounded.CalendarMonth,
                            iconColor   = SecondaryNeon,
                            label       = "This Week",
                            value       = "${totalHours}h total",
                            modifier    = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            icon        = Icons.Rounded.BarChart,
                            iconColor   = OrangeAccent,
                            label       = "Top App",
                            value       = topApp?.appName?.take(10) ?: "—",
                            modifier    = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Weekly app analytics header ───────────────────────────────
                item {
                    Row(
                        modifier              = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text  = "WEEKLY APP ANALYTICS",
                                style = OverlineTextStyle,
                                color = TextMuted
                            )
                            Text(
                                text  = "Time spent per app this week",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(OrangeAccent.copy(alpha = 0.10f))
                                .border(
                                    1.dp,
                                    OrangeAccent.copy(alpha = 0.25f),
                                    RoundedCornerShape(percent = 50)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text  = "${state.weeklyUsage.size} apps",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangeAccent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Glass container top cap ───────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(SurfaceVariant)
                            .border(
                                1.dp,
                                Brush.verticalGradient(listOf(GlassBorder, Color.Transparent)),
                                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                            .height(12.dp)
                    )
                }

                // ── App rows ──────────────────────────────────────────────────
                if (state.weeklyUsage.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Rounded.QueryStats,
                                    contentDescription = null,
                                    tint     = TextGray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "No weekly data yet",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    val maxMs = state.weeklyUsage.maxOfOrNull { it.totalTimeInForeground } ?: 1L

                    itemsIndexed(
                        items = state.weeklyUsage,
                        key   = { _, app -> app.packageName }
                    ) { index, app ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            WeeklyAppRow(
                                app     = app,
                                maxMs   = maxMs,
                                color   = WEEKLY_CHART_COLORS[index % WEEKLY_CHART_COLORS.size],
                                rank    = index + 1
                            )
                        }
                    }
                }

                // ── Glass container bottom cap ────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(SurfaceDark)
                            .border(
                                1.dp,
                                Brush.verticalGradient(listOf(Color.Transparent, GlassBorder)),
                                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            )
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}


@Composable
private fun WeeklyHeroCard(totalScreenTimeMs: Long, previousWeeklyTimeMs: Long) {
    val hours   = TimeUnit.MILLISECONDS.toHours(totalScreenTimeMs)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalScreenTimeMs) % 60

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        PrimaryNeon.copy(alpha = 0.12f),
                        OrangeAccent.copy(alpha = 0.06f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(PrimaryNeon.copy(alpha = 0.40f), OrangeAccent.copy(alpha = 0.20f))
                ),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "WEEKLY SCREEN TIME",
                        style = OverlineTextStyle,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Hero number — reuses ScreenTimeOverview logic inline for styling
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text       = "$hours",
                            color      = PrimaryNeon,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 52.sp,
                            lineHeight = 56.sp
                        )
                        Text(
                            text     = "h",
                            color    = TextMuted,
                            style    = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text       = "$minutes",
                            color      = PrimaryNeon,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 52.sp,
                            lineHeight = 56.sp
                        )
                        Text(
                            text     = "m",
                            color    = TextMuted,
                            style    = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                        )
                    }
                    Text(
                        text  = "across 7 days",
                        color = TextGray,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                val isTrendingUp = totalScreenTimeMs > previousWeeklyTimeMs
                val trendColor = if (isTrendingUp) ChartRed else PrimaryNeon
                val trendIcon = if (isTrendingUp) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(trendColor.copy(alpha = 0.10f))
                        .border(1.dp, trendColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint     = trendColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thin decorative gradient bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(PrimaryNeon, OrangeAccent.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )
        }
    }
}


@Composable
private fun QuickStatCard(
    icon:      ImageVector,
    iconColor: Color,
    label:     String,
    value:     String,
    modifier:  Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark)))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint     = iconColor,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = value,
                color      = TextLight,
                fontWeight = FontWeight.Bold,
                style      = MaterialTheme.typography.labelLarge,
                maxLines   = 1
            )
            Text(
                text  = label,
                color = TextGray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}


@Composable
private fun WeeklyAppRow(
    app:    com.detox.detox_droid.domain.models.AppUsage,
    maxMs:  Long,
    color:  Color,
    rank:   Int
) {
    val hrs      = TimeUnit.MILLISECONDS.toHours(app.totalTimeInForeground)
    val mins     = TimeUnit.MILLISECONDS.toMinutes(app.totalTimeInForeground) % 60
    val secs     = TimeUnit.MILLISECONDS.toSeconds(app.totalTimeInForeground) % 60
    val timeLabel = when {
        hrs > 0  -> "${hrs}h ${mins}m"
        mins > 0 -> "${mins}m ${secs}s"
        else     -> "${secs}s"
    }

    val fraction = if (maxMs > 0) app.totalTimeInForeground.toFloat() / maxMs.toFloat() else 0f

    val animFraction = remember { Animatable(0f) }
    LaunchedEffect(fraction) {
        animFraction.animateTo(
            fraction,
            tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rank badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "$rank",
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color      = color
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text       = app.appName,
                        color      = TextLight,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text       = timeLabel,
                    color      = color,
                    style      = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated gradient bar relative to top app
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color.copy(alpha = 0.09f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animFraction.value)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.5f)))
                        )
                )
            }
        }

        // Row divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerSubtle)
                .align(Alignment.BottomCenter)
        )
    }
}