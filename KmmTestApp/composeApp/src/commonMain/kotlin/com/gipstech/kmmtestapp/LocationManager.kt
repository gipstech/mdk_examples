package com.gipstech.kmmtestapp

import androidx.compose.runtime.mutableStateOf
import com.gipstech.mdk.GiPStech
import com.gipstech.mdk.LocationSession
import com.gipstech.mdk.SpatialManager
import com.gipstech.mdk.spatial.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object LocationManager {
    private const val DEV_KEY = // Your Development Key
    private const val BUILDING_ID = // Your Building Id
    private const val UNIVERSAL = true // Set to false for indoor localization

    var place: Place? = null
    val isButtonEnabled = mutableStateOf(false)
    val levelMessage = mutableStateOf("")
    val locationMessage = mutableStateOf("Starting...")
    val geofenceMessage = mutableStateOf("")
    private var session: LocationSession? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)

    fun init(context: Any?) {
        activityScope.launch {
            try {
                GiPStech.init(DEV_KEY, null, context)
                locationMessage.value = "Ready"
                isButtonEnabled.value = true
            } catch (e: Exception) {
                locationMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun startLocalization() {
        levelMessage.value = ""
        locationMessage.value = ""
        geofenceMessage.value = ""

        activityScope.launch {
            try {
                if (UNIVERSAL) {
                    startUniversalLocalization()
                } else {
                    startIndooLocalization()
                }
            } catch (e: Exception) {
                locationMessage.value = "Error: ${e.message}"
                isButtonEnabled.value = false
            }
        }
    }

    private suspend fun startIndooLocalization() {
        val building = SpatialManager.requestBuilding(BUILDING_ID)
        val listener = MyLocationListener()
        listener.onBuildingChanged(building)

        place = building
        session = building.startLocalization(listener)
    }

    private suspend fun startUniversalLocalization() {
        val location = SpatialManager.getLocationFromOS()

        if (location != null) {
            val (min, max) = location.getBoundingBox(1000.0)
            val region = SpatialManager.requestRegion(min, max)
            val listener = MyLocationListener()

            place = region
            session = region.startLocalization(listener)
        } else {
            locationMessage.value = "Position not available"
        }
    }

    fun stopLocalization() {
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
