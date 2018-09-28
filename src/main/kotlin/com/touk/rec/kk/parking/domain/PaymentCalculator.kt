package com.touk.rec.kk.parking.domain

import java.math.BigDecimal

interface PaymentCalculator {
    fun calculateTotal(meterRecord: ParkingMeterRecord): BigDecimal
}