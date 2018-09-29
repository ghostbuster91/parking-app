package com.touk.rec.kk.parking.domain

import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.impl.ParkingMeterRecordServiceImpl
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ParkingMeterRecordServiceTest {

    private val persistentRepository = mock<ParkingMeterPersistentRepository>()
    private val parkingMeterService = ParkingMeterRecordServiceImpl(persistentRepository)

    @Test
    fun `should save given parking record to repository`() {
        val meterRecord = ParkingMeterRecord("wn111", LocalDateTime.MIN, null, DriverType.REGULAR)
        parkingMeterService.save(meterRecord)
        verify(persistentRepository).save(meterRecord)
    }

    @Test
    fun `find should return null if there is no record for given plateNumber`() {
        whenever(persistentRepository.findById(any())).thenReturn(Optional.empty())
        assertk.assert(parkingMeterService.find("wn1111")).isNull()
    }

    @Test
    fun `find should return parking record from repository`() {
        val meterRecord = ParkingMeterRecord("wn111", LocalDateTime.MIN, null, DriverType.REGULAR)
        whenever(persistentRepository.findById(any())).thenReturn(Optional.of(meterRecord))
        assertk.assert(parkingMeterService.find("wn1111")).isEqualTo(meterRecord)
    }

    @Test
    fun `completedRecords should not return records which are not completed`() {
        whenever(persistentRepository.findAll()).thenReturn(listOf(ParkingMeterRecord("wn111", LocalDateTime.MIN, null, DriverType.REGULAR)))
        assertk.assert(parkingMeterService.getCompletedRecords(LocalDate.MIN)).isEmpty()
    }
}