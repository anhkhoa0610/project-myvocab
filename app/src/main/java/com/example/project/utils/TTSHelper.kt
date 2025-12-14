package com.example.project.utils

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale

class TTSHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                
                // Set speech rate (tốc độ đọc)
                tts?.setSpeechRate(0.9f) // Hơi chậm một chút để dễ nghe
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotEmpty()) {
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f) // Max volume
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    fun isReady(): Boolean = isInitialized
}
