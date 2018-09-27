package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class PaymentCalculatorTest {

    private val repository = mock<ParkingMeterRepository>()
    private val paymentCalculator = PaymentCalculator(repository)

    @Test
    fun `should return zero for driver who didn't start parking meter`() {
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return 1 PLN if parking time is less or equal to one hour for completed parking`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(30)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ONE)

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(60)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ONE)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
    }
}

class PaymentCalculator(private val repository: ParkingMeterRepository) {
    fun calculateTotal(plateNumber: String): BigDecimal {
        return if (repository.find(plateNumber) != null) BigDecimal.ONE else BigDecimal.ZERO
    }
}
