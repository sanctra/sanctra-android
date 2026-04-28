package com.sanctra.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val playerView = PlayerView(context).apply {
            this.player = player
            useController = true
        }
        
        onDispose {
            // Player lifecycle is managed by the ViewModel,
            // so we don't release it here.
            playerView.player = null
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                this.player = player
            }
        },
        modifier = modifier
    )
}