package com.detox.detox_droid.ui.theme

import android.app.Activity
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DetoxColorScheme = darkColorScheme(
    primary          = PrimaryNeon,
    onPrimary        = BackgroundDark,
    primaryContainer = SurfaceHighlight,
    onPrimaryContainer = PrimaryNeon,

    secondary        = SecondaryNeon,
    onSecondary      = BackgroundDark,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = SecondaryNeon,

    tertiary         = PurpleAccent,
    onTertiary       = BackgroundDark,

    background       = BackgroundDark,
    onBackground     = TextLight,

    surface          = SurfaceDark,
    onSurface        = TextLight,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = TextMuted,

    error            = ErrorRed,
    onError          = BackgroundDark,
)

/**
 * Detox shape scale — modern rounded corners tuned to the dark neon aesthetic.
 * More generous radii than M3 defaults for a premium, "app-like" feel.
 */
val DetoxShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),    // chips, badges, small tags
    small      = RoundedCornerShape(10.dp),   // input fields, small buttons
    medium     = RoundedCornerShape(16.dp),   // cards, dialogs (M3 default = 12)
    large      = RoundedCornerShape(24.dp),   // bottom sheets, large cards
    extraLarge = RoundedCornerShape(32.dp),   // full-bleed panels, hero cards
)

// ── Convenience shape aliases ──────────────────────────────────────────────────
val ShapePill     = RoundedCornerShape(percent = 50)   // fully rounded pill
val ShapeCard     = RoundedCornerShape(16.dp)           // standard card
val ShapeCardLg   = RoundedCornerShape(24.dp)           // large card / sheet
val ShapeChip     = RoundedCornerShape(8.dp)            // filter chip
val ShapeButton   = RoundedCornerShape(14.dp)           // CTA button
val ShapeDialog   = RoundedCornerShape(28.dp)           // bottom sheet / modal

/** Consistent spacing scale — use instead of magic dp numbers */
object DetoxSpacing {
    val xxs: Dp = 2.dp
    val xs:  Dp = 4.dp
    val sm:  Dp = 8.dp
    val md:  Dp = 12.dp
    val lg:  Dp = 16.dp
    val xl:  Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val section: Dp = 40.dp
    val screen: Dp = 56.dp
}

/** Elevation levels for consistent shadow depth */
object DetoxElevation {
    val none:   Dp = 0.dp
    val low:    Dp = 2.dp
    val medium: Dp = 6.dp
    val high:   Dp = 12.dp
    val modal:  Dp = 24.dp
}


/** Easing curves tuned for the app's snappy-but-smooth feel */
object DetoxEasing {
    val FastOutSlowIn  = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)  // standard M3
    val EaseOutExpo    = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)      // snappy exits
    val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)     // smooth transitions
    val SpringLike     = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)  // slight overshoot
}

/** Pre-built tween specs for common animations */
object DetoxAnimSpec {
    fun <T> fast()    = tween<T>(durationMillis = 150, easing = DetoxEasing.EaseOutExpo)
    fun <T> normal()  = tween<T>(durationMillis = 300, easing = DetoxEasing.FastOutSlowIn)
    fun <T> slow()    = tween<T>(durationMillis = 500, easing = DetoxEasing.EaseInOutCubic)
    fun <T> spring()  = tween<T>(durationMillis = 400, easing = DetoxEasing.SpringLike)
}

/** Provides gradient brushes through the composition tree */
data class DetoxGradients(
    val background:    Brush = GradientBackground,
    val primaryCyan:   Brush = GradientPrimaryCyan,
    val purplePink:    Brush = GradientPurplePink,
    val warm:          Brush = GradientWarm,
    val danger:        Brush = GradientDanger,
    val cardSurface:   Brush = GradientCardSurface,
    val neonShimmer:   Brush = GradientNeonShimmer,
    val progressArc:   Brush = GradientProgressArc,
    val chart:         Brush = GradientChart,
    val spotlight:     Brush = GradientSpotlight,
)

val LocalDetoxGradients = staticCompositionLocalOf { DetoxGradients() }

/** Provides spacing tokens through the composition tree */
val LocalDetoxSpacing = staticCompositionLocalOf { DetoxSpacing }

@Composable
fun Detox_DroidTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars  = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalDetoxGradients provides DetoxGradients(),
        LocalDetoxSpacing    provides DetoxSpacing,
    ) {
        MaterialTheme(
            colorScheme = DetoxColorScheme,
            typography  = Typography,
            shapes      = DetoxShapes,
            content     = content,
        )
    }
}

/** Access gradients anywhere inside Detox_DroidTheme: `MaterialTheme.detoxGradients.primaryCyan` */
val MaterialTheme.detoxGradients: DetoxGradients
    @Composable get() = LocalDetoxGradients.current

/** Access spacing anywhere inside Detox_DroidTheme: `MaterialTheme.detoxSpacing.lg` */
val MaterialTheme.detoxSpacing: DetoxSpacing
    @Composable get() = LocalDetoxSpacing.current