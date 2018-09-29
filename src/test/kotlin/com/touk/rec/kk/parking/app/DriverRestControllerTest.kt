package com.touk.rec.kk.parking.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeter
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(DriverRestController::class)
class DriverRestControllerTest {

    @MockBean
    private lateinit var parkingMeter: ParkingMeter

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should start parking meter after post on startMeter`() {
        startMeter(PLATE_ONE, DISABLED_DRIVER)
                .andExpect(status().isCreated)
                .andExpect(header().string("location", "/driver/$PLATE_ONE"))
        verify(parkingMeter).startMeter(PLATE_ONE, DISABLED_DRIVER)
    }

    @Test
    fun `should return badRequest when trying to startMeter twice for the same plateNumber`() {
        startMeter(PLATE_ONE, DISABLED_DRIVER)
                .andExpect(status().isCreated)
        whenever(parkingMeter.startMeter(any(), any())).thenThrow(IllegalArgumentException())
        startMeter(PLATE_ONE, DISABLED_DRIVER)
                .andExpect(status().isBadRequest)
    }

    private fun startMeter(plateNumber: String, driverType: DriverType): ResultActions {
        return mvc.perform(post("/driver/startMeter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(DriverRestController.Request(plateNumber, driverType))))
    }

    companion object {
        private val PLATE_ONE = "wn1111"
        private val DISABLED_DRIVER = DriverType.DISABLED
    }
}