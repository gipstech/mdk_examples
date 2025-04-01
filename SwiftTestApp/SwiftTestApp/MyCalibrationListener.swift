//
//  MyCalibrationListener.swift
//  SwiftTestApp
//
//  Created by GiPStech on 25/03/25.
//

import GiPStechMDK

class MyCalibrationListener: CalibrationListener {
    var manager: LocationManager
    
    init(manager: LocationManager) {
        self.manager = manager
    }
    
    func __onProgress(percentage: Int32, enough: Bool) async throws {
        manager.report("Calibration \(percentage)%")
        if enough {
            manager.report("Calibration done!")
            try await CalibrationManager.shared.endCalibration()
            try await manager.restartLocalization()
        }
    }
}
