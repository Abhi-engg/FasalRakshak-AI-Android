package com.agrovision.ai.presentation.home

import com.agrovision.ai.core.network.NetworkMonitor
import com.agrovision.ai.core.network.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeNetworkFlow = MutableStateFlow(NetworkStatus.OFFLINE)

    private val fakeNetworkMonitor = object : NetworkMonitor {
        override val networkStatus: Flow<NetworkStatus> = fakeNetworkFlow
        override val isOnline: Boolean get() = fakeNetworkFlow.value == NetworkStatus.ONLINE
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_defaultsToOfflineMode() = runTest {
        val viewModel = HomeViewModel(fakeNetworkMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(NetworkStatus.OFFLINE, viewModel.uiState.value.networkStatus)
        assertEquals(true, viewModel.uiState.value.isModelReady)
    }

    @Test
    fun networkStatusUpdate_updatesUiStateToOnline() = runTest {
        val viewModel = HomeViewModel(fakeNetworkMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        fakeNetworkFlow.value = NetworkStatus.ONLINE
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(NetworkStatus.ONLINE, viewModel.uiState.value.networkStatus)
    }
}
