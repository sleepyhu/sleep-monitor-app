package com.example.sleep.data.models

data class SleepData(
    val timestamp: Long,
    val snoring: Boolean = false,
    val coughing: Boolean = false,
    val movement: Boolean = false,
    val breathingRate: Float = 0f,
    val sleepStage: SleepStage = SleepStage.UNKNOWN,
    val lightLevel: Float = 0f
)

enum class SleepStage {
    AWAKE, LIGHT_SLEEP, DEEP_SLEEP, REM, UNKNOWN
} 