package com.electricbiro.runningmetronome.data.model

import com.electricbiro.runningmetronome.R

class SettingsModel {
    var bpm: Float = 175.0f;
    var sound: MetronomeSoundEnum = MetronomeSoundEnum.CLASSIC;
    var volume = 75;
    var audioUsageType: AudioUsageType = AudioUsageType.MEDIA;
}

enum class MetronomeSoundEnum(resourceId: Int) {
    CLASSIC(R.raw.metronomeclick),
    SNARE(R.raw.metronomesnare),
    KNOCK(R.raw.metronomeknock),
    DRUMTR707(R.raw.metronomedrumtr707),
    DRUMTR808(R.raw.metronomedrumtr808),
    DRUMTR909(R.raw.metronomedrumtr909),
}

enum class AudioUsageType(val displayName: String, val description: String) {
    NOTIFICATION("Notification", "Respects mute switch, uses notification volume"),
    MEDIA("Media", "Always plays, uses media volume")
}

/**
 * Playback mode - either simple metronome or pattern sequencer
 */
enum class PlaybackMode {
    SIMPLE,    // Standard metronome with optional accents
    PATTERN    // 8-step drum pattern
}

/**
 * Simple accent patterns for metronome mode
 */
enum class AccentPattern(val displayName: String, val beats: Int) {
    NONE("None", 0),
    EVERY_2ND("Every 2nd", 2),
    EVERY_3RD("Every 3rd", 3),
    EVERY_4TH("Every 4th", 4)
}

/**
 * A single step in an 8-step drum pattern
 */
data class PatternStep(
    val sound: MetronomeSoundEnum?,  // null = rest/silence
    val volume: Float = 1.0f          // 0.0 to 1.0 (multiplier on base volume)
)

/**
 * Complete 8-step drum pattern
 */
data class DrumPattern(
    val steps: List<PatternStep> = List(8) { PatternStep(null) },
    val sound1: MetronomeSoundEnum = MetronomeSoundEnum.CLASSIC,
    val sound2: MetronomeSoundEnum = MetronomeSoundEnum.SNARE
) {
    init {
        require(steps.size == 8) { "Pattern must have exactly 8 steps" }
    }
}