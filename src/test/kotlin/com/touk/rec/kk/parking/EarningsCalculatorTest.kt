package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class EarningsCalculatorTest {

    @Test
    fun `earnings should be equal to zero if no one has started parking meter till given day`() {
        val calculator = EarningsCalculator()
        assert(calculator.getEarnings(LocalDate.MIN)).isEqualTo(BigDecimal.ZERO)
    }
}

class EarningsCalculator {
    fun getEarnings(date: LocalDate): BigDecimal {
        return BigDecimal.ZERO
    }
}