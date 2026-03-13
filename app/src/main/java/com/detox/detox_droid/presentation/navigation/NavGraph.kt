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
            DashboardScreen()
        }
        composable(NavRoutes.FOCUS_MODE) {
            FocusModeScreen()
        }
        composable(NavRoutes.SCHEDULES) {
            ScheduleScreen()
        }
        composable(NavRoutes.APP_BLOCKING_CONFIG) {
            AppBlockingConfigScreen()
        }
        composable(NavRoutes.DOOM_SCROLLING) {
            DoomScrollingScreen()
        }
        composable(NavRoutes.STATISTICS) {
            StatisticsScreen()
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen()
        }
        composable(NavRoutes.SPLASH_OR_PERMISSIONS) {
            SplashScreen(navController)
        }
    }
}
