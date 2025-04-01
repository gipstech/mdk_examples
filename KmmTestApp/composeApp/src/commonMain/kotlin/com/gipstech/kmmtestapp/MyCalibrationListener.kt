package com.gipstech.kmmtestapp

import com.gipstech.mdk.CalibrationListener
import com.gipstech.mdk.CalibrationManager
import com.gipstech.mdk.LocationListener

class MyCalibrationListener(private val locationListener: LocationListener): CalibrationListener {
    override suspend fun onProgress(percentage: Int, enough: Boolean) {
        LocationManager.locationMessage.value = "Calibration: $percentage%"
        if (enough) {
            LocationManager.locationMessage.value = "Calibration done!"
            CalibrationManager.endCalibration()
            LocationManager.place?.startLocalization(locationListener)
        }
    }
}