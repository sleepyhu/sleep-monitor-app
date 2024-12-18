package com.example.sleep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleep.viewmodels.SleepMonitorViewModel

@Composable
fun MonitoringScreen(
    viewModel: SleepMonitorViewModel = viewModel()
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val currentEvent by viewModel.currentEvent.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Current Event: ${currentEvent.name}",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (isRecording) {
                    viewModel.stopMonitoring()
                } else {
                    viewModel.startMonitoring()
                }
            }
        ) {
            Text(if (isRecording) "Stop Monitoring" else "Start Monitoring")
        }
    }
} 