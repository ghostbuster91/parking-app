package com.touk.rec.kk.parking.domain

class OperatorGateway(private val repository: ParkingMeterRepository) {
    fun checkMeter(plateNumber: String): Boolean {
        return repository.find(plateNumber)?.isRunning ?: false
    }
}