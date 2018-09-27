package com.touk.rec.kk.parking

interface ParkingMeterRepository {
    fun save(parkingMeterRecord: ParkingMeterRecord)
    fun find(plateNumber: String): ParkingMeterRecord?
}