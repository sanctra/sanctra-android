package com.sanctra.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TransportControls(onToggleMic: () -> Unit, onSendText: (String) -> Unit) {
  Button(onClick = onToggleMic) { Text(""Mic"") }
}
