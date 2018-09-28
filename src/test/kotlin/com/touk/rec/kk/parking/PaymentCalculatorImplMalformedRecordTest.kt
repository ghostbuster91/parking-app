package com.touk.rec.kk.parking

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import java.time.LocalDateTime

class PaymentCalculatorImplMalformedRecordTest {

    private val paymentCalculator = PaymentCalculatorImpl(mock())

    @Test(expected = IllegalArgumentException::class)
    fun `should throw illegal argument exception if start date is after end date`() {
        val startDate = LocalDateTime.now()
        paymentCalculator.calculateTotal(ParkingMeterRecord("wn111", startDate, startDate.minusDays(3), DriverType.DISABLED))
    }
}