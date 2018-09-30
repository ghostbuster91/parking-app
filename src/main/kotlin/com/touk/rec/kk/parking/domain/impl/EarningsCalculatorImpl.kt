package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.EarningsCalculator
import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import com.touk.rec.kk.parking.domain.PaymentCalculator
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class EarningsCalculatorImpl(
        private val recordService: ParkingMeterRecordService,
        private val paymentCalculator: PaymentCalculator
) : EarningsCalculator {
    override fun getEarnings(date: LocalDate): BigDecimal {
        val completedRecords = recordService.getCompletedRecords(date)
        return completedRecords
                .map { paymentCalculator.calculateTotal(it) }
                .fold(BigDecimal.ZERO) { acc, item -> acc + item }
    }

    interface ParkingMeterRecordService {
        fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord>
    }
}