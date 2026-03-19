package com.detox.detox_droid.presentation.viewmodels

import android.app.Application
import com.detox.detox_droid.data.services.UsageStatsHelper
import com.detox.detox_droid.domain.models.AppUsage
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import com.detox.detox_droid.domain.usecases.FetchDailyUsageUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private val fetchDailyUsageUseCase: FetchDailyUsageUseCase = mockk()
    private val usageStatsHelper: UsageStatsHelper = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val application: Application = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock settings repository flow default
        val activeFlow = MutableStateFlow(false)
        every { settingsRepository.isDetoxModeActive } returns activeFlow

        // By default, assume no permissions
        every { usageStatsHelper.hasUsageStatsPermission() } returns false

        // Mocking Settings.Secure and Settings.canDrawOverlays requires static mocks,
        // but for unit test simplicity we can just let context relaxed mode handle nulls and return false.
        mockkStatic(android.provider.Settings.Secure::class)
        every { android.provider.Settings.Secure.getString(any(), any()) } returns null
        
        mockkStatic(android.provider.Settings::class)
        every { android.provider.Settings.canDrawOverlays(any()) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is set correctly when no permissions granted`() = runTest {
        viewModel = DashboardViewModel(
            fetchDailyUsageUseCase,
            usageStatsHelper,
            settingsRepository,
            application
        )
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.hasUsagePermission)
        assertEquals(false, state.hasAccessibilityPermission)
        assertEquals(false, state.hasOverlayPermission)
    }

    @Test
    fun `fetchUsageData updates state with top 5 apps`() = runTest {
        // Arrange
        every { usageStatsHelper.hasUsageStatsPermission() } returns true
        val mockUsage = listOf(
            AppUsage("com.test1", "App 1", 1000L, 0L),
            AppUsage("com.test2", "App 2", 2000L, 0L)
        )
        coEvery { fetchDailyUsageUseCase() } returns mockUsage

        // Act
        viewModel = DashboardViewModel(
            fetchDailyUsageUseCase,
            usageStatsHelper,
            settingsRepository,
            application
        )
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(true, state.hasUsagePermission)
        assertEquals(3000L, state.totalScreenTimeMs)
        assertEquals(2, state.topUsedApps.size)
        assertEquals("App 1", state.topUsedApps[0].appName)
    }
}
