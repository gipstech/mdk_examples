//
//  MyLocationListener.swift
//  SwiftTestApp
//
//  Created by GiPStech on 25/03/25.
//

import SwiftUI
import GiPStechMDK

class MyLocationListener: LocationListener {
    var currentBuilding: Building?
    var manager: LocationManager
    
    init(manager: LocationManager) {
        self.manager = manager
    }
    
    func __onBuildingChanged(building: Building?) async throws {
        currentBuilding = building
        manager.reportLevel("Building: \(currentBuilding?.name ?? "OUTDOOR")")
    }
    
    func __onCalibrationRequest(types: KotlinArray<__CalibrationType>) async throws {
        manager.report("Calibration required!\nPlease rotate the phone to start the calibration process.")
        try await CalibrationManager.shared.beginCalibration(
            type: types.get(index: 0)! as CalibrationType,
            listener: MyCalibrationListener(manager: manager))
    }
    
    func __onException(exception: GiPStechException) async throws {
        manager.report("Error: \(exception.message ?? "Unknown error")")
    }
    
    func __onGeofenceEnter(name: String, confidence: Float) async throws {
        manager.reportGeofence("Enter: \(name)")
    }
    
    func __onGeofenceExit(name: String, confidence: Float) async throws {
        manager.reportGeofence("Exit: \(name)")
    }
    
    func __onLevelChanged(level: Int32) async throws {
        if let building = currentBuilding {
            let floor = building.floors.get(index: level)
            manager.reportLevel("Building: \(building.name), Level: \(floor!.name)")
        } else {
            manager.reportLevel("OUTDOOR, Level: \(level)")
        }
    }
    
    func __onLocationUpdated(location: Location) async throws {
        if let region = manager.session?.target as? Region {
            if region.isNearOrOutsideBorder(coordinates: location, distance: 100.0) {
                let pair = location.getBoundingBox(size: 100)
                try await region.requestAdditionalArea(southWest: pair.first!, northEast: pair.second!, fallback: true)
            }
        }
        
        manager.report("Location: \(location.latitude) \(location.longitude) [\(location.steps)]")
    }
}
