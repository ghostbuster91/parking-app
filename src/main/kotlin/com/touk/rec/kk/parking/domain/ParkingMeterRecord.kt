package com.touk.rec.kk.parking.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ParkingMeterRecord(
        @Id
        val plateNumber: String,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime?,
        val driverType: DriverType
) {

    fun isRunning() = endDate == null
}

enum class DriverType {
    REGULAR,
    DISABLED
}