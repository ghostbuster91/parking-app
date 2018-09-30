package com.touk.rec.kk.parking.domain

import java.math.BigDecimal
import java.time.LocalDate

interface EarningsCalculator {
    fun getEarnings(date: LocalDate): BigDecimal
}