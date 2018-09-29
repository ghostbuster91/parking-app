package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.OperatorGateway
import com.touk.rec.kk.parking.domain.ParkingMeterRepository

class OperatorGatewayImpl(private val repository: ParkingMeterRepository) : OperatorGateway {
    override fun checkMeter(plateNumber: String): Boolean {
        return repository.find(plateNumber)?.isRunning ?: false
    }
}