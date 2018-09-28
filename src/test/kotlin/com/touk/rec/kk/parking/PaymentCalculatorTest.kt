package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

@RunWith(Parameterized::class)
class PaymentCalculatorTest(
        private val expectedPrice: BigDecimal,
        private val endTime: LocalDateTime?,
        private val currentTime: LocalDateTime?,
        private val driverType: DriverType
) {

    private val repository = mock<ParkingMeterRepository>()
    private val currentTimeProvider = mock<CurrentTimeProvider> {
        on { getCurrentLocalDateTime() } doReturn (currentTime ?: LocalDateTime.MAX)
    }
    private val paymentCalculator = PaymentCalculator(repository, currentTimeProvider)

    @Test
    fun `should return correct price for given parking time`() {
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime, driverType))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime, driverType))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
        private val startTime = LocalDateTime.MIN

        @Parameterized.Parameters(name = "{index}: price: {0} endTime: {1} currentTime: {2}, driverType: {3}")
        @JvmStatic
        fun data(): List<Array<Any?>> {
            val parametersForRegular = listOf(
                    listOf(bigDecimal(1.0), startTime.plusMinutes(30), null),
                    listOf(bigDecimal(1.0), startTime.plusHours(1), null),
                    listOf(bigDecimal(3.0), startTime.plusMinutes(61), null),
                    listOf(bigDecimal(3.0), startTime.plusHours(2), null),
                    listOf(bigDecimal(6.0), startTime.plusMinutes(121), null),
                    listOf(bigDecimal(6.0), startTime.plusHours(3), null),
                    listOf(bigDecimal(10.5), startTime.plusMinutes(181), null),
                    listOf(bigDecimal(10.5), startTime.plusHours(4), null),
                    listOf(bigDecimal(1.0), null, startTime.plusHours(1))
            )
                    .map { it + DriverType.REGULAR }
            val parametersForDisabled = listOf(
                    listOf(bigDecimal(0.0), startTime.plusMinutes(30), null),
                    listOf(bigDecimal(0.0), startTime.plusHours(1), null),
                    listOf(bigDecimal(2.0), startTime.plusMinutes(61), null),
                    listOf(bigDecimal(2.0), startTime.plusHours(2), null),
                    listOf(bigDecimal(4.4), startTime.plusMinutes(121), null),
                    listOf(bigDecimal(4.4), startTime.plusHours(3), null),
                    listOf(bigDecimal(7.28), startTime.plusMinutes(181), null),
                    listOf(bigDecimal(7.28), startTime.plusHours(4), null),
                    listOf(bigDecimal(0.0), null, startTime.plusHours(1))
            )
                    .map { it + DriverType.DISABLED }

            return (parametersForRegular + parametersForDisabled)
                    .map { it.toList().toTypedArray() }
        }

        private fun bigDecimal(number: Double): BigDecimal {
            return BigDecimal.valueOf(number).setScale(2)
        }
    }
}

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
                    }
                }
                .reduce { first, second -> first + second }
    }

    private fun calculatePriceForDisabled(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> BigDecimal.valueOf(0)
            item in 2..2 -> BigDecimal.valueOf(2)
            else -> acc.last().multiply(BigDecimal.valueOf(1.2))
        }.setScale(2)
    }

    private fun calculatePriceForRegular(item: Int, acc: List<BigDecimal>): BigDecimal {
        return when {
            item <= 1 -> BigDecimal.valueOf(1)
            item in 2..2 -> BigDecimal.valueOf(2)
            else -> acc.last().multiply(BigDecimal.valueOf(1.5))
        }.setScale(2)
    }
}
