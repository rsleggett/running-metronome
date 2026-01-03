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
 * Simple accent patterns for metronome mode
 */
enum class AccentPattern(val displayName: String, val beats: Int) {
    NONE("None", 0),
    EVERY_2ND("Every 2nd", 2),
    EVERY_3RD("Every 3rd", 3),
    EVERY_4TH("Every 4th", 4)
}