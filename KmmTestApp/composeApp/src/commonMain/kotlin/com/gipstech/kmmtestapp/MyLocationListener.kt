package com.gipstech.kmmtestapp

import com.gipstech.mdk.CalibrationManager
import com.gipstech.mdk.CalibrationType
import com.gipstech.mdk.GiPStechException
import com.gipstech.mdk.Location
import com.gipstech.mdk.LocationListener
import com.gipstech.mdk.spatial.Building
import com.gipstech.mdk.spatial.Region

class MyLocationListener: LocationListener {
    private var currentBuilding: Building? = null

    override suspend fun onCalibrationRequest(types: Array<CalibrationType>) {
        LocationManager.locationMessage.value = "Calibration required!\nPlease rotate the phone to start the calibration process."
        CalibrationManager.beginCalibration(types[0], MyCalibrationListener(this))
    }

    override suspend fun onException(exception: GiPStechException) {
        LocationManager.locationMessage.value = "Error: ${exception.message}"
    }

    override suspend fun onBuildingChanged(building: Building?) {
        currentBuilding = building
        if (building != null) {
            LocationManager.levelMessage.value = "Building: ${building.name}"
        } else {
            LocationManager.levelMessage.value = "OUTDOOR"
        }
    }

    override suspend fun onLevelChanged(level: Int) {
        LocationManager.levelMessage.value = currentBuilding?.let {
             "Building: ${it.name}, Level: ${it.floors[level].name}"
        } ?: "OUTDOOR, Level: $level"
    }

    override suspend fun onLocationUpdated(location: Location) {
        LocationManager.locationMessage.value = "Location: ${location.longitude} ${location.latitude} [${location.steps}]"

        (LocationManager.place as? Region)?.let { region ->
            if (region.isNearOrOutsideBorder(location, 100.0)) {
                val (sw, ne) = location.getBoundingBox(1000.0, 1000.0)
                region.requestAdditionalArea(sw, ne)
            }
        }
    }

    override suspend fun onGeofenceEnter(name: String, confidence: Float) {
        LocationManager.geofenceMessage.value = "Enter: $name"
    }

    override suspend fun onGeofenceExit(name: String, confidence: Float) {
        LocationManager.geofenceMessage.value = "Exit: $name"
    }
}