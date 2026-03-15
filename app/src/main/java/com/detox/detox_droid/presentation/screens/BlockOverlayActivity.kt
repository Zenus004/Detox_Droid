package com.detox.detox_droid.presentation.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.detox.detox_droid.presentation.viewmodels.BlockOverlayViewModel
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.OrangeAccent
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BlockOverlayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    startActivity(
                        Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                    finish()
                }
            }
        )
        val blockedPackage = intent.getStringExtra("BLOCKED_PACKAGE") ?: "Unknown App"

        setContent {
            MaterialTheme {
                BlockScreen(
                    packageName    = blockedPackage,
                    onGoHome       = {
                        startActivity(
                            Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        finish()
                    },
                    onPauseGranted = { finish() }
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BLOCK SCREEN  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun BlockScreen(
    packageName:    String,
    onGoHome:       () -> Unit,
    onPauseGranted: () -> Unit,
    viewModel:      BlockOverlayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var pendingPauseMinutes by remember { mutableStateOf(0) }

    LaunchedEffect(state.pausesRemaining) {
        if (pendingPauseMinutes > 0) {
            pendingPauseMinutes = 0
            onPauseGranted()
        }
    }

    val timeRemainingMs  = (state.detoxEndTimeMs - System.currentTimeMillis()).coerceAtLeast(0L)
    val hoursRemaining   = TimeUnit.MILLISECONDS.toHours(timeRemainingMs)
    val minsRemaining    = TimeUnit.MILLISECONDS.toMinutes(timeRemainingMs) % 60
    val timeLabel = when {
        hoursRemaining > 0 -> "${hoursRemaining}h ${minsRemaining}m remaining"
        minsRemaining > 0  -> "${minsRemaining}m remaining"
        else               -> ""
    }

    // ── Infinite animations ───────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "block")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.25f,
        targetValue   = 0.65f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "ringRot"
    )

    val ringRevRotation by infiniteTransition.animateFloat(
        initialValue  = 360f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "ringRevRot"
    )

    // Entry scale animation
    val entryScale = remember { Animatable(0.8f) }
    val entryAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entryScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        entryAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0E17),
                        Color(0xFF13141F),
                        Color(0xFF1A0A0A)   // subtle red tint at bottom — danger signal
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Deep ambient glow — red for danger ────────────────────────────────
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.Center)
                .blur(160.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ErrorRed.copy(alpha = pulseAlpha * 0.35f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Top-left purple accent orb
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopStart)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Bottom-right orange accent
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .blur(90.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(OrangeAccent.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // ── Main content ──────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Shield icon ring system ───────────────────────────────────────
            Box(
                modifier         = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outermost ambient red glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(28.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ErrorRed.copy(alpha = pulseAlpha * 0.6f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Rotating outer dashed arc — red
                Canvas(modifier = Modifier.size(180.dp)) {
                    val sw      = 2.5.dp.toPx()
                    val inset   = sw / 2f
                    val arcSize = Size(size.width - sw, size.height - sw)
                    val topLeft = Offset(inset, inset)

                    listOf(0f, 120f, 240f).forEach { base ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(ErrorRed.copy(alpha = 0.9f), Color.Transparent)
                            ),
                            startAngle = base + ringRotation,
                            sweepAngle = 75f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = sw, cap = StrokeCap.Round)
                        )
                    }
                }

                // Inner counter-rotating arc — orange
                Canvas(modifier = Modifier.size(148.dp)) {
                    val sw      = 2.dp.toPx()
                    val inset   = sw / 2f
                    val arcSize = Size(size.width - sw, size.height - sw)
                    val topLeft = Offset(inset, inset)

                    listOf(0f, 180f).forEach { base ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(OrangeAccent.copy(alpha = 0.7f), Color.Transparent)
                            ),
                            startAngle = base + ringRevRotation,
                            sweepAngle = 55f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = sw, cap = StrokeCap.Round)
                        )
                    }
                }

                // Inner shield circle
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ErrorRed.copy(alpha = 0.22f),
                                    Color(0xFF1E0A0A)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = ErrorRed.copy(alpha = 0.35f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint     = ErrorRed,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── "App Blocked" headline ────────────────────────────────────────
            Text(
                text       = "App Blocked",
                color      = TextLight,
                fontSize   = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Blocked app name badge ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ErrorRed.copy(alpha = 0.10f))
                    .border(1.dp, ErrorRed.copy(alpha = 0.30f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text       = packageName,
                    color      = ErrorRed,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    textAlign  = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Description ───────────────────────────────────────────────────
            Text(
                text      = "This app is blocked during your\nactive Focus Session.",
                color     = TextMuted,
                fontSize  = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp,
                modifier  = Modifier.padding(horizontal = 16.dp)
            )

            // ── Time remaining badge ──────────────────────────────────────────
            if (timeLabel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(OrangeAccent.copy(alpha = 0.10f))
                        .border(
                            1.dp,
                            OrangeAccent.copy(alpha = 0.30f),
                            RoundedCornerShape(percent = 50)
                        )
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text       = timeLabel,
                        color      = OrangeAccent,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Go Home CTA ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(PrimaryNeon, SecondaryNeon.copy(alpha = 0.85f))
                        )
                    )
                    .clickable { onGoHome() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = null,
                        tint     = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Got it, take me home",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Emergency pause section ───────────────────────────────────────
            if (state.pausesRemaining > 0) {

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(1.dp)
                        .background(GlassBorder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(bottom = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint     = OrangeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text  = "Emergency Pause  ·  ${state.pausesRemaining} left today",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier              = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(5, 15).forEach { mins ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (pendingPauseMinutes == 0)
                                        OrangeAccent.copy(alpha = 0.10f)
                                    else
                                        SurfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (pendingPauseMinutes == 0)
                                        OrangeAccent.copy(alpha = 0.40f)
                                    else
                                        GlassBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = pendingPauseMinutes == 0) {
                                    pendingPauseMinutes = mins
                                    viewModel.pauseDetox(mins)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text       = "$mins min",
                                    color      = if (pendingPauseMinutes == 0) OrangeAccent else TextGray,
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text  = "pause",
                                    color = if (pendingPauseMinutes == 0)
                                        OrangeAccent.copy(alpha = 0.65f) else TextGray.copy(alpha = 0.5f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

            } else {
                // No pauses left banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ErrorRed.copy(alpha = 0.07f))
                        .border(1.dp, ErrorRed.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text      = "No emergency pauses remaining today.",
                        color     = ErrorRed.copy(alpha = 0.80f),
                        fontSize  = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}