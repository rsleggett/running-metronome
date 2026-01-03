package com.electricbiro.runningmetronome.service

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.electricbiro.runningmetronome.audio.MetronomeAudioPlayer
import com.electricbiro.runningmetronome.data.model.AccentPattern
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.DrumPattern
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.data.model.PlaybackMode
import com.electricbiro.runningmetronome.di.AppModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Instrumented tests for MetronomeService.
 * Uses Hilt for dependency injection with a mocked MetronomeAudioPlayer.
 * Tests service state management, foreground service behavior, and audio player delegation.
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
@RunWith(AndroidJUnit4::class)
class MetronomeServiceTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockAudioPlayer: MetronomeAudioPlayer

    private lateinit var service: MetronomeService

    @Module
    @InstallIn(SingletonComponent::class)
    object TestAppModule {
        @Provides
        @Singleton
        fun provideMetronomeAudioPlayer(): MetronomeAudioPlayer {
            return mock()
        }
    }

    @Before
    fun setup() {
        hiltRule.inject()
        service = MetronomeService()
        service.audioPlayer = mockAudioPlayer
    }

    // StateFlow Tests

    @Test
    fun `initial isPlaying state should be false`() = runTest {
        service.isPlaying.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `initial bpm state should be 175`() = runTest {
        service.bpm.test {
            assertEquals(175, awaitItem())
        }
    }

    @Test
    fun `initial volume state should be 75`() = runTest {
        service.volume.test {
            assertEquals(75, awaitItem())
        }
    }

    @Test
    fun `initial sound state should be CLASSIC`() = runTest {
        service.sound.test {
            assertEquals(MetronomeSoundEnum.CLASSIC, awaitItem())
        }
    }

    @Test
    fun `initial audioUsageType state should be MEDIA`() = runTest {
        service.audioUsageType.test {
            assertEquals(AudioUsageType.MEDIA, awaitItem())
        }
    }

    // Play/Pause Tests

    @Test
    fun `play should call audioPlayer play and update state`() = runTest {
        service.isPlaying.test {
            assertEquals(false, awaitItem())

            service.play()

            assertEquals(true, awaitItem())
            verify(mockAudioPlayer).play()
        }
    }

    @Test
    fun `pause should call audioPlayer pause and update state`() = runTest {
        service.play() // Start playing first

        service.isPlaying.test {
            skipItems(1) // Skip initial state

            service.pause()

            assertEquals(false, awaitItem())
            verify(mockAudioPlayer).pause()
        }
    }

    @Test
    fun `calling play when already playing should not cause issues`() {
        service.play()
        service.play()

        verify(mockAudioPlayer).play() // Should only be called once due to guard
    }

    // BPM Tests

    @Test
    fun `setBpm should call audioPlayer and update state`() = runTest {
        service.bpm.test {
            skipItems(1) // Skip initial value

            service.setBpm(180)

            assertEquals(180, awaitItem())
            verify(mockAudioPlayer).setBpm(180)
        }
    }

    @Test
    fun `setBpm multiple times should update state each time`() = runTest {
        service.setBpm(160)
        service.setBpm(170)
        service.setBpm(180)

        service.bpm.test {
            assertEquals(180, awaitItem())
        }
    }

    // Volume Tests

    @Test
    fun `setVolume should call audioPlayer and update state`() = runTest {
        service.volume.test {
            skipItems(1) // Skip initial value

            service.setVolume(50)

            assertEquals(50, awaitItem())
            verify(mockAudioPlayer).setVolume(50)
        }
    }

    // Sound Tests

    @Test
    fun `setSound should call audioPlayer and update state`() = runTest {
        service.sound.test {
            skipItems(1) // Skip initial value

            service.setSound(MetronomeSoundEnum.SNARE)

            assertEquals(MetronomeSoundEnum.SNARE, awaitItem())
            verify(mockAudioPlayer).setSound(MetronomeSoundEnum.SNARE)
        }
    }

    // Audio Usage Type Tests

    @Test
    fun `setAudioUsageType should call audioPlayer and update state`() = runTest {
        service.audioUsageType.test {
            skipItems(1) // Skip initial value

            service.setAudioUsageType(AudioUsageType.NOTIFICATION)

            assertEquals(AudioUsageType.NOTIFICATION, awaitItem())
            verify(mockAudioPlayer).setAudioUsageType(AudioUsageType.NOTIFICATION)
        }
    }

    // Playback Mode Tests

    @Test
    fun `setPlaybackMode should call audioPlayer`() {
        service.setPlaybackMode(PlaybackMode.PATTERN)

        verify(mockAudioPlayer).setPlaybackMode(PlaybackMode.PATTERN)
    }

    // Accent Pattern Tests

    @Test
    fun `setAccentPattern should call audioPlayer`() {
        service.setAccentPattern(AccentPattern.EVERY_4TH)

        verify(mockAudioPlayer).setAccentPattern(AccentPattern.EVERY_4TH)
    }

    // Drum Pattern Tests

    @Test
    fun `setDrumPattern should call audioPlayer`() {
        val pattern = DrumPattern(sound1 = MetronomeSoundEnum.KNOCK)

        service.setDrumPattern(pattern)

        verify(mockAudioPlayer).setDrumPattern(pattern)
    }

    // Multiple Operation Tests

    @Test
    fun `multiple settings should all propagate to audioPlayer`() {
        service.setBpm(180)
        service.setVolume(90)
        service.setSound(MetronomeSoundEnum.DRUMTR808)
        service.setPlaybackMode(PlaybackMode.PATTERN)
        service.setAccentPattern(AccentPattern.EVERY_2ND)

        verify(mockAudioPlayer).setBpm(180)
        verify(mockAudioPlayer).setVolume(90)
        verify(mockAudioPlayer).setSound(MetronomeSoundEnum.DRUMTR808)
        verify(mockAudioPlayer).setPlaybackMode(PlaybackMode.PATTERN)
        verify(mockAudioPlayer).setAccentPattern(AccentPattern.EVERY_2ND)
    }

    @Test
    fun `play pause play sequence should work correctly`() = runTest {
        service.isPlaying.test {
            assertEquals(false, awaitItem())

            service.play()
            assertEquals(true, awaitItem())

            service.pause()
            assertEquals(false, awaitItem())

            service.play()
            assertEquals(true, awaitItem())
        }
    }
}
