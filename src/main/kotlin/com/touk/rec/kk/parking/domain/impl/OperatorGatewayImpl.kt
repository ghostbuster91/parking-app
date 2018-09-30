package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.OperatorGateway
import com.touk.rec.kk.parking.domain.ParkingMeterRecordService
import org.springframework.stereotype.Component

@Component
class OperatorGatewayImpl(private val recordService: ParkingMeterRecordService) : OperatorGateway {
    override fun checkMeter(plateNumber: String): Boolean {
        return recordService.find(plateNumber)?.isRunning() ?: false
    }
}