package com.example.sleep.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleep.audio.AudioEventProcessor
import com.example.sleep.audio.SleepEvent
import com.example.sleep.services.AudioCaptureService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepMonitorViewModel : ViewModel() {
    private val audioCaptureService = AudioCaptureService()
    private val audioEventProcessor = AudioEventProcessor()
    
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()
    
    private val _currentEvent = MutableStateFlow<SleepEvent>(SleepEvent.UNKNOWN)
    val currentEvent = _currentEvent.asStateFlow()
    
    fun startMonitoring() {
        viewModelScope.launch {
            _isRecording.value = true
            audioCaptureService.startRecording()
            
            // 收集音频数据并处理
            audioCaptureService.audioData.collect { frame ->
                audioEventProcessor.processFrame(frame)
            }
        }
        
        // 收集事件结果
        viewModelScope.launch {
            audioEventProcessor.currentEvent.collect {
                _currentEvent.value = it
            }
        }
    }
    
    fun stopMonitoring() {
        _isRecording.value = false
        audioCaptureService.stopRecording()
    }
    
    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
} 