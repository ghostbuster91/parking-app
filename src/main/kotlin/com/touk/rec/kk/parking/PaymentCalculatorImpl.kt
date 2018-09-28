package com.touk.rec.kk.parking

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

class PaymentCalculatorImpl(
        private val currentTimeProvider: CurrentTimeProvider
) : PaymentCalculator {
    override fun calculateTotal(meterRecord: ParkingMeterRecord): BigDecimal {
        with(meterRecord) { require(startDate.isBefore(endDate)) }
        val upperTimeLimit = (meterRecord.endDate ?: currentTimeProvider.getCurrentLocalDateTime()).plusMinutes(59)
        val totalHours = Duration.between(meterRecord.startDate, upperTimeLimit).toHours().toInt()
        return (1..totalHours)
                .fold(listOf<BigDecimal>()) { acc, item ->
                    acc + when (meterRecord.driverType) {
                        DriverType.REGULAR -> calculatePriceForRegular(item, acc)
                        DriverType.DISABLED -> calculatePriceForDisabled(item, acc)
                    }
                }
                .reduce { first, second -> first + second }
                .setScale(2, RoundingMode.HALF_EVEN)
    }

    private fun calculatePriceForDisabled(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> bigDecimal(0.0)
            item in 2..2 -> bigDecimal(2.0)
            else -> acc.last().multiply(bigDecimal(1.2))
        }
    }

    private fun calculatePriceForRegular(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> bigDecimal(1.0)
            item in 2..2 -> bigDecimal(2.0)
            else -> acc.last().multiply(bigDecimal(1.5))
        }
    }

    private fun bigDecimal(value: Double) = BigDecimal.valueOf(value)
}