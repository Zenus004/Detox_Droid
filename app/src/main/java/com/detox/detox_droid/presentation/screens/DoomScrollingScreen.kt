package com.detox.detox_droid.presentation.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.presentation.viewmodels.DoomScrollingViewModel
import com.detox.detox_droid.presentation.viewmodels.TrackedApp
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.DividerSubtle
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OrangeAccent
import com.detox.detox_droid.ui.theme.OverlineTextStyle
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceDark
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted

@Composable
fun DoomScrollingScreen(
    viewModel: DoomScrollingViewModel = hiltViewModel()
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
                .align(Alignment.TopCenter)
                .blur(130.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SecondaryNeon.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Header ───────────────────────────────────────────────────────
            item {
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
                            text       = "Doom Scroll Guard",
                            style      = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color      = TextLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Break continuous scrolling habits automatically",
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
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                    // ── Master toggle card ────────────────────────────────────
                    GuardStatusCard(
                        isEnabled = state.isEnabled,
                        onToggle  = { viewModel.setEnabled(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Threshold slider card ─────────────────────────────────
                    ThresholdSliderCard(
                        isEnabled         = state.isEnabled,
                        thresholdMinutes  = state.thresholdMinutes,
                        onValueChange     = { viewModel.setThresholdMinutes(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── How it works card ─────────────────────────────────────
                    HowItWorksCard(thresholdMinutes = state.thresholdMinutes)

                    Spacer(modifier = Modifier.height(28.dp))

                    // ── Tracked apps section header ───────────────────────────
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .alpha(if (state.isEnabled) 1f else 0.45f),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text  = "TRACKED APPS",
                                style = OverlineTextStyle,
                                color = TextMuted
                            )
                            Text(
                                text  = "Toggle which apps the guard monitors",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(SecondaryNeon.copy(alpha = 0.10f))
                                .border(
                                    1.dp,
                                    SecondaryNeon.copy(alpha = 0.25f),
                                    RoundedCornerShape(percent = 50)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text  = "${state.trackedApps.count { it.isTracked }} active",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SecondaryNeon
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // ── Tracked apps list ─────────────────────────────────────────────
            item {
                // Glass container top cap
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

            items(state.trackedApps, key = { it.packageName }) { app ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    TrackedAppRow(
                        app      = app,
                        enabled  = state.isEnabled,
                        onToggle = { viewModel.toggleTrackedApp(app.packageName, it) }
                    )
                }
            }

            item {
                // Glass container bottom cap
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


@Composable
private fun GuardStatusCard(isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    val borderBrush = if (isEnabled)
        Brush.horizontalGradient(listOf(SecondaryNeon.copy(alpha = 0.55f), PrimaryNeon.copy(alpha = 0.3f)))
    else
        Brush.horizontalGradient(listOf(GlassBorder, GlassBorder))

    val bgBrush = if (isEnabled)
        Brush.horizontalGradient(
            listOf(SecondaryNeon.copy(alpha = 0.09f), PrimaryNeon.copy(alpha = 0.05f))
        )
    else
        Brush.horizontalGradient(listOf(SurfaceVariant, SurfaceDark))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgBrush)
            .border(1.dp, borderBrush, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (isEnabled) SecondaryNeon.copy(alpha = 0.12f)
                            else SurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RemoveRedEye,
                        contentDescription = null,
                        tint     = if (isEnabled) SecondaryNeon else TextGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text       = "Guard Status",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextLight
                    )
                    Text(
                        text  = if (isEnabled) "Active — monitoring tracked apps" else "Disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isEnabled) SecondaryNeon else TextGray
                    )
                }
            }
            Switch(
                checked         = isEnabled,
                onCheckedChange = onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = Color.Black,
                    checkedTrackColor   = SecondaryNeon,
                    uncheckedThumbColor = TextGray,
                    uncheckedTrackColor = SurfaceVariant
                )
            )
        }
    }
}


@Composable
private fun ThresholdSliderCard(
    isEnabled:        Boolean,
    thresholdMinutes: Int,
    onValueChange:    (Int) -> Unit
) {
    // Animate threshold value color: green (short) → orange → red (long)
    val valueColor by animateColorAsState(
        targetValue = when {
            thresholdMinutes <= 10 -> PrimaryNeon
            thresholdMinutes <= 30 -> OrangeAccent
            else                   -> SecondaryNeon
        },
        animationSpec = tween(400),
        label         = "thresholdColor"
    )

    // Progress fraction for the custom fill bar
    val sliderProgress = (thresholdMinutes - 1f) / 59f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.45f)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark)))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "INTERVENTION THRESHOLD",
                        style = OverlineTextStyle,
                        color = TextMuted
                    )
                    Text(
                        text  = "Overlay appears after this long in one app",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                // Animated value badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(valueColor.copy(alpha = 0.12f))
                        .border(
                            1.dp,
                            valueColor.copy(alpha = 0.35f),
                            RoundedCornerShape(percent = 50)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text       = "$thresholdMinutes min",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color      = valueColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Custom slider track preview ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PrimaryNeon.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderProgress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(PrimaryNeon, valueColor)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value         = thresholdMinutes.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange    = 1f..60f,
                steps         = 58,
                enabled       = isEnabled,
                colors        = SliderDefaults.colors(
                    thumbColor          = valueColor,
                    activeTrackColor    = Color.Transparent,
                    inactiveTrackColor  = Color.Transparent,
                    activeTickColor     = Color.Transparent,
                    inactiveTickColor   = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "1 min",
                    color = TextGray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "60 min",
                    color = TextGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// HOW IT WORKS CARD
// ══════════════════════════════════════════════════════════════════════════════

private data class HowItWorksStep(
    val icon:        ImageVector,
    val iconTint:    Color,
    val title:       String,
    val description: String
)

@Composable
private fun HowItWorksCard(thresholdMinutes: Int) {
    val steps = listOf(
        HowItWorksStep(
            icon        = Icons.Rounded.PhoneAndroid,
            iconTint    = SecondaryNeon,
            title       = "Background monitoring",
            description = "App usage is tracked via Accessibility Service"
        ),
        HowItWorksStep(
            icon        = Icons.Rounded.HourglassEmpty,
            iconTint    = OrangeAccent,
            title       = "Threshold reached",
            description = "After $thresholdMinutes min in one tracked app, a gentle overlay appears"
        ),
        HowItWorksStep(
            icon        = Icons.Rounded.SelfImprovement,
            iconTint    = PrimaryNeon,
            title       = "Mindful choice",
            description = "Take a break — or consciously choose to keep scrolling"
        ),
        HowItWorksStep(
            icon        = Icons.Rounded.Loop,
            iconTint    = PurpleAccent,
            title       = "Auto reset",
            description = "Timer resets every time you switch apps"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(SecondaryNeon.copy(alpha = 0.07f), SurfaceDark)
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(SecondaryNeon.copy(alpha = 0.30f), GlassBorder)
                ),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(SecondaryNeon.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint     = SecondaryNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = "How it works",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = SecondaryNeon
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            steps.forEachIndexed { index, step ->
                Row(verticalAlignment = Alignment.Top) {
                    // Step icon in tinted badge
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(step.iconTint.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint     = step.iconTint,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = step.title,
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextLight
                        )
                        Text(
                            text     = step.description,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                if (index < steps.lastIndex) {
                    // Connector line between steps
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(2.dp)
                            .height(16.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(step.iconTint.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TRACKED APP ROW  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TrackedAppRow(
    app:     TrackedApp,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val accentColor by animateColorAsState(
        targetValue   = if (app.isTracked) SecondaryNeon else TextGray,
        animationSpec = tween(300),
        label         = "rowAccent"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.45f)
            .background(
                if (app.isTracked) SecondaryNeon.copy(alpha = 0.04f) else Color.Transparent
            )
    ) {
        // Left accent stripe when tracked
        if (app.isTracked) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(62.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(
                        Brush.verticalGradient(listOf(SecondaryNeon, PrimaryNeon.copy(alpha = 0.4f)))
                    )
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                .fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(1f)
            ) {
                // App initial avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.20f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = app.displayName.take(1).uppercase(),
                        color      = accentColor,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text       = app.displayName,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextLight
                    )
                    Text(
                        text  = app.packageName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Switch(
                checked         = app.isTracked,
                onCheckedChange = onToggle,
                enabled         = enabled,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = Color.Black,
                    checkedTrackColor   = SecondaryNeon,
                    uncheckedThumbColor = TextGray,
                    uncheckedTrackColor = SurfaceVariant
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerSubtle)
                .align(Alignment.BottomCenter)
        )
    }
}