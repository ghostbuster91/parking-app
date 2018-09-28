package com.touk.rec.kk.parking

import java.math.BigDecimal
import java.time.LocalDate

class EarningsCalculator(
        private val repository: ParkingMeterRepository,
        private val paymentCalculator: PaymentCalculator
) {
    fun getEarnings(date: LocalDate): BigDecimal {
        val completedRecords = repository.getCompletedRecords(date)
        return completedRecords
                .map { paymentCalculator.calculateTotal(it) }
                .fold(BigDecimal.ZERO) { acc, item -> acc + item }
    }

    interface ParkingMeterRepository {
        fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord>
    }
}