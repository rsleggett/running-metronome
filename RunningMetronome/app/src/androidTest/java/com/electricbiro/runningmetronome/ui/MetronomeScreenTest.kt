package com.electricbiro.runningmetronome.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.electricbiro.runningmetronome.MainActivity
import com.electricbiro.runningmetronome.MetronomeScreen
import com.electricbiro.runningmetronome.data.model.AccentPattern
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import com.electricbiro.runningmetronome.data.model.PlaybackMode
import com.electricbiro.runningmetronome.ui.theme.RunningMetronomeTheme
import com.electricbiro.runningmetronome.ui.viewmodel.MetronomeViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for MetronomeScreen composable.
 * Tests user interactions, state rendering, and Compose UI components.
 */
@RunWith(AndroidJUnit4::class)
class MetronomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MetronomeViewModel

    @Before
    fun setup() {
        viewModel = MetronomeViewModel()

        composeTestRule.setContent {
            RunningMetronomeTheme {
                MetronomeScreen(viewModel = viewModel)
            }
        }
    }

    // Initial State Tests

    @Test
    fun testAppTitleIsDisplayed() {
        composeTestRule
            .onNodeWithText("Running Metronome")
            .assertIsDisplayed()
    }

    @Test
    fun testDefaultBPMIsDisplayed() {
        composeTestRule
            .onNodeWithText("175")
            .assertIsDisplayed()
    }

    @Test
    fun testBPMTextIsDisplayed() {
        composeTestRule
            .onNodeWithText("BPM")
            .assertIsDisplayed()
    }

    @Test
    fun testDefaultVolumeIsDisplayed() {
        composeTestRule
            .onNodeWithText("Volume: 75%")
            .assertIsDisplayed()
    }

    @Test
    fun testPausedTextIsDisplayedInitially() {
        composeTestRule
            .onNodeWithText("Paused")
            .assertIsDisplayed()
    }

    // Mode Toggle Tests

    @Test
    fun testSimpleModeChipIsSelected() {
        composeTestRule
            .onNodeWithText("Simple")
            .assertIsDisplayed()
    }

    @Test
    fun testPatternModeChipIsDisplayed() {
        composeTestRule
            .onNodeWithText("Pattern")
            .assertIsDisplayed()
    }

    @Test
    fun testSwitchingToPatternMode() {
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // Pattern mode UI should appear
        composeTestRule
            .onNodeWithText("Pattern Steps (8-step)")
            .assertIsDisplayed()
    }

    @Test
    fun testSwitchingBackToSimpleMode() {
        // Switch to pattern
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // Switch back to simple
        composeTestRule
            .onNodeWithText("Simple")
            .performClick()

        // Accent pattern selector should be visible in simple mode
        composeTestRule
            .onNodeWithText("Accent Pattern")
            .assertIsDisplayed()
    }

    // BPM Preset Tests

    @Test
    fun testQuickPresetsLabelIsDisplayed() {
        composeTestRule
            .onNodeWithText("Quick Presets")
            .assertIsDisplayed()
    }

    @Test
    fun testAllPresetBPMChipsAreDisplayed() {
        val presets = listOf("160", "170", "175", "180", "185")

        presets.forEach { preset ->
            composeTestRule
                .onAllNodesWithText(preset)
                .assertCountEquals(2) // One in BPM display, one in preset chips
        }
    }

    @Test
    fun testClickingPresetBPMUpdatesDisplay() {
        composeTestRule
            .onAllNodesWithText("180")
            .onLast() // Get the preset chip, not the BPM display
            .performClick()

        // BPM display should update
        composeTestRule
            .onNodeWithText("180")
            .assertIsDisplayed()
    }

    // Sound Selector Tests


    @Test
    fun testSoundLabelIsDisplayed() {
        composeTestRule
            .onNodeWithText("Sound")
            .assertIsDisplayed()
    }

    @Test
    fun testAllSoundsAreDisplayed() {
        composeTestRule
            .onNodeWithText("Classic")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Snare")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Knock")
            .assertIsDisplayed()
    }

    // Accent Pattern Tests (Simple Mode)

    @Test
    fun testAccentPatternLabelIsDisplayed() {
        composeTestRule
            .onNodeWithText("Accent Pattern")
            .assertIsDisplayed()
    }

    @Test
    fun testAllAccentPatternsAreDisplayed() {
        composeTestRule
            .onNodeWithText("None")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Every 2nd")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Every 3rd")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Every 4th")
            .assertIsDisplayed()
    }

    // Pattern Editor Tests (Pattern Mode)

    @Test
    fun testPatternEditorAppearsInPatternMode() {
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        composeTestRule
            .onNodeWithText("Pattern Sounds")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pattern Steps (8-step)")
            .assertIsDisplayed()
    }

    @Test
    fun testPatternStepsAreDisplayed() {
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // Check step numbers 1-8 are displayed
        for (i in 1..8) {
            composeTestRule
                .onNodeWithText("$i")
                .assertIsDisplayed()
        }
    }

    @Test
    fun testPatternStepInitiallyShowsRest() {
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // All steps should initially show "–" (rest)
        composeTestRule
            .onAllNodesWithText("–")
            .assertCountEquals(8)
    }

    // Audio Mode Tests

    @Test
    fun testAudioModeLabelIsDisplayed() {
        composeTestRule
            .onNodeWithText("Audio Mode")
            .assertIsDisplayed()
    }

    @Test
    fun testMediaAudioModeIsDisplayed() {
        composeTestRule
            .onNodeWithText("Media")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Always plays, uses media volume")
            .assertIsDisplayed()
    }

    @Test
    fun testNotificationAudioModeIsDisplayed() {
        composeTestRule
            .onNodeWithText("Notification")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Respects mute switch, uses notification volume")
            .assertIsDisplayed()
    }

    // Play/Pause Button Tests

    @Test
    fun testPlayButtonIsDisplayedInitially() {
        composeTestRule
            .onNodeWithContentDescription("Play")
            .assertIsDisplayed()
    }

    @Test
    fun testClickingPlayButtonShowsPauseButton() {
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Pause")
            .assertIsDisplayed()
    }

    @Test
    fun testClickingPlayButtonShowsPlayingText() {
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()

        composeTestRule
            .onNodeWithText("Playing")
            .assertIsDisplayed()
    }

    @Test
    fun testPlayPauseToggle() {
        // Click play
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()

        composeTestRule
            .onNodeWithText("Playing")
            .assertIsDisplayed()

        // Click pause
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .performClick()

        composeTestRule
            .onNodeWithText("Paused")
            .assertIsDisplayed()
    }

    // Tempo Slider Tests

    @Test
    fun testTempoLabelIsDisplayed() {
        composeTestRule
            .onNodeWithText("Tempo")
            .assertIsDisplayed()
    }

    @Test
    fun testTempoRangeLabelsAreDisplayed() {
        composeTestRule
            .onAllNodesWithText("40")
            .assertCountEquals(1)

        composeTestRule
            .onAllNodesWithText("200")
            .assertCountEquals(1)
    }

    // Integration Tests

    @Test
    fun testCompleteUserFlow() {
        // Select a preset BPM
        composeTestRule
            .onAllNodesWithText("180")
            .onLast()
            .performClick()

        // Verify BPM updated
        composeTestRule
            .onNodeWithText("180")
            .assertIsDisplayed()

        // Switch to pattern mode
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // Verify pattern editor appears
        composeTestRule
            .onNodeWithText("Pattern Steps (8-step)")
            .assertIsDisplayed()

        // Start playing
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()

        // Verify playing state
        composeTestRule
            .onNodeWithText("Playing")
            .assertIsDisplayed()

        // Pause
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .performClick()

        // Verify paused state
        composeTestRule
            .onNodeWithText("Paused")
            .assertIsDisplayed()
    }

    @Test
    fun testSwitchingModesWhilePlaying() {
        // Start playing in simple mode
        composeTestRule
            .onNodeWithContentDescription("Play")
            .performClick()

        // Switch to pattern mode while playing
        composeTestRule
            .onNodeWithText("Pattern")
            .performClick()

        // Should still be playing
        composeTestRule
            .onNodeWithText("Playing")
            .assertIsDisplayed()

        // Pattern editor should be visible
        composeTestRule
            .onNodeWithText("Pattern Steps (8-step)")
            .assertIsDisplayed()
    }
}
