package com.electricbiro.runningmetronome

import android.app.Application
import com.electricbiro.runningmetronome.audio.MetronomeAudioPlayer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MetronomeApplication : Application {
    constructor() {
        Application()
    }
    constructor(audio: MetronomeAudioPlayer) {
        Application()
    }
}
