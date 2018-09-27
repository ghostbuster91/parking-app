package com.touk.rec.kk.parking

interface ParkingManager {
    fun checkMeter(plateNumber: String): Boolean
}