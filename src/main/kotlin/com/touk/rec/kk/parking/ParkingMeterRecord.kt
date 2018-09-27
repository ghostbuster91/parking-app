package com.touk.rec.kk.parking

import java.time.LocalDateTime

data class ParkingMeterRecord(
        val plateNumber: String,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime?
) {
    val isRunning = endDate == null
}