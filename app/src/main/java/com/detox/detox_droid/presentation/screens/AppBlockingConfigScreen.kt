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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.presentation.viewmodels.AppBlockingConfigViewModel
import com.detox.detox_droid.presentation.viewmodels.AppInfoUiModel
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.DividerSubtle
import com.detox.detox_droid.ui.theme.ErrorRed
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockingConfigScreen(
    viewModel: AppBlockingConfigViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest)
    ) {
        // ── Ambient background orb ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .blur(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ErrorRed.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.09f), Color.Transparent)
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
                                    ErrorRed.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column {
                        Text(
                            text       = "App Blocking",
                            style      = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color      = TextLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Toggle apps to block during focus sessions",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Neon divider — red tint for this screen
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ErrorRed, OrangeAccent)
                                    ),
                                    RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }

            // ── Search bar ───────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value       = state.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                "Search apps…",
                                color = TextGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = PrimaryNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors    = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = PrimaryNeon.copy(alpha = 0.7f),
                            unfocusedBorderColor    = GlassBorder,
                            focusedContainerColor   = SurfaceVariant,
                            unfocusedContainerColor = SurfaceDark,
                            cursorColor             = PrimaryNeon,
                            focusedTextColor        = TextLight,
                            unfocusedTextColor      = TextLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── System apps toggle ────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(SurfaceVariant, SurfaceDark)
                                )
                            )
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .fillMaxWidth(),
                            verticalAlignment   = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PrimaryNeon.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PhoneAndroid,
                                        contentDescription = null,
                                        tint     = PrimaryNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Show system apps",
                                        style      = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = TextLight
                                    )
                                    Text(
                                        "Phone, Messages, Camera…",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }
                            Switch(
                                checked         = state.showSystemApps,
                                onCheckedChange = { viewModel.toggleShowSystemApps(it) },
                                colors          = SwitchDefaults.colors(
                                    checkedTrackColor   = PrimaryNeon.copy(alpha = 0.55f),
                                    checkedThumbColor   = PrimaryNeon,
                                    uncheckedTrackColor = SurfaceVariant,
                                    uncheckedThumbColor = TextGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Loading state ────────────────────────────────────────────────
            if (state.isLoading) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color       = PrimaryNeon,
                                strokeWidth = 2.dp,
                                modifier    = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Loading apps…",
                                color = TextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                val appsToShow = state.filteredApps

                if (appsToShow.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Rounded.Apps,
                                    contentDescription = null,
                                    tint     = TextGray,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text  = if (state.searchQuery.isBlank()) "No apps found."
                                    else "No match for \"${state.searchQuery}\".",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                } else {
                    // ── Stats row ────────────────────────────────────────────
                    item {
                        val systemCount = state.apps.count { it.isSystemApp }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatChip(
                                label = "${state.blockedCount} blocked",
                                color = ErrorRed,
                                icon  = Icons.Rounded.Block
                            )
                            StatChip(
                                label = "${appsToShow.size} shown",
                                color = PrimaryNeon,
                                icon  = Icons.Rounded.Apps
                            )
                            if (state.showSystemApps) {
                                StatChip(
                                    label = "$systemCount system",
                                    color = PurpleAccent,
                                    icon  = Icons.Rounded.Shield
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // ── App list inside a glass card container ────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(SurfaceVariant, SurfaceDark)
                                    )
                                )
                                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        ) {
                            Column {
                                // List header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment   = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text  = "INSTALLED APPS",
                                        style = OverlineTextStyle,
                                        color = TextMuted
                                    )
                                    Text(
                                        text  = "${appsToShow.size} apps",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextGray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(DividerSubtle)
                                )
                            }
                        }
                    }

                    items(appsToShow, key = { it.packageName }) { app ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            AppExceptionCard(
                                app           = app,
                                onToggleBlock = { viewModel.toggleAppBlockedState(app, it) }
                            )
                        }
                    }

                    item {
                        // Bottom cap of the glass container
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = 24.dp,
                                        bottomEnd   = 24.dp
                                    )
                                )
                                .background(SurfaceDark)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color.Transparent, GlassBorder)
                                    ),
                                    shape = RoundedCornerShape(
                                        bottomStart = 24.dp,
                                        bottomEnd   = 24.dp
                                    )
                                )
                                .height(16.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun StatChip(
    label: String,
    color: Color,
    icon:  androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint     = color,
                    modifier = Modifier.size(11.dp)
                )
            }
            Text(
                label,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color      = color
            )
        }
    }
}

@Composable
fun AppExceptionCard(
    app:           AppInfoUiModel,
    onToggleBlock: (Boolean) -> Unit
) {
    val (statusLabel, indicatorColor) = when {
        app.isWhitelisted -> "Always allowed" to PrimaryNeon
        app.isBlocked     -> "Will be blocked" to ErrorRed
        else              -> "Not tracked"     to TextGray
    }

    // Animate background color on toggle
    val cardBgColor by animateColorAsState(
        targetValue = when {
            app.isBlocked     -> ErrorRed.copy(alpha = 0.07f)
            app.isWhitelisted -> PrimaryNeon.copy(alpha = 0.05f)
            else              -> Color.Transparent
        },
        animationSpec = tween(300),
        label         = "cardBg"
    )



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBgColor)
    ) {
        // Left accent stripe for blocked apps
        if (app.isBlocked) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(56.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(
                        Brush.verticalGradient(listOf(ErrorRed, OrangeAccent.copy(alpha = 0.5f)))
                    )
            )
        }
        if (app.isWhitelisted) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(56.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(
                        Brush.verticalGradient(listOf(PrimaryNeon, SecondaryNeon.copy(alpha = 0.5f)))
                    )
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App initial avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(indicatorColor.copy(alpha = 0.12f))
                    .border(1.dp, indicatorColor.copy(alpha = 0.20f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = app.appName.take(1).uppercase(),
                    color      = indicatorColor,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = app.appName,
                    color      = TextLight,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(indicatorColor.copy(alpha = 0.10f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            statusLabel,
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color      = indicatorColor
                        )
                    }
                    if (app.isSystemApp) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PurpleAccent.copy(alpha = 0.08f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "System",
                                style = MaterialTheme.typography.labelSmall,
                                color = PurpleAccent.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Switch(
                checked         = app.isBlocked,
                onCheckedChange = onToggleBlock,
                enabled         = !app.isWhitelisted,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor          = ErrorRed,
                    checkedTrackColor          = ErrorRed.copy(alpha = 0.30f),
                    uncheckedThumbColor        = TextGray,
                    uncheckedTrackColor        = SurfaceVariant,
                    disabledCheckedThumbColor  = PrimaryNeon.copy(alpha = 0.5f),
                    disabledCheckedTrackColor  = PrimaryNeon.copy(alpha = 0.15f)
                )
            )
        }

        // Bottom row divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerSubtle)
                .align(Alignment.BottomCenter)
        )
    }
}