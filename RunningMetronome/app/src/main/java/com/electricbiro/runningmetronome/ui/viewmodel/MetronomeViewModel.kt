package com.electricbiro.runningmetronome.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.service.MetronomeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * UI state for the metronome screen
 */
data class MetronomeUiState(
    val isPlaying: Boolean = false,
    val bpm: Int = 175,
    val volume: Int = 75,
    val sound: MetronomeSoundEnum = MetronomeSoundEnum.CLASSIC,
    val audioUsageType: AudioUsageType = AudioUsageType.MEDIA
)

/**
 * ViewModel for managing metronome playback and settings.
 * Handles all business logic and exposes UI state via StateFlow.
 * Controls the MetronomeService for background playback.
 */
@HiltViewModel
class MetronomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MetronomeUiState())
    val uiState: StateFlow<MetronomeUiState> = _uiState.asStateFlow()

    private var service: MetronomeService? = null

    /**
     * Bind the service to the ViewModel
     */
    fun bindService(service: MetronomeService) {
        this.service = service
        // Initialize service with current UI state
        service.setBpm(_uiState.value.bpm)
        service.setVolume(_uiState.value.volume)
        service.setSound(_uiState.value.sound)
    }

    /**
     * Unbind the service from the ViewModel
     */
    fun unbindService() {
        service = null
    }

    /**
     * Toggle play/pause state
     */
    fun togglePlayPause() {
        service?.let {
            if (_uiState.value.isPlaying) {
                it.pause()
                _uiState.update { state -> state.copy(isPlaying = false) }
            } else {
                it.play()
                _uiState.update { state -> state.copy(isPlaying = true) }
            }
        }
    }

    /**
     * Update BPM setting
     */
    fun setBpm(bpm: Float) {
        val bpmInt = bpm.toInt()
        service?.setBpm(bpmInt)
        _uiState.update { it.copy(bpm = bpmInt) }
    }

    /**
     * Update volume setting
     */
    fun setVolume(volume: Float) {
        val volumeInt = volume.toInt()
        service?.setVolume(volumeInt)
        _uiState.update { it.copy(volume = volumeInt) }
    }

    /**
     * Change the metronome sound
     */
    fun setSound(sound: MetronomeSoundEnum) {
        service?.setSound(sound)
        _uiState.update { it.copy(sound = sound) }
    }

    /**
     * Change the audio usage type
     */
    fun setAudioUsageType(usageType: AudioUsageType) {
        service?.setAudioUsageType(usageType)
        _uiState.update { it.copy(audioUsageType = usageType) }
    }
}
