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

    @Test
    fun `after staring parking meter for one car it should not be stared for other`() {
        val parkingManager = SimpleParkingManager()
        parkingManager.startMeter("wn1111")
        assert(parkingManager.checkMeter("wn2222")).isFalse()
    }
}

class SimpleParkingManager : ParkingManager {
    private val startedPlates = mutableListOf<String>()

    override fun startMeter(plateNumber: String) {
        startedPlates.add(plateNumber)
    }

    override fun checkMeter(plateNumber: String): Boolean {
        return startedPlates.contains(plateNumber)
    }
}
