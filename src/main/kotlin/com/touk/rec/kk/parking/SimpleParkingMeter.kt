package com.touk.rec.kk.parking

class SimpleParkingMeter(
        private val repository: ParkingMeterRepository,
        private val currentTimeProvider: CurrentTimeProvider
) : ParkingMeter {

    override fun startMeter(plateNumber: String, driverType: DriverType) {
        check(!checkMeter(plateNumber))
        repository.save(ParkingMeterRecord(plateNumber, currentTimeProvider.getCurrentLocalDateTime(), null, driverType))
    }

    override fun checkMeter(plateNumber: String): Boolean {
        val meterRecord = repository.find(plateNumber)
        return meterRecord?.isRunning ?: false
    }

    override fun stopMeter(plateNumber: String) {
        val meterRecord = repository.find(plateNumber)
        require(meterRecord?.isRunning ?: false)
        repository.save(meterRecord!!.copy(endDate = currentTimeProvider.getCurrentLocalDateTime()))
    }
}