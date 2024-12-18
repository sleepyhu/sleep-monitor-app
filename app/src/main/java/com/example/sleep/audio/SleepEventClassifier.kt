package com.example.sleep.audio

enum class SleepEvent {
    SNORING, COUGHING, MOVEMENT, NOISE, UNKNOWN
}

class SleepEventClassifier {
    companion object {
        private const val RLH_THRESHOLD = 2.0
        private const val VAR_THRESHOLD = 1000.0
        private const val RMS_THRESHOLD = 500.0
    }

    private val featureExtractor = AudioFeatureExtractor()
    
    fun classifyFrame(frame: ShortArray): SleepEvent {
        // 如果是噪声帧，直接返回
        if (isNoise(frame)) return SleepEvent.NOISE
        
        val rlh = featureExtractor.calculateRLH(frame)
        val variance = featureExtractor.calculateVariance(frame)
        val rms = featureExtractor.calculateRMS(frame)
        
        // 决策树分类
        return when {
            // 高RLH通常表示打鼾
            rlh > RLH_THRESHOLD -> SleepEvent.SNORING
            
            // 高方差可能是咳嗽
            variance > VAR_THRESHOLD -> SleepEvent.COUGHING
            
            // 高RMS但低方差可能是移动
            rms > RMS_THRESHOLD -> SleepEvent.MOVEMENT
            
            else -> SleepEvent.UNKNOWN
        }
    }

    private fun isNoise(frame: ShortArray): Boolean {
        val windowSize = 10 // 10帧窗口
        val stdThreshold = 0.5
        
        // 计算标准差
        val std = calculateStandardDeviation(frame)
        
        // TODO: 实现滑动窗口的方差计算
        // 这里简化处理，仅用单帧判断
        return std < stdThreshold
    }

    private fun calculateStandardDeviation(frame: ShortArray): Double {
        val variance = featureExtractor.calculateVariance(frame)
        return Math.sqrt(variance)
    }
} 