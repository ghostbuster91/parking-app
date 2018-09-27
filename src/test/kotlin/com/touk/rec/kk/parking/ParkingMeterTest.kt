package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.time.LocalDateTime

class ParkingMeterTest {
    private val repository = InMemoryParkingMeterRepository()
    private val currentTimeProvider = mock<CurrentTimeProvider>{
        on { getCurrentLocalDateTime() } doReturn LocalDateTime.MIN
    }
    private val parkingManager = SimpleParkingMeter(repository, currentTimeProvider)

    @Test
    fun `parkingMeter should not be started at the begging`() {
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }

    @Test
    fun `after starting parking meter it should be started for given plate`() {
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn1111")).isTrue()
    }

    @Test
    fun `after staring parking meter for one car it should not be stared for other`() {
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn2222")).isFalse()
    }

    @Test
    fun `driver can stop parking meter if it was started`() {
        parkingManager.startMeter("wn1111")
        parkingManager.stopMeter("wn1111")
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }

    @Test(expected = IllegalStateException::class)
    fun `driver cannot start parking meter twice`() {
        parkingManager.startMeter("wn1111")
        parkingManager.startMeter("wn1111")
    }

    @Test
    fun `parking meter should save to repository current time when starting meter`() {
        parkingManager.startMeter("wn1111")
        assert(repository.find("wn1111")!!.startDate).isEqualTo(LocalDateTime.MIN)
    }

    @Test
    fun `parking meter should use current time provider to obtain time when starting meter`() {
        whenever(currentTimeProvider.getCurrentLocalDateTime()).thenReturn(LocalDateTime.of(1, 2, 3, 4, 5))
        parkingManager.startMeter("wn1111")
        assert(repository.find("wn1111")!!.startDate).isEqualTo(LocalDateTime.of(1, 2, 3, 4, 5))
    }
}

class InMemoryParkingMeterRepository : ParkingMeterRepository {
    private val data = mutableMapOf<String, ParkingMeterRecord>()

    override fun save(parkingMeterRecord: ParkingMeterRecord) {
        data[parkingMeterRecord.plateNumber] = parkingMeterRecord
    }

    override fun find(plateNumber: String): ParkingMeterRecord? {
        return data[plateNumber]
    }
}

class SimpleParkingMeter(
        private val repository: ParkingMeterRepository,
        private val currentTimeProvider: CurrentTimeProvider
) : ParkingMeter {

    override fun startMeter(plateNumber: String) {
        check(!checkMeter(plateNumber))
        repository.save(ParkingMeterRecord(plateNumber, currentTimeProvider.getCurrentLocalDateTime(), null))
    }

    override fun checkMeter(plateNumber: String): Boolean {
        val meterRecord = repository.find(plateNumber)
        return meterRecord?.isRunning ?: false
    }

    override fun stopMeter(plateNumber: String) {
        val meterRecord = repository.find(plateNumber)
        check(meterRecord?.isRunning ?: false)
        repository.save(meterRecord!!.copy(endDate = LocalDateTime.MIN))
    }
}