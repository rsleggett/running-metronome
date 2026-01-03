package com.electricbiro.runningmetronome.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.*
import com.electricbiro.runningmetronome.R
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum

/**
 * Metronome audio player that plays click sounds at precise BPM intervals.
 * Uses SoundPool for low-latency playback that mixes with other audio.
 */
class MetronomeAudioPlayer(context: Context) {

    // Use application context to avoid attribution tag issues
    private val context: Context = context.applicationContext

    private var soundPool: SoundPool? = null
    private val soundIdMap = mutableMapOf<MetronomeSoundEnum, Int>()
    private var isPlaying = false
    private var playbackJob: Job? = null

    // Playback settings
    private var currentBpm: Int = 175
    private var currentVolume: Float = 0.75f // 0.0 to 1.0
    private var currentAudioUsageType: AudioUsageType = AudioUsageType.MEDIA
    private var currentSound: MetronomeSoundEnum = MetronomeSoundEnum.CLASSIC

    companion object {
        private const val TAG = "MetronomeAudioPlayer"
    }

    init {
        Log.d(TAG, "Initializing MetronomeAudioPlayer")
        setupSoundPool(currentAudioUsageType)
    }

    /**
     * Initialize SoundPool with audio attributes that allow mixing with music.
     */
    private fun setupSoundPool(usageType: AudioUsageType) {
        Log.d(TAG, "Setting up SoundPool with usage type: $usageType")

        val audioAttributes = when (usageType) {
            AudioUsageType.NOTIFICATION -> AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            AudioUsageType.MEDIA -> AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .build()
        }

        soundPool = SoundPool.Builder()
            .setMaxStreams(1) // Only need one concurrent sound
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            Log.d(TAG, "Sound load complete: sampleId=$sampleId, status=$status")
        }

        // Load the default sound
        loadSound(currentSound)
    }

    /**
     * Load a sound from resources.
     * @param sound The sound enum to load
     */
    private fun loadSound(sound: MetronomeSoundEnum) {
        val resourceId = when (sound) {
            MetronomeSoundEnum.CLASSIC -> R.raw.metronomeclick
        }

        Log.d(TAG, "Loading sound: $sound resourceId=$resourceId")
        soundPool?.let { pool ->
            if (!soundIdMap.containsKey(sound)) {
                val soundId = pool.load(context, resourceId, 1)
                soundIdMap[sound] = soundId
                Log.d(TAG, "Loaded $sound with ID: $soundId")
            }
        }
    }

    /**
     * Start playing the metronome with the current BPM setting.
     */
    fun play() {
        Log.d(TAG, "play() called, isPlaying=$isPlaying, volume=$currentVolume, bpm=$currentBpm")
        if (isPlaying) return
        isPlaying = true

        // Launch coroutine for precise timing
        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            Log.d(TAG, "Playback coroutine started")

            // Calculate delay based on BPM (beats per minute)
            // 60000 ms in a minute / BPM = milliseconds per beat
            val intervalMs = (60000.0 / currentBpm).toLong()

            while (isActive && isPlaying) {
                playBeat()
                delay(intervalMs)
            }
            Log.d(TAG, "Playback coroutine ended")
        }
    }

    /**
     * Play a beat
     */
    private fun playBeat() {
        soundPool?.let { pool ->
            val soundId = soundIdMap[currentSound]
            if (soundId != null && soundId != 0) {
                val streamId = pool.play(soundId, currentVolume, currentVolume, 1, 0, 1.0f)
                Log.d(TAG, "playBeat: sound=$currentSound, soundId=$soundId, volume=$currentVolume, streamId=$streamId")
            } else {
                Log.w(TAG, "playBeat: sound $currentSound not loaded yet")
            }
        } ?: Log.e(TAG, "playBeat: soundPool is null")
    }

    /**
     * Pause the metronome (stops playback but maintains state).
     */
    fun pause() {
        Log.d(TAG, "pause() called")
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
    }

    /**
     * Stop the metronome completely.
     */
    fun stop() {
        pause()
    }

    /**
     * Check if metronome is currently playing.
     */
    fun isPlaying(): Boolean = isPlaying

    /**
     * Set the tempo in beats per minute.
     * @param bpm Tempo value (40-200 BPM)
     */
    fun setBpm(bpm: Int) {
        Log.d(TAG, "Changing bpm from $currentBpm to $bpm")

        currentBpm = bpm.coerceIn(40, 200)
    }

    /**
     * Set the playback volume.
     * @param volume Volume level (0-100)
     */
    fun setVolume(volume: Int) {
        Log.d(TAG, "Changing volume from $currentVolume to $volume")

        currentVolume = (volume.coerceIn(0, 100) / 100f)
    }

    /**
     * Change the metronome sound.
     * @param sound The sound enum to use
     */
    fun setSound(sound: MetronomeSoundEnum) {
        Log.d(TAG, "setSound: $sound")
        loadSound(sound)
        currentSound = sound
    }

    /**
     * Change the audio usage type (requires recreating SoundPool).
     * @param usageType The audio usage type to use
     */
    fun setAudioUsageType(usageType: AudioUsageType) {
        if (currentAudioUsageType == usageType) return

        Log.d(TAG, "Changing audio usage type from $currentAudioUsageType to $usageType")
        val wasPlaying = isPlaying

        // Stop playback if currently playing
        if (wasPlaying) {
            pause()
        }

        // Release old SoundPool
        soundPool?.release()
        soundPool = null
        soundIdMap.clear()

        // Create new SoundPool with new usage type
        currentAudioUsageType = usageType
        setupSoundPool(usageType)

        // Resume playback if it was playing
        if (wasPlaying) {
            play()
        }
    }

    /**
     * Release all resources. Call this when done with the player.
     */
    fun release() {
        stop()
        soundPool?.release()
        soundPool = null
        soundIdMap.clear()
    }
}