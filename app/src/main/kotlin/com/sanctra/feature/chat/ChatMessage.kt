package com.sanctra.feature.chat

sealed class ChatMessage(open val text: String) {
  data class User(override val text: String) : ChatMessage(text)
  data class Agent(override val text: String) : ChatMessage(text)
  data class System(override val text: String) : ChatMessage(text)
}
