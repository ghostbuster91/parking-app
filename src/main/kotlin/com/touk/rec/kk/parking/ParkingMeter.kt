package com.touk.rec.kk.parking

interface ParkingMeter {
    fun checkMeter(plateNumber: String): Boolean
    fun startMeter(plateNumber: String)
    fun stopMeter(plateNumber: String)
}