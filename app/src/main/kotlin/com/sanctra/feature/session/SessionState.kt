package com.sanctra.feature.session

import com.sanctra.feature.chat.ChatMessage

data class SessionState(
  val sessionId: String? = null,
  val isConnecting: Boolean = true,
  val isRecording: Boolean = false,
  val micPermissionGranted: Boolean = false,
  val transcript: List<ChatMessage> = emptyList(),
  val interimTranscript: String? = null,
  val videoUrl: String? = null,
  val error: String? = null
)