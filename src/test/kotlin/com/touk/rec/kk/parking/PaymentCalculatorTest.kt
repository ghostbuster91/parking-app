package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
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
        private val endTime: LocalDateTime
) {

    private val repository = mock<ParkingMeterRepository>()
    private val paymentCalculator = PaymentCalculator(repository)

    @Test
    fun `should return correct price for given parking time`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(expectedPrice)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"

        @Parameterized.Parameters
        @JvmStatic
        fun data(): List<Array<Any>> {
            val startTime = LocalDateTime.MIN
            return listOf(
                    BigDecimal(1) to startTime.plusMinutes(30),
                    BigDecimal(1) to startTime.plusHours(1),
                    BigDecimal(3) to startTime.plusMinutes(61),
                    BigDecimal(3) to startTime.plusHours(2),
                    BigDecimal(6) to startTime.plusMinutes(121),
                    BigDecimal(6) to startTime.plusHours(3),
                    BigDecimal(10.5) to startTime.plusMinutes(181),
                    BigDecimal(10.5) to startTime.plusHours(4)
            )
                    .map { it.copy(first = it.first.setScale(2)) }
                    .map { it.toList().toTypedArray() }
        }
    }
}

class PaymentCalculator(private val repository: ParkingMeterRepository) {
    fun calculateTotal(plateNumber: String): BigDecimal {
        val meterRecord = repository.find(plateNumber) ?: return BigDecimal.ZERO
        val totalHours = Duration.between(meterRecord.startDate, meterRecord.endDate!!.plusMinutes(59)).toHours().toInt()
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
