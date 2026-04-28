package com.sanctra.feature.session

sealed interface SessionIntent {
  data object ToggleMic : SessionIntent
  data class SendText(val text: String) : SessionIntent
}
