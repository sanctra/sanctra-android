package com.sanctra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sanctra.ui.theme.SanctraTheme
import com.sanctra.ui.screens.SessionScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SanctraTheme {
        SessionScreen()
      }
    }
  }
}
