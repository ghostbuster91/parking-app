package com.touk.rec.kk.parking.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@WebMvcTest(DriverRestController::class)
class DriverRestControllerTest {

    @MockBean
    private lateinit var paymentCalculator: PaymentCalculator

    @MockBean
    private lateinit var parkingMeterRepository: ParkingMeterRepository

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
    fun `should return badRequest when parking meter throws illegal argument exception during startingMeter`() {
        whenever(parkingMeter.startMeter(any(), any())).thenThrow(IllegalArgumentException())
        startMeter(PLATE_ONE, DISABLED_DRIVER)
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 204 after put on stopMeter`() {
        mvc.perform(put("/driver/$PLATE_ONE/stopMeter"))
                .andExpect(status().isNoContent)
    }

    @Test
    fun `should return badRequest when parking meter throws illegal argument exception during stopMeter`() {
        whenever(parkingMeter.stopMeter(any())).thenThrow(IllegalArgumentException())
        mvc.perform(put("/driver/$PLATE_ONE/stopMeter"))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 404 when trying to get billing for not registered plate numbers`() {
        mvc.perform(get("/driver/$PLATE_ONE"))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should return return total payment for given plateNumber`() {
        val meterRecord = ParkingMeterRecord(PLATE_ONE, LocalDateTime.MIN, LocalDateTime.MIN, DISABLED_DRIVER)
        whenever(parkingMeterRepository.find(any())).thenReturn(meterRecord)
        whenever(paymentCalculator.calculateTotal(meterRecord)).thenReturn(BigDecimal.valueOf(22))
        mvc.perform(get("/driver/$PLATE_ONE"))
                .andExpect(status().isOk)
                .andExpect(content().json(ObjectMapper().writeValueAsString(DriverRestController.BillingResponse(BigDecimal.valueOf(22)))))
    }

    private fun startMeter(plateNumber: String, driverType: DriverType): ResultActions {
        return mvc.perform(post("/driver/startMeter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(DriverRestController.Request(plateNumber, driverType))))
    }

    companion object {
        private const val PLATE_ONE = "wn1111"
        private val DISABLED_DRIVER = DriverType.DISABLED
    }
}