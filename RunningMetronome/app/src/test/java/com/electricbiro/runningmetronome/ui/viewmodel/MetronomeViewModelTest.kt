package com.electricbiro.runningmetronome.ui.viewmodel

import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.service.MetronomeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for MetronomeViewModel.
 * Tests StateFlow emissions, service interactions, and state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MetronomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: MetronomeViewModel
    private lateinit var mockService: MetronomeService

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockService = mock()
        viewModel = MetronomeViewModel()
        viewModel.bindService(mockService)
        // Clear invocations from binding initialization
        clearInvocations(mockService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initial State Tests

    @Test
    fun `initial UI state should have correct defaults`() {
        val state = viewModel.uiState.value

        assertFalse(state.isPlaying)
        assertEquals(175, state.bpm)
        assertEquals(75, state.volume)
        assertEquals(MetronomeSoundEnum.CLASSIC, state.sound)
        assertEquals(AudioUsageType.MEDIA, state.audioUsageType)
    }

    // BPM Tests

    @Test
    fun `setBpm should update UI state`() {
        viewModel.setBpm(180f)

        assertEquals(180, viewModel.uiState.value.bpm)
    }

    @Test
    fun `setBpm should call service setBpm`() {
        viewModel.setBpm(180f)

        verify(mockService).setBpm(180)
    }

    @Test
    fun `setBpm should convert float to int correctly`() {
        viewModel.setBpm(175.7f)

        assertEquals(175, viewModel.uiState.value.bpm)
        verify(mockService).setBpm(175)
    }

    // Volume Tests

    @Test
    fun `setVolume should update UI state`() {
        viewModel.setVolume(50f)

        assertEquals(50, viewModel.uiState.value.volume)
    }

    @Test
    fun `setVolume should call service setVolume`() {
        viewModel.setVolume(50f)

        verify(mockService).setVolume(50)
    }

    @Test
    fun `setVolume should convert float to int correctly`() {
        viewModel.setVolume(85.3f)

        assertEquals(85, viewModel.uiState.value.volume)
        verify(mockService).setVolume(85)
    }

    // Play/Pause Tests

    @Test
    fun `togglePlayPause should start playing when paused`() {
        // Initial state is paused
        assertFalse(viewModel.uiState.value.isPlaying)

        viewModel.togglePlayPause()

        assertTrue(viewModel.uiState.value.isPlaying)
        verify(mockService).play()
    }

    @Test
    fun `togglePlayPause should pause when playing`() {
        // Start playing first
        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isPlaying)

        // Now toggle to pause
        viewModel.togglePlayPause()

        assertFalse(viewModel.uiState.value.isPlaying)
        verify(mockService).pause()
    }

    @Test
    fun `togglePlayPause should toggle state multiple times`() {
        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isPlaying)

        viewModel.togglePlayPause()
        assertFalse(viewModel.uiState.value.isPlaying)

        viewModel.togglePlayPause()
        assertTrue(viewModel.uiState.value.isPlaying)
    }

    // Audio Usage Type Tests

    @Test
    fun `setAudioUsageType should update UI state`() {
        viewModel.setAudioUsageType(AudioUsageType.NOTIFICATION)

        assertEquals(AudioUsageType.NOTIFICATION, viewModel.uiState.value.audioUsageType)
    }

    @Test
    fun `setAudioUsageType should call service setAudioUsageType`() {
        viewModel.setAudioUsageType(AudioUsageType.NOTIFICATION)

        verify(mockService).setAudioUsageType(AudioUsageType.NOTIFICATION)
    }

    // Service Binding Tests

    @Test
    fun `bindService should initialize service with current state`() {
        val newViewModel = MetronomeViewModel()
        val newMockService = mock<MetronomeService>()

        newViewModel.bindService(newMockService)

        verify(newMockService).setBpm(175) // default BPM
        verify(newMockService).setVolume(75) // default volume
        verify(newMockService).setSound(MetronomeSoundEnum.CLASSIC) // default sound
    }

    @Test
    fun `unbindService should clear service reference`() {
        viewModel.unbindService()

        // After unbinding, service calls should not throw exceptions
        // but also should not affect service (service is null)
        viewModel.setBpm(200f)

        assertEquals(200, viewModel.uiState.value.bpm)
        // Service was unbound, so no new call should be made
    }

    @Test
    fun `togglePlayPause should not crash when service is null`() {
        viewModel.unbindService()

        // Should not crash, but state should not change either
        viewModel.togglePlayPause()

        // State should remain false since service is null
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    // State Consistency Tests

    @Test
    fun `multiple state changes should all be reflected in UI state`() {
        viewModel.setBpm(160f)
        viewModel.setVolume(90f)

        val state = viewModel.uiState.value

        assertEquals(160, state.bpm)
        assertEquals(90, state.volume)
    }

    @Test
    fun `rapid BPM changes should update state correctly`() {
        viewModel.setBpm(160f)
        viewModel.setBpm(170f)
        viewModel.setBpm(180f)
        viewModel.setBpm(190f)

        assertEquals(190, viewModel.uiState.value.bpm)
    }
}
