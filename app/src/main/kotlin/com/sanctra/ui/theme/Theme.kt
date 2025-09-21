package com.sanctra.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun SanctraTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = lightColorScheme(), content = content)
}
