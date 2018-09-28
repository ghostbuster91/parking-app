package com.touk.rec.kk.parking.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.verify
import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeter
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest
@EnableAutoConfiguration
class DriverRestControllerTest {

    @MockBean
    private lateinit var parkingMeter: ParkingMeter

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should start parking meter after post on startMeter`() {
        val plateNumber = "wn1111"
        val driverType = DriverType.DISABLED
        mvc.perform(MockMvcRequestBuilders.post("/driver/startMeter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(DriverRestController.Request(plateNumber, driverType))))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.header().string("location", "/driver/$plateNumber"))
        verify(parkingMeter).startMeter(plateNumber, driverType)
    }
}