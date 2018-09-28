package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class EarningsCalculatorTest {
    private val repository = mock<EarningsCalculator.ParkingMeterRepository>()
    private val calculator = EarningsCalculator(repository)

    @Test
    fun `earnings should be equal to zero if there are no completed parking meter records for given day`() {
        whenever(repository.getCompletedRecords(LocalDate.MIN)).thenReturn(emptyList())
        assert(calculator.getEarnings(LocalDate.MIN)).isEqualTo(BigDecimal.ZERO)
    }
}

class EarningsCalculator(private val repository: ParkingMeterRepository) {
    fun getEarnings(date: LocalDate): BigDecimal {
        return BigDecimal.ZERO
    }

    interface ParkingMeterRepository {
        fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord>
    }
}