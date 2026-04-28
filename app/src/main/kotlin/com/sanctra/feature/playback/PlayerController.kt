package com.sanctra.feature.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerController(context: Context) {
    private val player = ExoPlayer.Builder(context).build()

    fun getPlayer(): ExoPlayer = player

    fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }
}