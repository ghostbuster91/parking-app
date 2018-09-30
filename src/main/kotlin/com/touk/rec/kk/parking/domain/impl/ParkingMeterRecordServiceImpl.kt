package com.touk.rec.kk.parking.domain.impl

import com.touk.rec.kk.parking.domain.ParkingMeterPersistentRepository
import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import com.touk.rec.kk.parking.domain.ParkingMeterRecordService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ParkingMeterRecordServiceImpl(private val persistentRepository: ParkingMeterPersistentRepository)
    : ParkingMeterRecordService, EarningsCalculatorImpl.ParkingMeterRecordService {

    override fun getCompletedRecords(date: LocalDate): List<ParkingMeterRecord> {
        return persistentRepository.findAll().filter { it.endDate != null && it.endDate.toLocalDate() == date }
    }

    override fun save(parkingMeterRecord: ParkingMeterRecord) {
        persistentRepository.save(parkingMeterRecord)
    }

    override fun find(plateNumber: String): ParkingMeterRecord? {
        val optionalRecord = persistentRepository.findById(plateNumber)
        return if (optionalRecord.isPresent) {
            optionalRecord.get()
        } else {
            null
        }
    }
}