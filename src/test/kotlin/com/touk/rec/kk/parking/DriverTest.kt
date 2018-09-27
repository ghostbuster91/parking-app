package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.Test

class DriverTest {

    @Test
    fun `parkingMeter should not be started at the begging`() {
        val parkingManager = SimpleParkingManager()
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }

    @Test
    fun `after starting parking meter it should be started for given plate`() {
        val parkingManager = SimpleParkingManager()
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn1111")).isTrue()
    }
}

class SimpleParkingManager : ParkingManager {
    private var started = false

    override fun startMeter(plateNumber: String) {
        started = true
    }

    override fun checkMeter(plateNumber: String): Boolean {
        return started
    }
}
