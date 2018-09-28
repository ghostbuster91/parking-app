package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class EarningsCalculatorTest {
    private val repository = mock<EarningsCalculator.ParkingMeterRepository>()
    private val paymentCalculator = mock<PaymentCalculator>()
    private val calculator = EarningsCalculator(repository, paymentCalculator)

    @Test
    fun `earnings should be equal to zero if there are no completed parking meter records for given day`() {
        whenever(repository.getCompletedRecords(LocalDate.MIN)).thenReturn(emptyList())
        assert(calculator.getEarnings(LocalDate.MIN)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return proper earnings for single parking meter record`() {
        val meterRecord = ParkingMeterRecord("wn1111", LocalDateTime.MIN, LocalDateTime.MIN.plusHours(3), DriverType.REGULAR)
        whenever(repository.getCompletedRecords(LocalDate.MIN)).thenReturn(listOf(meterRecord))
        whenever(paymentCalculator.calculateTotal(meterRecord)).thenReturn(BigDecimal.TEN)
        assert(calculator.getEarnings(LocalDate.MIN)).isEqualTo(BigDecimal.TEN)
    }
}

class EarningsCalculator(
        private val repository: ParkingMeterRepository,
        private val paymentCalculator: PaymentCalculator
) {
    fun getEarnings(date: LocalDate): BigDecimal {
        val completedRecords = repository.getCompletedRecords(date)
        return completedRecords.firstOrNull()?.let { paymentCalculator.calculateTotal(it) } ?: BigDecimal.ZERO
    }

    interface ParkingMeterRepository {
        fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord>
    }
}