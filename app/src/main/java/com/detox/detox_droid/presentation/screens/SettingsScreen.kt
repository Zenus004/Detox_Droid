package com.detox.detox_droid.presentation.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.presentation.viewmodels.SettingsViewModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.DividerSubtle
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OrangeAccent
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceDark
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // ── Ambient orbs (now inside scrollable container) ──────────────────
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .offset(x = 0.dp, y = 0.dp)
                        .blur(120.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SecondaryNeon.copy(alpha = 0.08f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 0.dp, y = 100.dp)
                        .blur(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PurpleAccent.copy(alpha = 0.10f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )

                Column {
                    // ── Header ───────────────────────────────────────────────────────
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
                                text       = "Settings",
                                style      = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color      = TextLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text  = "Personalise your DetoxDroid experience",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(2.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(SecondaryNeon, PrimaryNeon)
                                        ),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }

                    Column(
                        modifier            = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // ── Session Status ────────────────────────────────────────────
                        SectionLabel("Session Status")
                        Spacer(modifier = Modifier.height(4.dp))
                        SessionStatusCard(
                            isActive            = state.isDetoxModeActive,
                            pausesRemaining     = state.pausesRemainingToday,
                            dailyPauseLimit     = state.dailyPauseLimit
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── Emergency Pauses ──────────────────────────────────────────
                        SectionLabel("Emergency Pauses")
                        Spacer(modifier = Modifier.height(4.dp))
                        PauseLimitCard(
                            dailyPauseLimit      = state.dailyPauseLimit,
                            onPreview            = { viewModel.previewPauseLimit(it) },
                            onChangeFinished     = { viewModel.updatePauseLimit(state.dailyPauseLimit) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── Emergency Override ────────────────────────────────────────
                        SectionLabel("Emergency Override")
                        Spacer(modifier = Modifier.height(4.dp))
                        EmergencyOverrideCard(
                            isSessionActive = state.isDetoxModeActive,
                            onKillSession   = { viewModel.disableActiveDetoxSession() }
                        )

                        Spacer(modifier = Modifier.height(15.dp)) // Sufficient space for Nav
                    }
                }
            }
        }
    }
}


@Composable
private fun SessionStatusCard(
    isActive:        Boolean,
    pausesRemaining: Int,
    dailyPauseLimit: Int
) {
    // Pulse animation for active dot
    val infiniteTransition = rememberInfiniteTransition(label = "statusPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.4f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val pauseFraction = if (dailyPauseLimit > 0)
        pausesRemaining.toFloat() / dailyPauseLimit.toFloat() else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isActive)
                    Brush.linearGradient(
                        listOf(PrimaryNeon.copy(alpha = 0.09f), SecondaryNeon.copy(alpha = 0.05f))
                    )
                else
                    Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark))
            )
            .border(
                1.dp,
                if (isActive)
                    Brush.horizontalGradient(
                        listOf(PrimaryNeon.copy(alpha = 0.45f), SecondaryNeon.copy(alpha = 0.25f))
                    )
                else
                    Brush.horizontalGradient(listOf(GlassBorder, GlassBorder)),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Top row — Focus Mode status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Icon badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (isActive) PrimaryNeon.copy(alpha = 0.12f) else SurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint     = if (isActive) PrimaryNeon else TextGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Focus Mode",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextLight
                    )
                    Text(
                        if (isActive) "Active & blocking" else "Not running",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isActive) PrimaryNeon else TextGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Pulsing status dot
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp)) {
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(PrimaryNeon.copy(alpha = pulseAlpha * 0.18f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) PrimaryNeon.copy(alpha = pulseAlpha)
                                else TextGray.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(1.dp)
                    .background(DividerSubtle)
            )

            // Bottom row — Emergency pauses remaining
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Icon badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryNeon.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                        tint     = SecondaryNeon,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Emergency Pauses Left",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color      = TextLight
                    )
                    Text(
                        "Resets at midnight",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Animated pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(SecondaryNeon.copy(alpha = 0.12f))
                        .border(
                            1.dp,
                            SecondaryNeon.copy(alpha = 0.30f),
                            RoundedCornerShape(percent = 50)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "$pausesRemaining / $dailyPauseLimit",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color      = SecondaryNeon
                    )
                }
            }

            // Pause quota mini progress bar
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SecondaryNeon.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pauseFraction.coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(listOf(SecondaryNeon, PrimaryNeon))
                        )
                )
            }
        }
    }
}


@Composable
private fun PauseLimitCard(
    dailyPauseLimit:  Int,
    onPreview:        (Int) -> Unit,
    onChangeFinished: () -> Unit
) {
    val sliderProgress = (dailyPauseLimit - 1f) / 9f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark)))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(PrimaryNeon.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        tint     = PrimaryNeon,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Daily Pause Limit",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextLight
                    )
                    Text(
                        "Max pauses per Focus Session",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Value badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryNeon.copy(alpha = 0.12f))
                        .border(
                            1.dp,
                            PrimaryNeon.copy(alpha = 0.30f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "$dailyPauseLimit",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = PrimaryNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom gradient track bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PrimaryNeon.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderProgress.coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(listOf(PrimaryNeon, SecondaryNeon))
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value                = dailyPauseLimit.toFloat(),
                onValueChange        = { onPreview(it.toInt()) },
                onValueChangeFinished = onChangeFinished,
                valueRange           = 1f..10f,
                steps                = 8,
                colors               = SliderDefaults.colors(
                    thumbColor         = PrimaryNeon,
                    activeTrackColor   = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor    = Color.Transparent,
                    inactiveTickColor  = Color.Transparent
                )
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1 pause", color = TextGray, style = MaterialTheme.typography.labelSmall)
                Text("10 pauses", color = TextGray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun EmergencyOverrideCard(
    isSessionActive: Boolean,
    onKillSession:   () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ErrorRed.copy(alpha = 0.06f))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(ErrorRed.copy(alpha = 0.40f), OrangeAccent.copy(alpha = 0.20f))
                ),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(ErrorRed.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint     = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Kill Session",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = ErrorRed
                    )
                    Text(
                        "Immediately ends Focus Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorRed.copy(alpha = 0.65f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ErrorRed.copy(alpha = 0.15f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "Immediately ends your active Focus Session and disables all blocking.",
                style    = MaterialTheme.typography.bodySmall,
                color    = TextMuted,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // Kill button — gradient when active, muted when disabled
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (isSessionActive)
                            Brush.horizontalGradient(listOf(ErrorRed, OrangeAccent.copy(alpha = 0.8f)))
                        else
                            Brush.horizontalGradient(listOf(SurfaceVariant, SurfaceVariant))
                    )
                    .then(
                        if (!isSessionActive)
                            Modifier.border(1.dp, GlassBorder, RoundedCornerShape(15.dp))
                        else Modifier
                    )
                    .clickable(enabled = isSessionActive) { onKillSession() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PowerSettingsNew,
                        contentDescription = null,
                        tint     = if (isSessionActive) Color.White else TextGray,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Kill Focus Session Now",
                        fontWeight = FontWeight.Bold,
                        color      = if (isSessionActive) Color.White else TextGray,
                        style      = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text.uppercase(),
        style         = MaterialTheme.typography.labelSmall,
        fontWeight    = FontWeight.SemiBold,
        color         = TextMuted,
        letterSpacing = 1.2.sp,
        modifier      = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

