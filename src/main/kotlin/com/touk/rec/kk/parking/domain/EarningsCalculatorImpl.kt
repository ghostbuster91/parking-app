package com.touk.rec.kk.parking.domain

import java.math.BigDecimal
import java.time.LocalDate

class EarningsCalculatorImpl(
        private val repository: ParkingMeterRepository,
        private val paymentCalculator: PaymentCalculator
) : EarningsCalculator {
    override fun getEarnings(date: LocalDate): BigDecimal {
        val completedRecords = repository.getCompletedRecords(date)
        return completedRecords
                .map { paymentCalculator.calculateTotal(it) }
                .fold(BigDecimal.ZERO) { acc, item -> acc + item }
    }

    interface ParkingMeterRepository {
        fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord>
    }
}