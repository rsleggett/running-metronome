package com.electricbiro.runningmetronome.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class PersistedSettings(
    val bpm: Int = 175,
    val volume: Int = 75,
    val audioUsageType: AudioUsageType = AudioUsageType.MEDIA,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_BPM = intPreferencesKey("bpm")
        private val KEY_VOLUME = intPreferencesKey("volume")
        private val KEY_AUDIO_USAGE_TYPE = stringPreferencesKey("audio_usage_type")
    }

    val settings: Flow<PersistedSettings> = dataStore.data.map { prefs ->
        PersistedSettings(
            bpm = prefs[KEY_BPM] ?: 175,
            volume = prefs[KEY_VOLUME] ?: 75,
            audioUsageType = prefs[KEY_AUDIO_USAGE_TYPE]
                ?.let { runCatching { AudioUsageType.valueOf(it) }.getOrNull() }
                ?: AudioUsageType.MEDIA,
        )
    }

    suspend fun save(settings: PersistedSettings) {
        dataStore.edit { prefs ->
            prefs[KEY_BPM] = settings.bpm
            prefs[KEY_VOLUME] = settings.volume
            prefs[KEY_AUDIO_USAGE_TYPE] = settings.audioUsageType.name
        }
    }
}
