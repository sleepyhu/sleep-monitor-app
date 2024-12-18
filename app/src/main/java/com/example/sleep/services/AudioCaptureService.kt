package com.example.sleep.services

import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AudioCaptureService : Service() {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    
    private val _audioData = MutableSharedFlow<ShortArray>()
    val audioData = _audioData.asSharedFlow()
    
    companion object {
        const val SAMPLE_RATE = 16000 // 16kHz
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val FRAME_SIZE = 1600 // 0.1秒的数据
    }
    
    override fun onCreate() {
        super.onCreate()
        initAudioRecorder()
    }
    
    private fun initAudioRecorder() {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )
    }
    
    fun startRecording() {
        if (isRecording) return
        
        audioRecord?.startRecording()
        isRecording = true
        
        serviceScope.launch(Dispatchers.IO) {
            val buffer = ShortArray(FRAME_SIZE)
            while (isRecording) {
                val readSize = audioRecord?.read(buffer, 0, FRAME_SIZE) ?: 0
                if (readSize > 0) {
                    _audioData.emit(buffer.copyOf(readSize))
                }
            }
        }
    }
    
    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        audioRecord?.release()
        audioRecord = null
    }
}