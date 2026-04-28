package com.sanctra.feature.session

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanctra.di.AppModule
import com.sanctra.feature.capture.audio.AudioRecorder
import com.sanctra.feature.chat.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak") // App context is acceptable in ViewModel
class SessionViewModel(
    private val appContext: Context // Use app context to avoid leaks
) : ViewModel() {

    private val orchestratorApi = AppModule.orchestratorApi
    private val streamClient = AppModule.streamClient
    private val personId = "default_person" // TODO: Make person_id selectable

    private val audioRecorder = AudioRecorder(appContext) { audioChunk ->
        streamClient.sendAudio(audioChunk)
    }

    private val _state = MutableStateFlow(SessionState())
    val state = _state.asStateFlow()

    init {
        startNewSession()
        listenForTranscripts()
    }

    private fun startNewSession() {
        viewModelScope.launch {
            _state.update { it.copy(isConnecting = true) }
            orchestratorApi.startSession(personId = personId)
                .onSuccess { sessionId ->
                    _state.update { it.copy(sessionId = sessionId, isConnecting = false, connected = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isConnecting = false, error = error.message) }
                }
        }
    }

    private fun listenForTranscripts() {
        viewModelScope.launch {
            streamClient.messages.collect { messageJson ->
                // TODO: Parse JSON from ASR and update state with final transcript
                val message = ChatMessage.Agent(messageJson) // Placeholder
                 _state.update { it.copy(transcript = it.transcript + message) }
            }
        }
    }

    fun onIntent(intent: SessionIntent) {
        when (intent) {
            is SessionIntent.SendText -> sendText(intent.text)
            SessionIntent.ToggleMic -> toggleMic()
        }
    }

    private fun toggleMic() {
        val isCurrentlyRecording = _state.value.isRecording
        val sessionId = _state.value.sessionId

        if (isCurrentlyRecording) {
            audioRecorder.stop()
            streamClient.disconnect()
            _state.update { it.copy(isRecording = false) }
        } else if (sessionId != null) {
            streamClient.connect(sessionId, personId)
            audioRecorder.start()
            _state.update { it.copy(isRecording = true) }
        }
    }

    private fun sendText(text: String) {
        if (text.isBlank()) return
        val userMessage = ChatMessage.User(text)
        _state.update { it.copy(transcript = it.transcript + userMessage) }

        // TODO: Call orchestrator's text endpoint
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stop()
        streamClient.disconnect()
    }
}
