package com.example.sleep.audio

class AudioFeatureExtractor {
    companion object {
        const val SAMPLE_RATE = 16000 // 16kHz
        const val FRAME_SIZE = 1600   // 0.1秒 = 16000 * 0.1
        const val ALPHA = 0.1f        // 滤波器系数
    }

    // 计算均方根值(RMS)
    fun calculateRMS(frame: ShortArray): Double {
        var sum = 0.0
        frame.forEach { sample ->
            sum += sample * sample
        }
        return Math.sqrt(sum / frame.size)
    }

    // 计算低频与高频能量比(RLH)
    fun calculateRLH(frame: ShortArray): Double {
        val lowPass = getLowPassFiltered(frame)
        val highPass = getHighPassFiltered(frame)
        
        val rmsLow = calculateRMS(lowPass)
        val rmsHigh = calculateRMS(highPass)
        
        return if (rmsHigh != 0.0) rmsLow / rmsHigh else 0.0
    }

    // 计算方差(VAR)
    fun calculateVariance(frame: ShortArray): Double {
        val mean = frame.average()
        return frame.map { (it - mean) * (it - mean) }.average()
    }

    // 低通滤波器
    private fun getLowPassFiltered(frame: ShortArray): ShortArray {
        val filtered = ShortArray(frame.size)
        filtered[0] = frame[0]
        
        for (i in 1 until frame.size) {
            filtered[i] = (filtered[i-1] + ALPHA * (frame[i] - filtered[i-1])).toInt().toShort()
        }
        return filtered
    }

    // 高通滤波器
    private fun getHighPassFiltered(frame: ShortArray): ShortArray {
        val filtered = ShortArray(frame.size)
        filtered[0] = frame[0]
        
        for (i in 1 until frame.size) {
            filtered[i] = (ALPHA * (filtered[i-1] + frame[i] - frame[i-1])).toInt().toShort()
        }
        return filtered
    }
} 