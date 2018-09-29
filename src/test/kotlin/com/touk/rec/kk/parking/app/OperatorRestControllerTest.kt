package com.touk.rec.kk.parking.app

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(OperatorRestController::class)
class OperatorRestControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should return 200 ok when asked for vehicle which started meter`() {
        val plateNumber = "wn1111"
        mvc.perform(MockMvcRequestBuilders.get("/operator/$plateNumber"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}