package com.touk.rec.kk.parking.app

import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.OperatorGateway
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(OperatorRestController::class)
class OperatorRestControllerTest {

    @MockBean
    private lateinit var operatorGateway: OperatorGateway

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should return 200 ok when operatorGateway returns true`() {
        val plateNumber = "wn1111"
        whenever(operatorGateway.checkMeter(plateNumber)).thenReturn(true)
        mvc.perform(MockMvcRequestBuilders.get("/operator/$plateNumber"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return 404 not found when operatorGateway returns false`() {
        val plateNumber = "wn1111"
        mvc.perform(MockMvcRequestBuilders.get("/operator/$plateNumber"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}