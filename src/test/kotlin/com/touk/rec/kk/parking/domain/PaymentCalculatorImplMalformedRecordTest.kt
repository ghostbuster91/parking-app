package com.touk.rec.kk.parking.domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.impl.PaymentCalculatorImpl
import org.junit.Test
import java.time.LocalDateTime

class PaymentCalculatorImplMalformedRecordTest {

    private val currentTimeProvider = mock<CurrentTimeProvider>()
    private val paymentCalculator = PaymentCalculatorImpl(currentTimeProvider)

    @Test(expected = IllegalArgumentException::class)
    fun `should throw illegal argument exception if start date is after end date`() {
        val startDate = LocalDateTime.now()
        paymentCalculator.calculateTotal(ParkingMeterRecord("wn111", startDate, startDate.minusDays(3), DriverType.DISABLED))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw illegal argument exception if start date is after current date`() {
        val startDate = LocalDateTime.now()
        whenever(currentTimeProvider.getCurrentLocalDateTime()).thenReturn(startDate.minusDays(1))
        paymentCalculator.calculateTotal(ParkingMeterRecord("wn111", startDate, null, DriverType.DISABLED))
    }
}