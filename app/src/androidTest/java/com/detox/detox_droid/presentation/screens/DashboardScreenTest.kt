package com.detox.detox_droid.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.detox.detox_droid.domain.models.AppUsage
import com.detox.detox_droid.ui.theme.Detox_DroidTheme
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenTimeDonutCard_shows_total_time_and_apps() {
        val mockApps = listOf(
            AppUsage("com.example.youtube", "YouTube", 3600000L, 0L), // 1 hour
            AppUsage("com.example.insta", "Instagram", 1800000L, 0L)  // 30 mins
        )
        val totalMs = 5400000L // 1.5 hours

        composeTestRule.setContent {
            Detox_DroidTheme {
                ScreenTimeDonutCard(
                    totalScreenTimeMs = totalMs,
                    apps = mockApps
                )
            }
        }

        // Verify that the apps are listed in the legend
        composeTestRule.onNodeWithText("YouTube").assertIsDisplayed()
        composeTestRule.onNodeWithText("Instagram").assertIsDisplayed()
        
        // Assert total time converts correctly (1h 30m)
        composeTestRule.onNodeWithText("1h 30m").assertIsDisplayed()
    }

    @Test
    fun detoxStatusCard_shows_active_state_correctly() {
        composeTestRule.setContent {
            Detox_DroidTheme {
                DetoxStatusCard(isDetoxActive = true)
            }
        }

        composeTestRule.onNodeWithText("Active & Enforced").assertIsDisplayed()
    }
    
    @Test
    fun detoxStatusCard_shows_inactive_state_correctly() {
        composeTestRule.setContent {
            Detox_DroidTheme {
                DetoxStatusCard(isDetoxActive = false)
            }
        }

        composeTestRule.onNodeWithText("Not running right now").assertIsDisplayed()
    }
}
