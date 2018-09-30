package com.touk.rec.kk.parking.domain

import java.math.BigDecimal

interface ParkingMeter {
    fun startMeter(plateNumber: String, driverType: DriverType)
    fun stopMeter(plateNumber: String)
    fun getTotalCost(plateNumber: String): BigDecimal
}