package com.gipstech.androidtestappp

import androidx.compose.runtime.MutableState
import com.gipstech.mdk.CalibrationManager
import com.gipstech.mdk.CalibrationType
import com.gipstech.mdk.GiPStechException
import com.gipstech.mdk.Location
import com.gipstech.mdk.LocationListener
import com.gipstech.mdk.spatial.Building
import com.gipstech.mdk.spatial.Place
import com.gipstech.mdk.spatial.Region

class MyLocationListener(
    private val place: Place,
    private val levelMessage: MutableState<String>,
    private val locationMessage: MutableState<String>,
    private val geofenceMessage: MutableState<String>,
): LocationListener {
    private var currentBuilding: Building? = null

    override suspend fun onCalibrationRequest(types: Array<CalibrationType>) {
        locationMessage.value = "Calibration required!\nPlease rotate the phone to start the calibration process."
        CalibrationManager.beginCalibration(types[0], MyCalibrationListener(place, this, locationMessage))
    }

    override suspend fun onException(exception: GiPStechException) {
        locationMessage.value = "Error: ${exception.message}"
    }

    override suspend fun onBuildingChanged(building: Building?) {
        currentBuilding = building
        if (building != null) {
            levelMessage.value = "Building: ${building.name}"
        } else {
            levelMessage.value = "OUTDOOR"
        }
    }

    override suspend fun onLevelChanged(level: Int) {
        levelMessage.value = currentBuilding?.let {
            "Building: ${it.name}, Level: ${it.floors[level].name}"
        } ?: "OUTDOOR, Level: $level"
    }

    override suspend fun onLocationUpdated(location: Location) {
        locationMessage.value = "Location: ${location.longitude} ${location.latitude} [${location.steps}]"

        (place as? Region)?.let { region ->
            if (region.isNearOrOutsideBorder(location, 100.0)) {
                val (sw, ne) = location.getBoundingBox(1000.0, 1000.0)
                region.requestAdditionalArea(sw, ne)
            }
        }
    }

    override suspend fun onGeofenceEnter(name: String, confidence: Float) {
        geofenceMessage.value = "Enter: $name"
    }

    override suspend fun onGeofenceExit(name: String, confidence: Float) {
        geofenceMessage.value = "Exit: $name"
    }
}