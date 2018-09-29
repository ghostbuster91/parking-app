package com.touk.rec.kk.parking.domain

interface ParkingMeter {
    fun startMeter(plateNumber: String, driverType: DriverType)
    fun stopMeter(plateNumber: String)
}