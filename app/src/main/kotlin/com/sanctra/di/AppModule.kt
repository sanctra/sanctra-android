package com.sanctra.di

import com.sanctra.feature.net.OrchestratorApi
import com.sanctra.feature.net.StreamClient
import com.sanctra.feature.playback.PlayerController
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object AppModule {
    // In a production app, use Hilt or Koin for DI.
    // For now, a simple object singleton is fine.

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    // TODO: Move base URL to BuildConfig or a remote config solution
    private const val ORCHESTRATOR_HTTP_URL = "http://10.0.2.2:8080" // For Android emulator
    private const val ORCHESTRATOR_WS_URL = "ws://10.0.2.2:8080"

    val orchestratorApi: OrchestratorApi by lazy {
        OrchestratorApi(ORCHESTRATOR_HTTP_URL, okHttpClient)
    }

    val streamClient: StreamClient by lazy {
        StreamClient(okHttpClient, ORCHESTRATOR_WS_URL)
    }
}