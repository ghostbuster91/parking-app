package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import com.touk.rec.kk.parking.domain.ParkingMeterRepository
import com.touk.rec.kk.parking.domain.impl.EarningsCalculatorImpl
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ParkingMeterRepositoryImpl(private val persistantRepository: ParkingMeterPersistantRepository)
    : ParkingMeterRepository, EarningsCalculatorImpl.ParkingMeterRepository {

    override fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord> {
        return persistantRepository.findAll().filter { !it.isRunning() && it.startDate.toLocalDate() == date }
    }

    override fun save(parkingMeterRecord: ParkingMeterRecord) {
        persistantRepository.save(parkingMeterRecord)
    }

    override fun find(plateNumber: String): ParkingMeterRecord? {
        val optionalRecord = persistantRepository.findById(plateNumber)
        return if (optionalRecord.isPresent) {
            optionalRecord.get()
        } else {
            null
        }
    }
}