package com.touk.rec.kk.parking.domain

class OperatorGatewayImpl(private val repository: ParkingMeterRepository) : OperatorGateway {
    override fun checkMeter(plateNumber: String): Boolean {
        return repository.find(plateNumber)?.isRunning ?: false
    }
}