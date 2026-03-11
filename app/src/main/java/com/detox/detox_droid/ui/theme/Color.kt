package com.detox.detox_droid.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Core Palette (Dracula-inspired, elevated) ─────────────────────────────────
val PrimaryNeon    = Color(0xFF50FA7B)   // signature green
val SecondaryNeon  = Color(0xFF8BE9FD)   // cyan accent
val PurpleAccent   = Color(0xFFBD93F9)   // soft purple
val PinkAccent     = Color(0xFFFF79C6)   // vivid pink
val OrangeAccent   = Color(0xFFFFB86C)   // warm orange

// ── Semantic colors ────────────────────────────────────────────────────────────
val ErrorRed       = Color(0xFFFF5555)
val WarningYellow  = Color(0xFFF1FA8C)
val SuccessGreen   = Color(0xFF50FA7B)

// ── Background / Surface spectrum ─────────────────────────────────────────────
val BackgroundDark    = Color(0xFF13141F)   // deepest — screen bg
val SurfaceDark       = Color(0xFF1E2030)   // card base
val SurfaceVariant    = Color(0xFF252840)   // slightly lighter card
val SurfaceHighlight  = Color(0xFF2E3154)   // hover / selected

// ── Text ───────────────────────────────────────────────────────────────────────
val TextLight  = Color(0xFFF8F8F2)    // primary text
val TextMuted  = Color(0xFFADB4D5)    // secondary/hint text
val TextGray   = Color(0xFF6272A4)    // disabled / placeholder

// ── Legacy aliases kept for compatibility ─────────────────────────────────────
val Purple80      = Color(0xFFD0BCFF)
val PurpleGrey80  = Color(0xFFCCC2DC)
val Pink80        = Color(0xFFEFB8C8)
val Purple40      = Color(0xFF6650a4)
val PurpleGrey40  = Color(0xFF625b71)
val Pink40        = Color(0xFF7D5260)


// ── Neon Glow variants (for BoxShadow / Canvas glow effects) ──────────────────
val PrimaryNeonDim     = Color(0x3350FA7B)   // 20% opacity — subtle ambient glow
val PrimaryNeonGlow    = Color(0x6650FA7B)   // 40% opacity — hover glow
val PrimaryNeonFlare   = Color(0x9950FA7B)   // 60% opacity — pressed/active glow
val SecondaryNeonDim   = Color(0x338BE9FD)
val SecondaryNeonGlow  = Color(0x668BE9FD)
val PurpleAccentDim    = Color(0x33BD93F9)
val PurpleAccentGlow   = Color(0x66BD93F9)
val PinkAccentGlow     = Color(0x66FF79C6)
val OrangeAccentGlow   = Color(0x66FFB86C)
val ErrorRedGlow       = Color(0x66FF5555)

// ── Glassmorphism surfaces ─────────────────────────────────────────────────────
val GlassSurface       = Color(0x1AFFFFFF)   // white 10% — frosted glass base
val GlassSurfaceMid    = Color(0x26FFFFFF)   // white 15%
val GlassBorder        = Color(0x33FFFFFF)   // white 20% — glass card border
val GlassBorderBright  = Color(0x55FFFFFF)   // white 33% — highlighted glass border

// ── Deep background layers (for layered parallax/depth) ───────────────────────
val BackgroundDeepest  = Color(0xFF0D0E17)   // even deeper than BackgroundDark
val BackgroundMid      = Color(0xFF181929)   // between bg and surface
val SurfaceElevated    = Color(0xFF323660)   // top-elevation card / modal
val SurfaceOverlay     = Color(0xCC1E2030)   // 80% opaque overlay (dialogs)

// ── Divider & stroke ──────────────────────────────────────────────────────────
val DividerSubtle      = Color(0x1AADB4D5)   // 10% TextMuted
val DividerNormal      = Color(0x33ADB4D5)   // 20% TextMuted
val StrokeNeon         = Color(0x8050FA7B)   // 50% PrimaryNeon — neon border

// ── Status / badge colors ─────────────────────────────────────────────────────
val StatusActive       = Color(0xFF50FA7B)
val StatusWarning      = Color(0xFFFFB86C)
val StatusDanger       = Color(0xFFFF5555)
val StatusNeutral      = Color(0xFF6272A4)
val StatusInfo         = Color(0xFF8BE9FD)

// ── Chart / data visualization palette ───────────────────────────────────────
val ChartGreen         = Color(0xFF50FA7B)
val ChartCyan          = Color(0xFF8BE9FD)
val ChartPurple        = Color(0xFFBD93F9)
val ChartPink          = Color(0xFFFF79C6)
val ChartOrange        = Color(0xFFFFB86C)
val ChartYellow        = Color(0xFFF1FA8C)
val ChartRed           = Color(0xFFFF5555)

/** Full-screen background: deep navy → near-black with subtle blue tint */
val GradientBackground = Brush.verticalGradient(
    colors = listOf(BackgroundDeepest, BackgroundDark, BackgroundMid)
)

/** Hero/banner gradient: neon green → cyan — for timers and primary CTAs */
val GradientPrimaryCyan = Brush.horizontalGradient(
    colors = listOf(PrimaryNeon, SecondaryNeon)
)

/** Accent gradient: purple → pink — for secondary highlights */
val GradientPurplePink = Brush.horizontalGradient(
    colors = listOf(PurpleAccent, PinkAccent)
)

/** Warm gradient: orange → pink — for streaks, rewards */
val GradientWarm = Brush.horizontalGradient(
    colors = listOf(OrangeAccent, PinkAccent)
)

/** Danger gradient: red → orange — for warnings, block states */
val GradientDanger = Brush.horizontalGradient(
    colors = listOf(ErrorRed, OrangeAccent)
)

/** Card surface gradient: subtle depth for elevated cards */
val GradientCardSurface = Brush.verticalGradient(
    colors = listOf(SurfaceVariant, SurfaceDark)
)

/** Neon card shimmer: used as card overlay for a glowing-edge effect */
val GradientNeonShimmer = Brush.horizontalGradient(
    colors = listOf(PrimaryNeonDim, Color.Transparent, SecondaryNeonDim)
)

/** Progress / timer ring fill gradient */
val GradientProgressArc = Brush.sweepGradient(
    colors = listOf(SecondaryNeon, PrimaryNeon, PrimaryNeonDim)
)

/** Full rainbow chart gradient */
val GradientChart = Brush.horizontalGradient(
    colors = listOf(ChartCyan, ChartGreen, ChartYellow, ChartOrange, ChartPink, ChartPurple)
)

/** Radial spotlight — center screen hero glow */
val GradientSpotlight = Brush.radialGradient(
    colors = listOf(PrimaryNeonDim, Color.Transparent),
    radius = 600f
)