package com.electricbiro.runningmetronome.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.data.model.Preset
import com.electricbiro.runningmetronome.data.model.RunningLevel
import com.electricbiro.runningmetronome.data.repository.PersistedSettings
import com.electricbiro.runningmetronome.data.repository.SettingsRepository
import com.electricbiro.runningmetronome.service.MetronomeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MetronomeUiState(
    val isPlaying: Boolean = false,
    val bpm: Int = 175,
    val volume: Int = 75,
    val sound: MetronomeSoundEnum = MetronomeSoundEnum.CLASSIC,
    val audioUsageType: AudioUsageType = AudioUsageType.MEDIA,
    val presets: List<Preset> = RunningLevel.CASUAL.presets,
    val activePresetId: String? = "race",
)

@HiltViewModel
class MetronomeViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetronomeUiState())
    val uiState: StateFlow<MetronomeUiState> = _uiState.asStateFlow()

    private var service: MetronomeService? = null
    private var pendingSettings: PersistedSettings? = null
    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val settings = repository.settings.first()
            pendingSettings = settings
            _uiState.update { it.copy(presets = settings.runningLevel.presets) }
            service?.let { applyPersistedSettings(it, settings) }
        }
    }

    fun bindService(service: MetronomeService) {
        this.service = service
        val persisted = pendingSettings
        if (persisted != null) {
            applyPersistedSettings(service, persisted)
        } else {
            _uiState.update { state ->
                state.copy(
                    isPlaying = service.isPlaying.value,
                    bpm = service.bpm.value,
                    volume = service.volume.value,
                    sound = service.sound.value,
                    audioUsageType = service.audioUsageType.value,
                )
            }
        }
    }

    private fun applyPersistedSettings(service: MetronomeService, settings: PersistedSettings) {
        service.setBpm(settings.bpm)
        service.setVolume(settings.volume)
        service.setAudioUsageType(settings.audioUsageType)
        val presets = settings.runningLevel.presets
        val activeId = presets.find { it.bpm == settings.bpm }?.id
        _uiState.update { state ->
            state.copy(
                isPlaying = service.isPlaying.value,
                bpm = settings.bpm,
                volume = settings.volume,
                sound = service.sound.value,
                audioUsageType = settings.audioUsageType,
                presets = presets,
                activePresetId = activeId,
            )
        }
    }

    fun refreshPresets() {
        viewModelScope.launch {
            val settings = repository.settings.first()
            val presets = settings.runningLevel.presets
            val activeId = presets.find { it.bpm == _uiState.value.bpm }?.id
            _uiState.update { it.copy(presets = presets, activePresetId = activeId) }
        }
    }

    fun unbindService() {
        service = null
    }

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

    fun setBpm(bpm: Float) {
        val bpmInt = bpm.toInt()
        service?.setBpm(bpmInt)
        val activeId = _uiState.value.presets.find { it.bpm == bpmInt }?.id
        _uiState.update { it.copy(bpm = bpmInt, activePresetId = activeId) }
        scheduleSave()
    }

    fun setPreset(preset: Preset) {
        service?.setBpm(preset.bpm)
        _uiState.update { it.copy(bpm = preset.bpm, activePresetId = preset.id) }
        scheduleSave()
    }

    fun setVolume(volume: Float) {
        val volumeInt = volume.toInt()
        service?.setVolume(volumeInt)
        _uiState.update { it.copy(volume = volumeInt) }
        scheduleSave()
    }

    fun setSound(sound: MetronomeSoundEnum) {
        service?.setSound(sound)
        _uiState.update { it.copy(sound = sound) }
        scheduleSave()
    }

    fun setAudioUsageType(usageType: AudioUsageType) {
        service?.setAudioUsageType(usageType)
        _uiState.update { it.copy(audioUsageType = usageType) }
        scheduleSave()
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500)
            val state = _uiState.value
            repository.save(
                PersistedSettings(
                    bpm = state.bpm,
                    volume = state.volume,
                    audioUsageType = state.audioUsageType,
                )
            )
        }
    }
}
