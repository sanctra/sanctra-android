package com.sanctra.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanctra.feature.chat.ChatMessage
import com.sanctra.feature.playback.PlayerController
import com.sanctra.feature.session.SessionIntent
import com.sanctra.feature.session.SessionState
import com.sanctra.feature.session.SessionViewModel
import com.sanctra.ui.components.VideoPlayer

@Composable
fun SessionScreen() {
    // ViewModel factory to pass application context
    val context = LocalContext.current
    val vm: SessionViewModel = viewModel(factory = SessionViewModel.Factory(context.applicationContext))
    val state by vm.state.collectAsStateWithLifecycle()

    // Create a player instance that will be managed across compositions
    val playerController = remember { PlayerController(context) }
    
    // Update player when video URL changes
    LaunchedEffect(state.videoUrl) {
        state.videoUrl?.let { playerController.play(it) }
    }

    // Dispose of the player when the screen is removed
    DisposableEffect(Unit) {
        onDispose {
            playerController.release()
        }
    }

    // Microphone permission handler
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            vm.onIntent(SessionIntent.MicPermissionResult(isGranted))
        }
    )
    
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sanctra Session") })
        },
        bottomBar = {
            TransportControls(state = state, onIntent = vm::onIntent)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (state.isConnecting) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                VideoPane(
                    videoUrl = state.videoUrl,
                    playerController = playerController,
                    modifier = Modifier.weight(1f)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                TranscriptPane(
                    messages = state.transcript,
                    interim = state.interimTranscript,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun VideoPane(videoUrl: String?, playerController: PlayerController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        contentAlignment = Alignment.Center
    ) {
        if (videoUrl != null) {
            VideoPlayer(player = playerController.getPlayer(), modifier = Modifier.fillMaxSize())
        } else {
            Text("Video will appear here...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TranscriptPane(messages: List<ChatMessage>, interim: String?, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(messages) { msg ->
            val alignment = if (msg is ChatMessage.User) Alignment.End else Alignment.Start
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                Card(
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = if (msg is ChatMessage.User) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
                ) {
                    Text(text = msg.text, modifier = Modifier.padding(8.dp))
                }
            }
        }
        if (interim != null) {
            item {
                Text(
                    text = interim,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TransportControls(state: SessionState, onIntent: (SessionIntent) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        // TODO: Add a text input field here
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onIntent(SessionIntent.ToggleMic) },
            enabled = state.micPermissionGranted && state.sessionId != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isRecording) "Stop Listening" else "Start Listening")
        }
        if (!state.micPermissionGranted) {
            Text("Microphone permission is required to speak.", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
        }
    }
}