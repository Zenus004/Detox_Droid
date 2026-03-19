package com.detox.detox_droid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.detox.detox_droid.presentation.navigation.AppNavGraph
import com.detox.detox_droid.presentation.navigation.NavRoutes
import com.detox.detox_droid.ui.theme.BackgroundDark
import com.detox.detox_droid.ui.theme.BackgroundDeepest
import com.detox.detox_droid.ui.theme.Detox_DroidTheme
import com.detox.detox_droid.ui.theme.ErrorRed
import com.detox.detox_droid.ui.theme.GlassBorder
import com.detox.detox_droid.ui.theme.PrimaryNeon
import com.detox.detox_droid.ui.theme.PurpleAccent
import com.detox.detox_droid.ui.theme.SecondaryNeon
import com.detox.detox_droid.ui.theme.SurfaceDark
import com.detox.detox_droid.ui.theme.SurfaceVariant
import com.detox.detox_droid.ui.theme.TextGray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Detox_DroidTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier       = Modifier.fillMaxSize(),
                    containerColor = BackgroundDark,
                    bottomBar      = { ModernBottomNav(navController) }
                ) { innerPadding ->
                    AppNavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}

data class BottomNavItem(
    val name:          String,
    val route:         String,
    val selectedIcon:  ImageVector,
    val unselectedIcon:ImageVector,
    val accentColor:   Color
)
@Composable
fun ModernBottomNav(navController: androidx.navigation.NavHostController) {
    val items = listOf(
        BottomNavItem(
            name          = "Home",
            route         = NavRoutes.DASHBOARD,
            selectedIcon  = Icons.Rounded.Dashboard,
            unselectedIcon= Icons.Outlined.Home,
            accentColor   = PrimaryNeon
        ),
        BottomNavItem(
            name          = "Focus",
            route         = NavRoutes.FOCUS_MODE,
            selectedIcon  = Icons.Rounded.Timer,
            unselectedIcon= Icons.Rounded.Timer,
            accentColor   = PrimaryNeon
        ),
        BottomNavItem(
            name          = "Schedule",
            route         = NavRoutes.SCHEDULES,
            selectedIcon  = Icons.Rounded.CalendarMonth,
            unselectedIcon= Icons.Outlined.CalendarMonth,
            accentColor   = PurpleAccent
        ),
        BottomNavItem(
            name          = "Apps",
            route         = NavRoutes.APP_BLOCKING_CONFIG,
            selectedIcon  = Icons.Rounded.Block,
            unselectedIcon= Icons.Outlined.Lock,
            accentColor   = ErrorRed
        ),
        BottomNavItem(
            name          = "Scroll",
            route         = NavRoutes.DOOM_SCROLLING,
            selectedIcon  = Icons.Rounded.RemoveRedEye,
            unselectedIcon= Icons.Rounded.RemoveRedEye,
            accentColor   = SecondaryNeon
        ),
        BottomNavItem(
            name          = "Settings",
            route         = NavRoutes.SETTINGS,
            selectedIcon  = Icons.Rounded.Settings,
            unselectedIcon= Icons.Outlined.Settings,
            accentColor   = SecondaryNeon
        ),
    )

    val navBackStackEntry  by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottom         = currentDestination?.route in items.map { it.route }
    if (!showBottom) return

    val currentAccent = items.firstOrNull {
        currentDestination?.hierarchy?.any { d -> d.route == it.route } == true
    }?.accentColor ?: PrimaryNeon

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundDeepest)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .blur(28.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            currentAccent.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(38.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .shadow(
                    elevation    = 20.dp,
                    shape        = RoundedCornerShape(38.dp),
                    ambientColor = Color.Black.copy(alpha = 0.8f),
                    spotColor    = Color.Black.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(38.dp))
                .background(
                    Brush.verticalGradient(listOf(SurfaceVariant, SurfaceDark))
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            currentAccent.copy(alpha = 0.30f),
                            GlassBorder,
                            currentAccent.copy(alpha = 0.15f)
                        )
                    ),
                    RoundedCornerShape(38.dp)
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                BottomNavTabItem(
                    item     = item,
                    selected = selected,
                    onClick  = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTabItem(
    item:     BottomNavItem,
    selected: Boolean,
    onClick:  () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue   = if (selected) item.accentColor else TextGray.copy(alpha = 0.50f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "iconColor"
    )

    val pillWidth by animateDpAsState(
        targetValue   = if (selected) 50.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "pillWidth"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Neon glow halo behind active icon
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 32.dp)
                        .clip(CircleShape)
                        .background(item.accentColor.copy(alpha = 0.30f))
                        .blur(12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(width = pillWidth, height = 32.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) item.accentColor.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
            )

            Icon(
                imageVector        = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.name,
                tint               = iconColor,
                modifier           = Modifier.size(22.dp)
            )
        }

        androidx.compose.animation.AnimatedVisibility(visible = selected) {
            Text(
                text       = item.name,
                color      = iconColor,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }
    }
}