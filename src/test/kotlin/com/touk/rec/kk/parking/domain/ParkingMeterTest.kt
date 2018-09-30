package com.touk.rec.kk.parking.domain

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.*
import com.touk.rec.kk.parking.domain.impl.SimpleParkingMeter
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class ParkingMeterTest {
    private val recordService = mock<ParkingMeterRecordService>()
    private val currentTimeProvider = mock<CurrentTimeProvider> {
        on { getCurrentLocalDateTime() } doReturn LocalDateTime.MIN
    }
    private val paymentCalculator = mock<PaymentCalculator>()
    private val parkingManager = SimpleParkingMeter(recordService, currentTimeProvider, paymentCalculator)

    @Test
    fun `should save meterRecord to recordService when starting parking meter`() {
        startParkingMeter(PLATE_NUMBER_ONE, DriverType.REGULAR)
        verify(recordService).save(argThat { plateNumber == PLATE_NUMBER_ONE && driverType == DriverType.REGULAR })
    }

    @Test
    fun `should update meterRecord in recordService when stopping parking meter`() {
        whenever(currentTimeProvider.getCurrentLocalDateTime()).thenReturn(LocalDateTime.MAX)
        whenever(recordService.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, LocalDateTime.MIN, null, DriverType.REGULAR))
        parkingManager.stopMeter(PLATE_NUMBER_ONE)
        verify(recordService).save(argThat { plateNumber == PLATE_NUMBER_ONE && endDate == LocalDateTime.MAX })
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw illegalStateException when trying to start already started parkingMeter`() {
        whenever(recordService.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, LocalDateTime.MIN, null, DriverType.REGULAR))
        startParkingMeter(PLATE_NUMBER_ONE, DriverType.REGULAR)
    }

    @Test
    fun `parking meter should save to repository current time when starting meter`() {
        startParkingMeter(PLATE_NUMBER_ONE, DriverType.REGULAR)
        verify(recordService).save(argThat { plateNumber == PLATE_NUMBER_ONE && startDate == LocalDateTime.MIN })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw illegal argument exception when trying to stop not started meter`() {
        parkingManager.stopMeter(PLATE_NUMBER_ONE)
    }

    @Test
    fun `should return total cost zero before starting meter`() {
        val totalCost = parkingManager.getTotalCost(PLATE_NUMBER_ONE)
        assert(totalCost).isEqualTo(BigDecimal.ZERO.setScale(2))
    }

    @Test
    fun `should return proper total cost for recognized plate number`() {
        whenever(recordService.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, LocalDateTime.MIN, null, DriverType.REGULAR))
        whenever(paymentCalculator.calculateTotal(any())).thenReturn(BigDecimal.TEN.setScale(2))
        val totalCost = parkingManager.getTotalCost(PLATE_NUMBER_ONE)
        assert(totalCost).isEqualTo(BigDecimal.TEN.setScale(2))
    }

    private fun startParkingMeter(plateNumber: String, driverType: DriverType) {
        parkingManager.startMeter(plateNumber, driverType)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
    }
}