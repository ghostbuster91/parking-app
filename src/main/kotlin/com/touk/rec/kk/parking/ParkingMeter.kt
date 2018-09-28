package com.touk.rec.kk.parking

interface ParkingMeter {
    fun checkMeter(plateNumber: String): Boolean
    fun startMeter(plateNumber: String, driverType: DriverType)
    fun stopMeter(plateNumber: String)
}