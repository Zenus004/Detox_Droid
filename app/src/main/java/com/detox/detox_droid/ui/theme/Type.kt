package com.detox.detox_droid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Full Material3 typography scale with harmonious sizing + weight ramp.
 * Using system default font family (clean sans-serif on all Android versions).
 *
 * ENHANCEMENT NOTES (originals preserved):
 * - Letter-spacing tightened on large display styles for more modern feel
 * - lineHeight ratios improved for breathing room on body text
 * - Extra TextStyle aliases added below for quick access (timer, badge, mono)
 */
val Typography = Typography(
    // Display — used for timers and hero numbers
    displayLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold,
        fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,
        fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp),
    displaySmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp),

    // Headlines
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp),

    // Titles
    titleLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),

    // Body
    bodyLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),

    // Labels
    labelLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)

/** Hero timer display — massive, tight, monospaced feel */
val TimerTextStyle = TextStyle(
    fontFamily    = FontFamily.Monospace,
    fontWeight    = FontWeight.ExtraBold,
    fontSize      = 64.sp,
    lineHeight    = 72.sp,
    letterSpacing = (-2).sp
)

/** Smaller timer / countdown secondary */
val TimerSmallTextStyle = TextStyle(
    fontFamily    = FontFamily.Monospace,
    fontWeight    = FontWeight.Bold,
    fontSize      = 40.sp,
    lineHeight    = 48.sp,
    letterSpacing = (-1).sp
)

/** Badge / pill label — tight, all-caps feel */
val BadgeTextStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.Bold,
    fontSize      = 10.sp,
    lineHeight    = 14.sp,
    letterSpacing = 1.2.sp
)

/** Stat number on cards (e.g. "3h 24m") */
val StatNumberTextStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.ExtraBold,
    fontSize      = 28.sp,
    lineHeight    = 34.sp,
    letterSpacing = (-0.5).sp
)

/** Stat label below number (e.g. "Screen Time Today") */
val StatLabelTextStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.Medium,
    fontSize      = 11.sp,
    lineHeight    = 15.sp,
    letterSpacing = 0.8.sp
)

/** Overline / section header — spaced caps */
val OverlineTextStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 11.sp,
    lineHeight    = 16.sp,
    letterSpacing = 1.5.sp
)

/** Code / app package name monospace */
val MonoTextStyle = TextStyle(
    fontFamily    = FontFamily.Monospace,
    fontWeight    = FontWeight.Normal,
    fontSize      = 12.sp,
    lineHeight    = 18.sp,
    letterSpacing = 0.sp
)

/** Motivational quote / insight text — slightly italic feel via weight */
val QuoteTextStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.Light,
    fontSize      = 15.sp,
    lineHeight    = 24.sp,
    letterSpacing = 0.3.sp
)

/** Streak / reward headline — punchy */
val StreakHeadlineStyle = TextStyle(
    fontFamily    = FontFamily.Default,
    fontWeight    = FontWeight.ExtraBold,
    fontSize      = 42.sp,
    lineHeight    = 48.sp,
    letterSpacing = (-1).sp
)