//
//  LocationManager.swift
//  SwiftTestApp
//
//  Created by GiPStech on 25/03/25.
//

import SwiftUI
import GiPStechMDK

class LocationManager: ObservableObject {
    let DEV_KEY = // Your Development Key
    let BUILDING_ID = // Your Building Id
    let UNIVERSAL = true // Set to false for indoor localization

    var session: LocationSession?

    @Published var levelText: String = ""
    @Published var geofenceText: String = ""
    @Published var locationText: String = "Ready"

    public func report(_ message: String) {
        DispatchQueue.main.async {
            self.locationText = message
        }
    }

    public func reportLevel(_ message: String) {
        DispatchQueue.main.async {
            self.levelText = message
        }
    }

    public func reportGeofence(_ message: String) {
        DispatchQueue.main.async {
            self.geofenceText = message
        }
    }

    func restartLocalization() async throws {
        do {
            let locationListener = MyLocationListener(manager: self)
            session = try await session?.target.startLocalization(listener: locationListener)
        } catch let error as NSError {
            report("Error: \(error.localizedDescription)")
        }
    }

    func startLocalization() {
        Task {
            do {
                if !(GiPStech.shared.isInitialized) {
                    _ = try await GiPStech.shared.doInit(devKey: DEV_KEY)
                }

                if UNIVERSAL {
                    try await startUniversalLocalization()
                } else {
                    try await startIndoorLocalization()
                }
            } catch let error as NSError {
                let ex = error.kotlinException as? GiPStechException
                report("Error: \(ex?.message ?? "Unknown error")")
            }
        }
    }

    func startIndoorLocalization() async throws {
        let building = try await SpatialManager.shared.requestBuilding(buildingId: BUILDING_ID, fallback: true)
        let locationListener = MyLocationListener(manager: self)
        try await locationListener.onBuildingChanged(building: building)

        session = try await building.startLocalization(listener: locationListener)
    }

    func startUniversalLocalization() async throws {
        if let location = try await SpatialManager.shared.getLocationFromOS() {
            let pair = location.getBoundingBox(size: 100)
            let region = try await SpatialManager.shared.requestRegion(southWest: pair.first!, northEast: pair.second!, fallback: true)
            let locationListener = MyLocationListener(manager: self)

            session = try await region.startLocalization(listener: locationListener)
        }
    }

    func stopLocalization() {
        Task {
            try await session?.target.stopLocalization()
        }
    }
}
