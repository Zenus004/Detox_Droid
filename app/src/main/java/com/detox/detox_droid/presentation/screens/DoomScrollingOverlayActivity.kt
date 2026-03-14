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
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import com.detox.detox_droid.ui.theme.TextLight
import com.detox.detox_droid.ui.theme.TextMuted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoomScrollingOverlayActivity : ComponentActivity() {

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

        val packageName = intent.getStringExtra("SCROLLING_PACKAGE") ?: "this app"

        setContent {
            MaterialTheme {
                DoomScrollScreen(
                    packageName  = packageName,
                    onTakeABreak = {
                        startActivity(Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        finish()
                    },
                    onContinue = { finish() }
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// DOOM SCROLL SCREEN  (signature unchanged)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun DoomScrollScreen(
    packageName:  String,
    onTakeABreak: () -> Unit,
    onContinue:   () -> Unit
) {
    // ── Infinite animations ───────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "doomScroll")

    // Slow "breathing" scale for the orb — calm, meditative feel
    val breathScale by infiniteTransition.animateFloat(
        initialValue  = 0.85f,
        targetValue   = 1.15f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val breathAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.50f,
        animationSpec = infiniteRepeatable(
            animation  = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathAlpha"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "ring"
    )

    val ringRevRotation by infiniteTransition.animateFloat(
        initialValue  = 360f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing)
        ),
        label = "ringRev"
    )

    // ── Entry animations ──────────────────────────────────────────────────────
    val entryAlpha = remember { Animatable(0f) }
    val entrySlide = remember { Animatable(60f) }
    LaunchedEffect(Unit) {
        entryAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        entrySlide.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0E17),
                        Color(0xFF13141F),
                        Color(0xFF0E1320)   // subtle blue-tint at bottom — calm, not danger
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Ambient orbs — CYAN theme (matches DoomScrollingScreen) ──────────

        // Central breathing glow
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.Center)
                .blur(150.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SecondaryNeon.copy(alpha = breathAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Top-right purple accent
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleAccent.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Bottom-left green accent — gentle hope signal
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .blur(90.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryNeon.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // ── Main content column ───────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Breathing icon ring ───────────────────────────────────────────
            Box(
                modifier         = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ambient breathing glow — scales with breathScale
                Box(
                    modifier = Modifier
                        .size((180 * breathScale).dp)
                        .blur(30.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SecondaryNeon.copy(alpha = breathAlpha * 0.55f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                // Outer slowly-rotating arc — cyan, very gentle
                Canvas(modifier = Modifier.size(180.dp)) {
                    val sw      = 2.dp.toPx()
                    val inset   = sw / 2f
                    val arcSize = Size(size.width - sw, size.height - sw)
                    val topLeft = Offset(inset, inset)

                    // 4 soft arc segments — wider, more peaceful than the block overlay
                    listOf(0f, 90f, 180f, 270f).forEach { base ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(SecondaryNeon.copy(alpha = 0.70f), Color.Transparent)
                            ),
                            startAngle = base + ringRotation,
                            sweepAngle = 60f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = sw, cap = StrokeCap.Round)
                        )
                    }
                }

                // Inner reverse-rotating arc — purple, delicate
                Canvas(modifier = Modifier.size(148.dp)) {
                    val sw      = 1.5.dp.toPx()
                    val inset   = sw / 2f
                    val arcSize = Size(size.width - sw, size.height - sw)
                    val topLeft = Offset(inset, inset)

                    listOf(0f, 120f, 240f).forEach { base ->
                        drawArc(
                            brush      = Brush.sweepGradient(
                                listOf(PurpleAccent.copy(alpha = 0.55f), Color.Transparent)
                            ),
                            startAngle = base + ringRevRotation,
                            sweepAngle = 50f,
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(width = sw, cap = StrokeCap.Round)
                        )
                    }
                }

                // Center circle with breathing icon
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SecondaryNeon.copy(alpha = 0.18f),
                                    Color(0xFF0E1320)
                                )
                            )
                        )
                        .border(1.dp, SecondaryNeon.copy(alpha = 0.30f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SelfImprovement,
                        contentDescription = null,
                        tint     = SecondaryNeon,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Headline ──────────────────────────────────────────────────────
            Text(
                text       = "Time for a break?",
                color      = TextLight,
                fontSize   = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── App name badge ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SecondaryNeon.copy(alpha = 0.08f))
                    .border(1.dp, SecondaryNeon.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text       = packageName,
                    color      = SecondaryNeon,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Body text ─────────────────────────────────────────────────────
            Text(
                text      = "You've been scrolling for a while.\nTime for a deep breath and a screen break.",
                color     = TextMuted,
                fontSize  = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier  = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Reminder note ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SecondaryNeon.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Rounded.Air,
                        contentDescription = null,
                        tint     = SecondaryNeon.copy(alpha = 0.55f),
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text      = "If you continue, you'll be reminded again\nafter the same interval.",
                        color     = SecondaryNeon.copy(alpha = 0.60f),
                        fontSize  = 12.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            // ── Primary CTA — Take a Break ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(SecondaryNeon, PrimaryNeon.copy(alpha = 0.80f))
                        )
                    )
                    .clickable { onTakeABreak() },
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
                        text       = "Take a Break",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Secondary CTA — Keep Scrolling ────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceVariant)
                    .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint     = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text       = "Keep Scrolling",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color      = TextGray
                    )
                }
            }
        }
    }
}