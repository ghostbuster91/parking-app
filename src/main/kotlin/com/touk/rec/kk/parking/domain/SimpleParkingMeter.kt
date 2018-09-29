package com.touk.rec.kk.parking.domain

class SimpleParkingMeter(
        private val repository: ParkingMeterRepository,
        private val currentTimeProvider: CurrentTimeProvider
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
}