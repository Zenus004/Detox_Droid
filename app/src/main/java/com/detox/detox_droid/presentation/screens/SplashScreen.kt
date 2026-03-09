package com.detox.detox_droid.presentation.screens

import android.content.Intent
import androidx.core.net.toUri
import android.provider.Settings
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            context.startActivity(
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        navController.popBackStack()
    }

    // ── Entry animations ──────────────────────────────────────────────────────
    val logoScale   = remember { Animatable(0.6f) }
    val logoAlpha   = remember { Animatable(0f) }
    val textAlpha   = remember { Animatable(0f) }
    val subtextAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f,   tween(700, easing = FastOutSlowInEasing))
        logoAlpha.animateTo(1f,   tween(600, easing = FastOutSlowInEasing))
        textAlpha.animateTo(1f,   tween(500, 200, FastOutSlowInEasing))
        subtextAlpha.animateTo(1f, tween(500, 400, FastOutSlowInEasing))
    }

    // ── Infinite animations ───────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Outer ring rotation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "ringRotation"
    )

    // Reverse ring rotation
    val ringRotationRev by infiniteTransition.animateFloat(
        initialValue  = 360f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        ),
        label = "ringRotationRev"
    )

    // Pulse glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.55f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Loading arc sweep
    val loadingArc by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "loadingArc"
    )

    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(BackgroundDeepest),
        contentAlignment  = Alignment.Center
    ) {
        // ── Deep background glow orb ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(400.dp)
                .blur(160.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryNeon.copy(alpha = pulseAlpha * 0.5f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // ── Secondary purple orb ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .blur(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SecondaryNeon.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ring system ──────────────────────────────────────────────
            Box(
                modifier         = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing outer glow halo
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(24.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryNeon.copy(alpha = pulseAlpha),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Rotating outer arc ring (Canvas)
                Canvas(modifier = Modifier.size(180.dp)) {
                    val strokeW  = 3.dp.toPx()
                    val inset    = strokeW / 2f
                    val arcSize  = androidx.compose.ui.geometry.Size(
                        size.width - strokeW, size.height - strokeW
                    )
                    val topLeft  = androidx.compose.ui.geometry.Offset(inset, inset)

                    // Outer decorative dashed-style arc (3 segments, rotating)
                    listOf(0f, 120f, 240f).forEach { baseAngle ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(PrimaryNeon.copy(alpha = 0.8f), Color.Transparent)
                            ),
                            startAngle = baseAngle + ringRotation,
                            sweepAngle = 80f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                }

                // Reverse-rotating inner arc ring
                Canvas(modifier = Modifier.size(152.dp)) {
                    val strokeW = 2.dp.toPx()
                    val inset   = strokeW / 2f
                    val arcSize = androidx.compose.ui.geometry.Size(
                        size.width - strokeW, size.height - strokeW
                    )
                    val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)

                    listOf(0f, 180f).forEach { baseAngle ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(SecondaryNeon.copy(alpha = 0.6f), Color.Transparent)
                            ),
                            startAngle = baseAngle + ringRotationRev,
                            sweepAngle = 60f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                }

                // Inner logo circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryNeon.copy(alpha = 0.18f),
                                    Color(0xFF1E2030)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // App monogram
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = "D",
                            color      = PrimaryNeon,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 44.sp,
                            lineHeight = 46.sp,
                            fontFamily = FontFamily.Default
                        )
                    }
                }

                // Spinning loading arc at outermost edge
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeW = 2.5.dp.toPx()
                    val inset   = strokeW / 2f
                    drawArc(
                        brush      = Brush.sweepGradient(
                            listOf(
                                PrimaryNeon,
                                SecondaryNeon.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        startAngle = loadingArc - 90f,
                        sweepAngle = 120f,
                        useCenter  = false,
                        topLeft    = androidx.compose.ui.geometry.Offset(inset, inset),
                        size       = androidx.compose.ui.geometry.Size(
                            size.width - strokeW, size.height - strokeW
                        ),
                        style      = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text       = "Detox Droid",
                color      = TextLight.copy(alpha = textAlpha.value),
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 30.sp,
                letterSpacing = (-0.5).sp,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text      = "Reclaim your focus",
                color     = TextMuted.copy(alpha = subtextAlpha.value),
                fontSize  = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Status text ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(PrimaryNeon.copy(alpha = 0.08f))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text      = "Opening Settings…",
                    color     = TextGray,
                    fontSize  = 12.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}