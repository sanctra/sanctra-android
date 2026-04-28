package com.sanctra.feature.capture.audio

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ForegroundMicService : Service() {
  override fun onBind(intent: Intent?): IBinder? = null
}
