package com.touk.rec.kk.parking

import java.math.BigDecimal
import java.time.Duration

class PaymentCalculator(
        private val repository: ParkingMeterRepository,
        private val currentTimeProvider: CurrentTimeProvider
) {
    fun calculateTotal(plateNumber: String): BigDecimal {
        val meterRecord = repository.find(plateNumber) ?: return BigDecimal.ZERO
        val upperTimeLimit = (meterRecord.endDate ?: currentTimeProvider.getCurrentLocalDateTime()).plusMinutes(59)
        val totalHours = Duration.between(meterRecord.startDate, upperTimeLimit).toHours().toInt()
        return (1..totalHours)
                .fold(listOf<BigDecimal>()) { acc, item ->
                    acc + when (meterRecord.driverType) {
                        DriverType.REGULAR -> calculatePriceForRegular(item, acc)
                        DriverType.DISABLED -> calculatePriceForDisabled(item, acc)
                    }.setScale(2)
                }
                .reduce { first, second -> first + second }
    }

    private fun calculatePriceForDisabled(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> BigDecimal.valueOf(0)
            item in 2..2 -> BigDecimal.valueOf(2)
            else -> acc.last().multiply(BigDecimal.valueOf(1.2))
        }
    }

    private fun calculatePriceForRegular(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> BigDecimal.valueOf(1)
            item in 2..2 -> BigDecimal.valueOf(2)
            else -> acc.last().multiply(BigDecimal.valueOf(1.5))
        }
    }
}