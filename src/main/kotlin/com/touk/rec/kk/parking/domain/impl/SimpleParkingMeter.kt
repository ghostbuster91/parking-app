package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.*
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class SimpleParkingMeter(
        private val repository: ParkingMeterRepository,
        private val currentTimeProvider: CurrentTimeProvider,
        private val paymentCalculator: PaymentCalculator
) : ParkingMeter {

    override fun startMeter(plateNumber: String, driverType: DriverType) {
        check(repository.find(plateNumber)?.isRunning?.not() ?: true)
        repository.save(ParkingMeterRecord(plateNumber, currentTimeProvider.getCurrentLocalDateTime(), null, driverType))
    }

    override fun stopMeter(plateNumber: String) {
        val meterRecord = repository.find(plateNumber)
        require(meterRecord?.isRunning ?: false)
        repository.save(meterRecord!!.copy(endDate = currentTimeProvider.getCurrentLocalDateTime()))
    }

    override fun getTotalCost(plateNumber: String): BigDecimal {
        return repository.find(plateNumber)
                ?.let { paymentCalculator.calculateTotal(it) }
                ?: BigDecimal.ZERO.setScale(2)
    }
}