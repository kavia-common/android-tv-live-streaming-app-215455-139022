package com.example.androidtv_frontend.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.example.androidtv_frontend.R

/**
 * PUBLIC_INTERFACE
 * PlaybackActivity hosts Media3 ExoPlayer for VOD and Live playback with MediaSession integration.
 *
 * Intent extras:
 * - EXTRA_MEDIA_ID (String)
 * - EXTRA_MEDIA_TITLE (String)
 * - EXTRA_MEDIA_URL (String) required
 * - EXTRA_MEDIA_IS_LIVE (Boolean)
 */
class PlaybackActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MEDIA_ID = "extra_media_id"
        const val EXTRA_MEDIA_TITLE = "extra_media_title"
        const val EXTRA_MEDIA_URL = "extra_media_url"
        const val EXTRA_MEDIA_IS_LIVE = "extra_media_is_live"

        // PUBLIC_INTERFACE
        /**
         * Helper to build an Intent for playback.
         */
        fun createIntent(
            context: Context,
            id: String,
            title: String,
            url: String,
            isLive: Boolean
        ): Intent = Intent(context, PlaybackActivity::class.java).apply {
            putExtra(EXTRA_MEDIA_ID, id)
            putExtra(EXTRA_MEDIA_TITLE, title)
            putExtra(EXTRA_MEDIA_URL, url)
            putExtra(EXTRA_MEDIA_IS_LIVE, isLive)
        }
    }

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private lateinit var playerView: PlayerView

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerView = PlayerView(this).apply {
            useController = true
            controllerAutoShow = true
            controllerShowTimeoutMs = 3000
            setShowRewindButton(true)
            setShowFastForwardButton(true)
            setShowPreviousButton(false)
            setShowNextButton(false)
            contentDescription = getString(R.string.app_name)
        }
        setContentView(FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            addView(playerView)
        })

        val title = intent.getStringExtra(EXTRA_MEDIA_TITLE) ?: "Playback"
        setTitle(title)

        val url = intent.getStringExtra(EXTRA_MEDIA_URL)
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "Playback URL missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val isLive = intent.getBooleanExtra(EXTRA_MEDIA_IS_LIVE, false)

        try {
            val exo = ExoPlayer.Builder(this).build().also { p ->
                p.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Toast.makeText(this@PlaybackActivity, "Playback error", Toast.LENGTH_LONG).show()
                        Log.e("PlaybackActivity", "Player error: ${error.errorCodeName}")
                    }
                })
            }
            player = exo

            // Configure media item with live hint and inferred mimeType when possible
            val builder = MediaItem.Builder()
                .setUri(Uri.parse(url))
                .setMimeType(inferMimeType(url))

            if (isLive) {
                builder.setLiveConfiguration(
                    MediaItem.LiveConfiguration.Builder().build()
                )
            }

            val mediaItem = builder.build()

            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = true

            playerView.player = exo

            // MediaSession for TV controls and safe background behavior
            mediaSession = MediaSession.Builder(this, exo).build()

        } catch (e: Exception) {
            // Graceful error on initialization failures
            Toast.makeText(this, "Unable to initialize player", Toast.LENGTH_LONG).show()
            Log.e("PlaybackActivity", "Init failure: ${e.message}")
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23) {
            playerView.onResume()
        }
    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT <= 23) {
            playerView.onPause()
        }
        super.onPause()
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT > 23) {
            playerView.onPause()
        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaSession?.release()
        } catch (_: Exception) {
        }
        try {
            playerView.player = null
            player?.release()
            player = null
        } catch (_: Exception) {
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val p = player
        if (p != null && (event?.action == KeyEvent.ACTION_DOWN)) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    if (p.isPlaying) p.pause() else p.play()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    p.play()
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    p.pause()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    seekBySafe(p, -10_000L) // 10s
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    seekBySafe(p, 10_000L) // 10s
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun seekBySafe(p: ExoPlayer, deltaMs: Long) {
        val target = (p.currentPosition + deltaMs).coerceIn(0L, if (p.duration > 0) p.duration else Long.MAX_VALUE)
        p.seekTo(target)
    }

    @UnstableApi
    private fun inferMimeType(url: String): String {
        val lower = url.lowercase()
        return when {
            lower.endsWith(".m3u8") || lower.contains("m3u8") -> MimeTypes.APPLICATION_M3U8
            lower.endsWith(".mpd") || lower.contains("mpd") -> MimeTypes.APPLICATION_MPD
            lower.endsWith(".mp4") -> MimeTypes.VIDEO_MP4
            else -> MimeTypes.APPLICATION_M3U8 // default safe for many demos
        }
    }
}
