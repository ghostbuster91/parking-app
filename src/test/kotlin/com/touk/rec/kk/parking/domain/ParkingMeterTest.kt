package com.touk.rec.kk.parking.domain

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.impl.OperatorGatewayImpl
import com.touk.rec.kk.parking.domain.impl.SimpleParkingMeter
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class ParkingMeterTest {
    private val repository = InMemoryParkingMeterRecordService()
    private val currentTimeProvider = mock<CurrentTimeProvider> {
        on { getCurrentLocalDateTime() } doReturn LocalDateTime.MIN
    }
    private val operatorGateway = OperatorGatewayImpl(repository)
    private val paymentCalculator = mock<PaymentCalculator>()
    private val parkingManager = SimpleParkingMeter(repository, currentTimeProvider, paymentCalculator)

    @Test
    fun `after starting parking meter it should be started for given plate`() {
        startParkingMeter()
        assert(operatorGateway.checkMeter(PLATE_NUMBER_ONE)).isTrue()
    }

    @Test
    fun `after staring parking meter for one car it should not be stared for other`() {
        startParkingMeter()
        assert(operatorGateway.checkMeter(PLATE_NUMBER_TOW)).isFalse()
    }

    @Test
    fun `driver can stop parking meter if it was started`() {
        startParkingMeter()
        parkingManager.stopMeter(PLATE_NUMBER_ONE)
        assert(operatorGateway.checkMeter(PLATE_NUMBER_ONE)).isFalse()
    }

    @Test(expected = IllegalStateException::class)
    fun `driver cannot start parking meter twice`() {
        startParkingMeter()
        startParkingMeter()
    }

    @Test
    fun `parking meter should save to repository current time when starting meter`() {
        startParkingMeter()
        assert(repository.find(PLATE_NUMBER_ONE)!!.startDate).isEqualTo(LocalDateTime.MIN)
    }

    @Test
    fun `should use current time provider to obtain time when starting meter`() {
        whenever(currentTimeProvider.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(1, 2, 3, 4, 5))
        startParkingMeter()
        assert(repository.find(PLATE_NUMBER_ONE)!!.startDate).isEqualTo(LocalDateTime.of(1, 2, 3, 4, 5))
    }

    @Test
    fun `should use current time provider to obtain time when stopping meter`() {
        startParkingMeter()
        whenever(currentTimeProvider.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(1, 2, 3, 4, 5))
        parkingManager.stopMeter(PLATE_NUMBER_ONE)
        assert(repository.find(PLATE_NUMBER_ONE)!!.endDate).isEqualTo(LocalDateTime.of(1, 2, 3, 4, 5))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw illegal argument exception when trying to stop not started meter`() {
        parkingManager.stopMeter(PLATE_NUMBER_TOW)
    }

    @Test
    fun `should return total cost zero before starting meter`() {
        val totalCost = parkingManager.getTotalCost(PLATE_NUMBER_ONE)
        assert(totalCost).isEqualTo(BigDecimal.ZERO.setScale(2))
    }

    @Test
    fun `should return proper total cost for recognized plate number`() {
        startParkingMeter()
        whenever(paymentCalculator.calculateTotal(any())).thenReturn(BigDecimal.TEN.setScale(2))
        val totalCost = parkingManager.getTotalCost(PLATE_NUMBER_ONE)
        assert(totalCost).isEqualTo(BigDecimal.TEN.setScale(2))
    }

    private fun startParkingMeter() {
        parkingManager.startMeter(PLATE_NUMBER_ONE, DriverType.REGULAR)
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
        private const val PLATE_NUMBER_TOW = "wn2222"
    }
}

private class InMemoryParkingMeterRecordService : ParkingMeterRecordService {
    private val data = mutableMapOf<String, ParkingMeterRecord>()

    override fun save(parkingMeterRecord: ParkingMeterRecord) {
        data[parkingMeterRecord.plateNumber] = parkingMeterRecord
    }

    override fun find(plateNumber: String): ParkingMeterRecord? {
        return data[plateNumber]
    }
}

