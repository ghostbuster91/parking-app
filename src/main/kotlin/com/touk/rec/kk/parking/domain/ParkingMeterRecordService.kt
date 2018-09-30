package com.touk.rec.kk.parking.domain

interface ParkingMeterRecordService {
    fun save(parkingMeterRecord: ParkingMeterRecord)
    fun find(plateNumber: String): ParkingMeterRecord?
}