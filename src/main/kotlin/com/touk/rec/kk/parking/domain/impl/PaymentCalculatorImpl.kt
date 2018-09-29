package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.CurrentTimeProvider
import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import com.touk.rec.kk.parking.domain.PaymentCalculator
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

@Component
class PaymentCalculatorImpl(
        private val currentTimeProvider: CurrentTimeProvider
) : PaymentCalculator {
    override fun calculateTotal(meterRecord: ParkingMeterRecord): BigDecimal {
        val currentTime = currentTimeProvider.getCurrentLocalDateTime()
        with(meterRecord) { require(startDate.isBefore(endDate ?: currentTime)) }
        val upperTimeLimit = (meterRecord.endDate ?: currentTime).plusMinutes(59)
        val totalHours = Duration.between(meterRecord.startDate, upperTimeLimit).toHours().toInt()
        return (1..totalHours)
                .fold(listOf<BigDecimal>()) { acc, item ->
                    acc + when (meterRecord.driverType) {
                        DriverType.REGULAR -> calculatePriceForRegular(item, acc)
                        DriverType.DISABLED -> calculatePriceForDisabled(item, acc)
                    }
                }
                .fold(BigDecimal.ZERO) { first, second -> first + second }
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