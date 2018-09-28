package com.touk.rec.kk.parking

import java.math.BigDecimal

interface PaymentCalculator {
    fun calculateTotal(meterRecord: ParkingMeterRecord): BigDecimal
}