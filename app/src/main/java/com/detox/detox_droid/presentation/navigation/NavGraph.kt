package com.detox.detox_droid.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.detox.detox_droid.presentation.screens.AppBlockingConfigScreen
import com.detox.detox_droid.presentation.screens.DashboardScreen
import com.detox.detox_droid.presentation.screens.DoomScrollingScreen
import com.detox.detox_droid.presentation.screens.FocusModeScreen
import com.detox.detox_droid.presentation.screens.ScheduleScreen
import com.detox.detox_droid.presentation.screens.SettingsScreen
import com.detox.detox_droid.presentation.screens.SplashScreen
import com.detox.detox_droid.presentation.screens.StatisticsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.DASHBOARD,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(navController)
        }
        composable(NavRoutes.FOCUS_MODE) {
            FocusModeScreen(navController)
        }
        composable(NavRoutes.SCHEDULES) {
            ScheduleScreen(navController)
        }
        composable(NavRoutes.APP_BLOCKING_CONFIG) {
            AppBlockingConfigScreen(navController)
        }
        composable(NavRoutes.DOOM_SCROLLING) {
            DoomScrollingScreen(navController)
        }
        composable(NavRoutes.STATISTICS) {
            StatisticsScreen(navController)
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(navController)
        }
        composable(NavRoutes.SPLASH_OR_PERMISSIONS) {
            SplashScreen(navController)
        }
    }
}
