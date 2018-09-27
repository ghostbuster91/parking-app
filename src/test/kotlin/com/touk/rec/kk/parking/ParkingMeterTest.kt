package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.Test
import java.lang.IllegalStateException

class ParkingMeterTest {

    @Test
    fun `parkingMeter should not be started at the begging`() {
        val parkingManager = SimpleParkingMeter()
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }

    @Test
    fun `after starting parking meter it should be started for given plate`() {
        val parkingManager = SimpleParkingMeter()
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn1111")).isTrue()
    }

    @Test
    fun `after staring parking meter for one car it should not be stared for other`() {
        val parkingManager = SimpleParkingMeter()
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn2222")).isFalse()
    }

    @Test
    fun `driver can stop parking meter if it was started`() {
        val parkingManager = SimpleParkingMeter()
        parkingManager.startMeter("wn1111")
        parkingManager.stopMeter("wn1111")
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }

    @Test(expected = IllegalStateException::class)
    fun `driver cannot start parking meter twice`() {
        val parkingManager = SimpleParkingMeter()
        parkingManager.startMeter("wn1111")
        parkingManager.startMeter("wn1111")
    }
}

class SimpleParkingMeter : ParkingMeter {
    private val startedMeters = mutableListOf<String>()

    override fun startMeter(plateNumber: String) {
        check(!startedMeters.contains(plateNumber))
        startedMeters.add(plateNumber)
    }

    override fun checkMeter(plateNumber: String): Boolean {
        return startedMeters.contains(plateNumber)
    }

    override fun stopMeter(plateNumber: String) {
        startedMeters.remove(plateNumber)
    }
}
