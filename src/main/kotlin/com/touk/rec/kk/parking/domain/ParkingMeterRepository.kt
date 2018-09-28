package com.touk.rec.kk.parking.domain

interface ParkingMeterRepository {
    fun save(parkingMeterRecord: ParkingMeterRecord)
    fun find(plateNumber: String): ParkingMeterRecord?
}