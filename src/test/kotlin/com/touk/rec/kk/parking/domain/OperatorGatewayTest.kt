package com.touk.rec.kk.parking.domain

import assertk.assert
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.time.LocalDateTime

class OperatorGatewayTest {
    private val repository = mock<ParkingMeterRepository>()
    private val operatorGateway = OperatorGateway(repository)

    @Test
    fun `should return false if there is no record for given plate number`() {
        whenever(repository.find(any())).thenReturn(null)
        assert(operatorGateway.checkMeter("wn111")).isFalse()
    }

    @Test
    fun `should return true if there is a running record for given plate number`() {
        val plateNumber = "wn111"
        whenever(repository.find(any())).thenReturn(ParkingMeterRecord(plateNumber, LocalDateTime.MIN, null, DriverType.DISABLED))
        assert(operatorGateway.checkMeter(plateNumber)).isTrue()
    }

    @Test
    fun `should return false if there is a completed record for given plate number`() {
        whenever(repository.find(any())).thenReturn(ParkingMeterRecord(plateNumber, LocalDateTime.MIN, LocalDateTime.MAX, DriverType.DISABLED))
        assert(operatorGateway.checkMeter(plateNumber)).isFalse()
    }

    companion object {
        private const val plateNumber = "wn111"
    }
}