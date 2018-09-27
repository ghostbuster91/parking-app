package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isFalse
import org.junit.Test

class DriverTest {

    @Test
    fun `parkingMeter should not be started at the begging`() {
        val parkingManager = SimpleParkingManager()
        assert(parkingManager.checkMeter("wn1111")).isFalse()
    }
}

class SimpleParkingManager : ParkingManager {

    override fun checkMeter(plateNumber: String): Boolean {
        return false
    }
}
