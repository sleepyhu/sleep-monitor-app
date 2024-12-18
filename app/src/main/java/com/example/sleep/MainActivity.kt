package com.example.sleep

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import com.example.sleep.ui.screens.MonitoringScreen
import com.example.sleep.ui.theme.SleepTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 权限已获取，可以开始录音
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 请求录音权限
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        
        setContent {
            SleepTheme {
                Surface {
                    MonitoringScreen()
                }
            }
        }
    }
}