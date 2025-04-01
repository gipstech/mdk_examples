package com.gipstech.androidtestappp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gipstech.androidtestappp.ui.theme.AndroidTestApppTheme
import com.gipstech.mdk.GiPStech
import com.gipstech.mdk.LocationSession
import com.gipstech.mdk.SpatialManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val DEV_KEY = // Your Development Key
        private const val BUILDING_ID = // Your Building Id
        private const val UNIVERSAL = true // Set to false for indoor localization
    }

    private var session: LocationSession? = null
    private val isButtonEnabled = mutableStateOf(false)
    private val levelMessage = mutableStateOf("")
    private val locationMessage = mutableStateOf("Starting...")
    private val geofenceMessage = mutableStateOf("")
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        activityScope.launch {
            try {
                GiPStech.init("FE041A0609C1BA931AEE066E4C30C3C1", context=this@MainActivity)
                locationMessage.value = "Ready"
                isButtonEnabled.value = true
            } catch (e: Exception) {
                locationMessage.value = "Error: ${e.message}"
            }
        }

        PermissionHelper.checkPermissions(this) { allGranted ->
            if (allGranted) {
                setContent {
                    AndroidTestApppTheme {
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
                                    text = levelMessage.value,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = locationMessage.value,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = geofenceMessage.value,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Button(
                                    onClick = {
                                        if (isRunning) {
                                            stopLocalization()
                                            isRunning = false
                                        } else {
                                            startLocalization()
                                            isRunning = true
                                        }
                                    },
                                    enabled = isButtonEnabled.value
                                ) {
                                    Text(if (isRunning) "Stop" else "Start")
                                }
                            }
                        }
                    }
                }
            } else {
                setContent {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "This app requires all permissions to work")
                    }
                }
            }
        }
    }

    private fun startLocalization() {
        try {
            activityScope.launch {
                if (UNIVERSAL) {
                    startUniversalLocalization()
                } else {
                    startIndooLocalization()
                }
            }
        } catch (e: Exception) {
            locationMessage.value = "Error: ${e.message}"
            isButtonEnabled.value = false
        }
    }

    private suspend fun startIndooLocalization() {
        val building = SpatialManager.requestBuilding(BUILDING_ID)
        val listener = MyLocationListener(building, levelMessage, locationMessage, geofenceMessage)
        listener.onBuildingChanged(building)

        session = building.startLocalization(listener)
    }

    private suspend fun startUniversalLocalization() {
        val location = SpatialManager.getLocationFromOS()

        if (location != null) {
            val (min, max) = location.getBoundingBox(1000.0)
            val region = SpatialManager.requestRegion(min, max)
            val listener = MyLocationListener(region, levelMessage, locationMessage, geofenceMessage)

            session = region.startLocalization(listener)
        } else {
            locationMessage.value = "Position not available"
        }
    }

    private fun stopLocalization() {
        activityScope.launch {
            try {
                session!!.target.stopLocalization()
            } catch (e: Exception) {
                locationMessage.value = "Error: ${e.message}"
                isButtonEnabled.value = false
            }
        }
    }
}
