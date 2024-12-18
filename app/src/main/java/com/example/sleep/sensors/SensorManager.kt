package com.example.sleep.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SleepSensorManager(context: Context) {
    private val sensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    private val _accelerometerData = MutableStateFlow(FloatArray(3))
    val accelerometerData = _accelerometerData.asStateFlow()
    
    private val _magnetometerData = MutableStateFlow(FloatArray(3))
    val magnetometerData = _magnetometerData.asStateFlow()
    
    private val _lightData = MutableStateFlow(0f)
    val lightData = _lightData.asStateFlow()
    
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> _accelerometerData.value = event.values.clone()
                Sensor.TYPE_MAGNETIC_FIELD -> _magnetometerData.value = event.values.clone()
                Sensor.TYPE_LIGHT -> _lightData.value = event.values[0]
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    
    fun startMonitoring() {
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    
    fun stopMonitoring() {
        sensorManager.unregisterListener(sensorListener)
    }
} 