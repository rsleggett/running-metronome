package com.electricbiro.runningmetronome.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.electricbiro.runningmetronome.MainActivity
import com.electricbiro.runningmetronome.R
import com.electricbiro.runningmetronome.audio.MetronomeAudioPlayer
import com.electricbiro.runningmetronome.data.model.AudioUsageType
import com.electricbiro.runningmetronome.data.model.MetronomeSoundEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Foreground service for continuous metronome playback in the background.
 * Allows the metronome to play even when the app is minimized or screen is off.
 */
@AndroidEntryPoint
class MetronomeService : Service() {

    @Inject
    lateinit var audioPlayer: MetronomeAudioPlayer

    private val binder = MetronomeBinder()

    // Service state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _bpm = MutableStateFlow(175)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _volume = MutableStateFlow(75)
    val volume: StateFlow<Int> = _volume.asStateFlow()

    private val _sound = MutableStateFlow(MetronomeSoundEnum.CLASSIC)
    val sound: StateFlow<MetronomeSoundEnum> = _sound.asStateFlow()

    private val _audioUsageType = MutableStateFlow(AudioUsageType.MEDIA)
    val audioUsageType: StateFlow<AudioUsageType> = _audioUsageType.asStateFlow()

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "metronome_channel"
        private const val CHANNEL_NAME = "Metronome Playback"

        // Intent actions for notification controls
        const val ACTION_PLAY = "com.electricbiro.runningmetronome.ACTION_PLAY"
        const val ACTION_PAUSE = "com.electricbiro.runningmetronome.ACTION_PAUSE"
        const val ACTION_STOP = "com.electricbiro.runningmetronome.ACTION_STOP"
    }

    inner class MetronomeBinder : Binder() {
        fun getService(): MetronomeService = this@MetronomeService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_PAUSE -> pause()
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows metronome playback controls"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Build the foreground notification with playback controls
     */
    private fun buildNotification(): Notification {
        // Intent to open the app when notification is tapped
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for play/pause action
        val playPauseIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MetronomeService::class.java).apply {
                action = if (_isPlaying.value) ACTION_PAUSE else ACTION_PLAY
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for stop action
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MetronomeService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Running Metronome")
            .setContentText("${_bpm.value} BPM - ${_sound.value.name.lowercase().replaceFirstChar { it.uppercase() }}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .addAction(
                if (_isPlaying.value) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground,
                if (_isPlaying.value) "Pause" else "Play",
                playPauseIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                stopIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1)
            )
            .build()
    }

    /**
     * Update the notification with current state
     */
    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    /**
     * Start metronome playback
     */
    fun play() {
        if (!_isPlaying.value) {
            audioPlayer.play()
            _isPlaying.value = true

            // Start foreground service with notification
            startForeground(NOTIFICATION_ID, buildNotification())
        }
    }

    /**
     * Pause metronome playback
     */
    fun pause() {
        if (_isPlaying.value) {
            audioPlayer.pause()
            _isPlaying.value = false
            updateNotification()
        }
    }

    /**
     * Set BPM and update notification
     */
    fun setBpm(bpm: Int) {
        audioPlayer.setBpm(bpm)
        _bpm.value = bpm
        if (_isPlaying.value) {
            updateNotification()
        }
    }

    /**
     * Set volume
     */
    fun setVolume(volume: Int) {
        audioPlayer.setVolume(volume)
        _volume.value = volume
    }

    /**
     * Set sound and update notification
     */
    fun setSound(sound: MetronomeSoundEnum) {
        audioPlayer.setSound(sound)
        _sound.value = sound
        if (_isPlaying.value) {
            updateNotification()
        }
    }

    /**
     * Set audio usage type
     */
    fun setAudioUsageType(usageType: AudioUsageType) {
        audioPlayer.setAudioUsageType(usageType)
        _audioUsageType.value = usageType
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
    }
}
