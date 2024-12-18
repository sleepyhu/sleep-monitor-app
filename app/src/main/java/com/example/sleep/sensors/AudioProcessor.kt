package com.example.sleep.sensors

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioProcessor {
    private val SAMPLE_RATE = 44100
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    
    private val _audioData = MutableStateFlow<ShortArray?>(null)
    val audioData = _audioData.asStateFlow()
    
    fun startRecording() {
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
        
        val buffer = ShortArray(bufferSize)
        audioRecord?.startRecording()
        isRecording = true
        
        // 在协程中处理音频数据
        while (isRecording) {
            val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: 0
            if (readSize > 0) {
                processAudioData(buffer.copyOf(readSize))
            }
        }
    }
    
    private fun processAudioData(data: ShortArray) {
        // 1. 进行降噪处理
        val denoisedData = applyNoiseReduction(data)
        // 2. 提取特征
        extractFeatures(denoisedData)
        _audioData.value = denoisedData
    }
    
    private fun applyNoiseReduction(data: ShortArray): ShortArray {
        // 实现降噪算法
        return data // 临时返回原始数据
    }
    
    private fun extractFeatures(data: ShortArray) {
        // 计算RMS
        val rms = calculateRMS(data)
        // 计算其他特征
        // TODO: 实现MFCC等特征提取
    }
    
    private fun calculateRMS(data: ShortArray): Double {
        var sum = 0.0
        for (sample in data) {
            sum += sample * sample
        }
        return Math.sqrt(sum / data.size)
    }
    
    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
} 