package com.electricbiro.runningmetronome.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.RunningLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class PersistedSettings(
    val bpm: Int = 175,
    val volume: Int = 75,
    val audioUsageType: AudioUsageType = AudioUsageType.MEDIA,
    val onboardingComplete: Boolean = false,
    val runningLevel: RunningLevel = RunningLevel.CASUAL,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_BPM = intPreferencesKey("bpm")
        private val KEY_VOLUME = intPreferencesKey("volume")
        private val KEY_AUDIO_USAGE_TYPE = stringPreferencesKey("audio_usage_type")
        private val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val KEY_RUNNING_LEVEL = stringPreferencesKey("running_level")
    }

    val settings: Flow<PersistedSettings> = dataStore.data.map { prefs ->
        PersistedSettings(
            bpm = prefs[KEY_BPM] ?: 175,
            volume = prefs[KEY_VOLUME] ?: 75,
            audioUsageType = prefs[KEY_AUDIO_USAGE_TYPE]
                ?.let { runCatching { AudioUsageType.valueOf(it) }.getOrNull() }
                ?: AudioUsageType.MEDIA,
            onboardingComplete = prefs[KEY_ONBOARDING_COMPLETE] ?: false,
            runningLevel = prefs[KEY_RUNNING_LEVEL]
                ?.let { RunningLevel.fromId(it) }
                ?: RunningLevel.CASUAL,
        )
    }

    suspend fun save(settings: PersistedSettings) {
        dataStore.edit { prefs ->
            prefs[KEY_BPM] = settings.bpm
            prefs[KEY_VOLUME] = settings.volume
            prefs[KEY_AUDIO_USAGE_TYPE] = settings.audioUsageType.name
            prefs[KEY_ONBOARDING_COMPLETE] = settings.onboardingComplete
            prefs[KEY_RUNNING_LEVEL] = settings.runningLevel.id
        }
    }

    suspend fun savePlaybackSettings(bpm: Int, volume: Int, audioUsageType: AudioUsageType) {
        dataStore.edit { prefs ->
            prefs[KEY_BPM] = bpm
            prefs[KEY_VOLUME] = volume
            prefs[KEY_AUDIO_USAGE_TYPE] = audioUsageType.name
        }
    }

    suspend fun completeOnboarding(level: RunningLevel) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETE] = true
            prefs[KEY_RUNNING_LEVEL] = level.id
            prefs[KEY_BPM] = level.presets[1].bpm
        }
    }

    suspend fun resetOnboarding() {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETE] = false
        }
    }
}
