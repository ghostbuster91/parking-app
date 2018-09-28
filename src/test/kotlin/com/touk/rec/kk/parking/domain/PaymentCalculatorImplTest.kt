package com.touk.rec.kk.parking.domain

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.time.LocalDateTime

@RunWith(Parameterized::class)
class PaymentCalculatorImplTest(
        private val expectedPrice: BigDecimal,
        private val endTime: LocalDateTime?,
        private val currentTime: LocalDateTime?,
        private val driverType: DriverType
) {

    private val currentTimeProvider = mock<CurrentTimeProvider> {
        on { getCurrentLocalDateTime() } doReturn (currentTime ?: LocalDateTime.MAX)
    }
    private val paymentCalculator = PaymentCalculatorImpl(currentTimeProvider)

    @Test
    fun `should return correct price for given parking time`() {
        val meterRecord = ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, endTime, driverType)
        assert(paymentCalculator.calculateTotal(meterRecord))
                .isEqualTo(expectedPrice)
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