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
        private val currentTime: LocalDateTime?
) {

    private val repository = mock<ParkingMeterRepository>()
    private val currentTimeProvider = mock<CurrentTimeProvider> {
        on { getCurrentLocalDateTime() } doReturn (currentTime ?: LocalDateTime.MAX)
    }
    private val paymentCalculator = PaymentCalculator(repository, currentTimeProvider)

    @Test
    fun `should return correct price for given parking time`() {
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
        private val startTime = LocalDateTime.MIN

        @Parameterized.Parameters(name = "{index}: price: {0} endTime: {1} currentTime: {2}")
        @JvmStatic
        fun data(): List<Array<Any?>> {
            return listOf(
                    Triple(BigDecimal(1), startTime.plusMinutes(30), null),
                    Triple(BigDecimal(1), startTime.plusHours(1), null),
                    Triple(BigDecimal(3), startTime.plusMinutes(61), null),
                    Triple(BigDecimal(3), startTime.plusHours(2), null),
                    Triple(BigDecimal(6), startTime.plusMinutes(121), null),
                    Triple(BigDecimal(6), startTime.plusHours(3), null),
                    Triple(BigDecimal(10.5), startTime.plusMinutes(181), null),
                    Triple(BigDecimal(10.5), startTime.plusHours(4), null),
                    Triple(BigDecimal(1), null, startTime.plusHours(1))
            )
                    .map { it.copy(first = it.first.setScale(2)) }
                    .map { it.toList().toTypedArray() }
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
                    acc + when {
                        item <= 1 -> BigDecimal(1)
                        item in 2..2 -> BigDecimal(2)
                        else -> acc.last().multiply(BigDecimal(1.5))
                    }.setScale(2)
                }
                .reduce { first, second -> first + second }
    }
}
