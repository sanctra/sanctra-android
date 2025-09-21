package com.sanctra.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sanctra.feature.chat.ChatMessage

@Composable
fun TranscriptList(messages: List<ChatMessage>) {
  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    items(messages) { msg -> Text(text = msg.text) }
  }
}
