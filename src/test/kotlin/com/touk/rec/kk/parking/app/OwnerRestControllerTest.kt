package com.touk.rec.kk.parking.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.EarningsCalculator
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(SpringRunner::class)
@WebMvcTest(OwnerRestController::class)
class OwnerRestControllerTest {

    @MockBean
    private lateinit var earningsCalculator: EarningsCalculator

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `should return value calculated by earnings calculator`() {
        val localDate = LocalDate.now()
        whenever(earningsCalculator.getEarnings(localDate)).thenReturn(BigDecimal.TEN)
        mvc.perform(get("/owner/earnings").param("date", localDate.toString()))
                .andExpect(status().isOk)
                .andExpect(content().json(createJsonResponse(BigDecimal.TEN)))
    }

    private fun createJsonResponse(value: BigDecimal) = ObjectMapper().writeValueAsString(OwnerRestController.EarningsResponse(value))
}