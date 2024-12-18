package com.example.sleep.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioEventProcessor {
    private val featureExtractor = AudioFeatureExtractor()
    private val eventClassifier = SleepEventClassifier()
    
    private val _currentEvent = MutableStateFlow<SleepEvent>(SleepEvent.UNKNOWN)
    val currentEvent = _currentEvent.asStateFlow()
    
    // 用于事件平滑处理的缓冲区
    private val eventBuffer = mutableListOf<SleepEvent>()
    private const val BUFFER_SIZE = 5
    
    fun processFrame(frame: ShortArray) {
        // 1. 事件分类
        val event = eventClassifier.classifyFrame(frame)
        
        // 2. 事件平滑处理
        eventBuffer.add(event)
        if (eventBuffer.size > BUFFER_SIZE) {
            eventBuffer.removeAt(0)
        }
        
        // 3. 使用多数投票法确定最终事件
        val smoothedEvent = smoothEvents()
        _currentEvent.value = smoothedEvent
    }
    
    private fun smoothEvents(): SleepEvent {
        return eventBuffer
            .groupBy { it }
            .maxByOrNull { it.value.size }
            ?.key ?: SleepEvent.UNKNOWN
    }
} 