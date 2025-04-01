package com.gipstech.kmmtestapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(context: Any? = null) {
    LocationManager.init(context)

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            var isRunning by remember { mutableStateOf(false) }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LocationManager.levelMessage.value,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = LocationManager.locationMessage.value,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = LocationManager.geofenceMessage.value,
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = {
                            if (isRunning) {
                                LocationManager.stopLocalization()
                                isRunning = false
                            } else {
                                LocationManager.startLocalization()
                                isRunning = true
                            }
                        },
                        enabled = LocationManager.isButtonEnabled.value
                    ) {
                        Text(if (isRunning) "Stop" else "Start")
                    }
                }
            }
        }
    }
}