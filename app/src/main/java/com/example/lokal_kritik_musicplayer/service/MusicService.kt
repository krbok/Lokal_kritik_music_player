package com.example.lokal_kritik_musicplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.lokal_kritik_musicplayer.R

class MusicService : MediaSessionService() {

    companion object {
        const val CHANNEL_ID = "music_playback_channel"
        const val NOTIF_ID = 1

        const val ACTION_TOGGLE = "com.example.lokal_kritik_musicplayer.ACTION_TOGGLE"
        const val ACTION_NEXT = "com.example.lokal_kritik_musicplayer.ACTION_NEXT"
        const val ACTION_PREV = "com.example.lokal_kritik_musicplayer.ACTION_PREV"
        const val ACTION_SEEK = "com.example.lokal_kritik_musicplayer.ACTION_SEEK"
        const val ACTION_FORWARD = "com.example.lokal_kritik_musicplayer.ACTION_FORWARD"
        const val ACTION_REVERSE = "com.example.lokal_kritik_musicplayer.ACTION_REVERSE"
        const val ACTION_STOP = "com.example.lokal_kritik_musicplayer.ACTION_STOP"
        
        const val EXTRA_POSITION = "position"
        const val EXTRA_URL = "url"
        const val EXTRA_TITLE = "title"
        const val EXTRA_IMAGE = "image"
    }

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player!!).build()

        val notificationProvider = DefaultMediaNotificationProvider(this)
        setMediaNotificationProvider(notificationProvider)

        val placeholder: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Preparing playback…")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
        startForeground(NOTIF_ID, placeholder)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE -> {
                player?.let { if (it.isPlaying) it.pause() else it.play() }
            }
            ACTION_NEXT -> {
                player?.seekToNextMediaItem()
                player?.play()
            }
            ACTION_PREV -> {
                player?.seekToPreviousMediaItem()
                player?.play()
            }
            ACTION_FORWARD -> {
                player?.let {
                    val newPos = it.currentPosition + 10000
                    it.seekTo(newPos.coerceAtMost(it.duration))
                }
            }
            ACTION_REVERSE -> {
                player?.let {
                    val newPos = it.currentPosition - 10000
                    it.seekTo(newPos.coerceAtLeast(0L))
                }
            }
            ACTION_SEEK -> {
                val pos = intent.getLongExtra(EXTRA_POSITION, -1L)
                if (pos >= 0) player?.seekTo(pos)
            }
            ACTION_STOP -> {
                player?.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                val url = intent?.getStringExtra(EXTRA_URL)
                val title = intent?.getStringExtra(EXTRA_TITLE)

                if (!url.isNullOrEmpty()) {
                    val mediaItem = MediaItem.Builder()
                        .setUri(url)
                        .setMediaId(title ?: "Unknown")
                        .setMediaMetadata(
                            androidx.media3.common.MediaMetadata.Builder()
                                .setTitle(title)
                                .build()
                        )
                        .build()

                    player?.apply {
                        clearMediaItems()
                        setMediaItem(mediaItem)
                        prepare()
                        play()
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun getPlayer(): ExoPlayer? = player

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaSession?.release()
        player?.release()
        mediaSession = null
        player = null
        super.onDestroy()
    }
}
