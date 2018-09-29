package com.touk.rec.kk.parking.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
data class ParkingMeterRecord(
        @Id
        val plateNumber: String,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime?,
        val driverType: DriverType
) {
    @Transient
    val isRunning = endDate == null
}

enum class DriverType {
    REGULAR,
    DISABLED
}