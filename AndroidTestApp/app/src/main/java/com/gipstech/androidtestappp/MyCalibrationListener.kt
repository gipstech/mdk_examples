package com.gipstech.androidtestappp

import androidx.compose.runtime.MutableState
import com.gipstech.mdk.CalibrationListener
import com.gipstech.mdk.CalibrationManager
import com.gipstech.mdk.LocationListener
import com.gipstech.mdk.spatial.Place

class MyCalibrationListener(
    private val place: Place,
    private val locationListener: LocationListener,
    private val locationMessage: MutableState<String>
): CalibrationListener {
    override suspend fun onProgress(percentage: Int, enough: Boolean) {
        locationMessage.value = "Calibration: $percentage%"
        if (enough) {
            locationMessage.value = "Calibration done!"
            CalibrationManager.endCalibration()
            place.startLocalization(locationListener)
        }
    }
}