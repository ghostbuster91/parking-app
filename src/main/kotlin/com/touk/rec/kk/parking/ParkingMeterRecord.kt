package com.touk.rec.kk.parking

import java.time.LocalDateTime

data class ParkingMeterRecord(
        val plateNumber: String,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime?,
        val driverType: DriverType
) {
    val isRunning = endDate == null
}

enum class DriverType{
    REGULAR,
    DISABLED
}