package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.*
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class SimpleParkingMeter(
        private val recordService: ParkingMeterRecordService,
        private val currentTimeProvider: CurrentTimeProvider,
        private val paymentCalculator: PaymentCalculator
) : ParkingMeter {

    override fun startMeter(plateNumber: String, driverType: DriverType) {
        check(recordService.find(plateNumber)?.isRunning()?.not() ?: true)
        recordService.save(ParkingMeterRecord(plateNumber, currentTimeProvider.getCurrentLocalDateTime(), null, driverType))
    }

    override fun stopMeter(plateNumber: String) {
        val meterRecord = recordService.find(plateNumber)
        require(meterRecord?.isRunning() ?: false)
        recordService.save(meterRecord!!.copy(endDate = currentTimeProvider.getCurrentLocalDateTime()))
    }

    override fun getTotalCost(plateNumber: String): BigDecimal {
        return recordService.find(plateNumber)
                ?.let { paymentCalculator.calculateTotal(it) }
                ?: BigDecimal.ZERO.setScale(2)
    }
}