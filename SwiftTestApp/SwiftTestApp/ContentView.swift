//
//  ContentView.swift
//  SwiftTestApp
//
//  Created by GiPStech on 25/03/25.
//

import SwiftUI
import GiPStechMDK

struct ContentView: View {
    @State private var isRunning = false
    @StateObject private var manager = LocationManager()
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
                .padding()
            Text(isRunning ? "Localization started" : "Localization halted")
            Text(manager.levelText)
                .foregroundColor(.green)
            Text(manager.locationText)
                .foregroundColor(.blue)
            Text(manager.geofenceText)
                .foregroundColor(.red)
            Button(action: {
                if isRunning {
                    manager.stopLocalization()
                    isRunning = false
                } else {
                    manager.startLocalization()
                    isRunning = true
                }
            }) {
                Text(isRunning ? "Stop" : "Start")
                    .font(.headline)
            }
            .padding()
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
