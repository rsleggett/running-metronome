package com.electricbiro.runningmetronome.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.electricbiro.runningmetronome.data.model.RunningLevel
import com.electricbiro.runningmetronome.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class OnboardingStep { WELCOME, LEVEL_SELECT, PERMISSION, APP }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val selectedLevel: RunningLevel? = null,
    val tourStep: Int = 0,   // 0..2 active tour, -1 = tour done
    val isComplete: Boolean = false,
    val isLoading: Boolean = true,
    val isReset: Boolean = false, // true when navigating from settings, suppresses tour
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = repository.settings.first()
            if (settings.onboardingComplete) {
                // tourStep = -1 so the coachmark never shows on a returning open
                _state.update { it.copy(isComplete = true, isLoading = false, tourStep = -1) }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun goToLevelSelect() = _state.update { it.copy(step = OnboardingStep.LEVEL_SELECT) }

    fun goBack() = _state.update {
        when (it.step) {
            OnboardingStep.LEVEL_SELECT -> it.copy(step = OnboardingStep.WELCOME)
            OnboardingStep.PERMISSION   -> it.copy(step = OnboardingStep.LEVEL_SELECT)
            else -> it
        }
    }

    fun selectLevel(level: RunningLevel) = _state.update { it.copy(selectedLevel = level) }

    fun goToPermission() = _state.update { it.copy(step = OnboardingStep.PERMISSION) }

    fun skip() {
        viewModelScope.launch { withContext(NonCancellable) { repository.completeOnboarding(RunningLevel.CASUAL) } }
        _state.update { it.copy(isComplete = true, tourStep = -1) }
    }

    fun finishPermissionStep() {
        val level = _state.value.selectedLevel ?: RunningLevel.CASUAL
        val skipTour = _state.value.isReset
        viewModelScope.launch {
            withContext(NonCancellable) { repository.completeOnboarding(level) }
            // State update happens only after the DataStore write completes, so
            // refreshPresets() in AppRoot will always read the newly-saved level.
            _state.update { it.copy(step = OnboardingStep.APP, tourStep = if (skipTour) -1 else 0, isReset = false) }
        }
    }

    fun nextTourStep() {
        _state.update {
            val next = it.tourStep + 1
            if (next > 2) it.copy(tourStep = -1, isComplete = true)
            else it.copy(tourStep = next)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch { repository.resetOnboarding() }
        _state.update {
            it.copy(
                step = OnboardingStep.LEVEL_SELECT,
                isComplete = false,
                selectedLevel = null,
                tourStep = 0,
                isReset = true,
            )
        }
    }
}
