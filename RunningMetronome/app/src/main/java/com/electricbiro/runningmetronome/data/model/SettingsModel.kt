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
}

enum class AudioUsageType(val displayName: String, val description: String) {
    NOTIFICATION("Notification", "Respects mute switch, uses notification volume"),
    MEDIA("Media", "Always plays, uses media volume")
}