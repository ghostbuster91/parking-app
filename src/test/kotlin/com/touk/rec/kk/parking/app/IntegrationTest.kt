package com.touk.rec.kk.parking.app

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.domain.CurrentTimeProvider
import com.touk.rec.kk.parking.domain.DriverType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @MockBean
    private lateinit var timeProvider: CurrentTimeProvider

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Before
    fun setUp() {
        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(LocalDateTime.now())
    }

    @Test
    fun `as an owner I can check my earnings which are zero at the begging`() {
        val response = restTemplate.getForEntity<String>("/owner/earnings?date={date}", "2018-09-11")
        assert(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `as a driver I can start parking meter and check my billing`() {
        val plateNumber = "wn1111"
        val localDateTime = LocalDateTime.now()
        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(localDateTime)
        val startMeterResponse = restTemplate.postForEntity<String>("/driver/startMeter", createJsonRequest(plateNumber))
        assert(startMeterResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(localDateTime.plusHours(3))
        val billingResponse = restTemplate.getForEntity<DriverRestController.BillingResponse>("/driver/{plateNumber}", plateNumber)
        assert(billingResponse.statusCode).isEqualTo(HttpStatus.OK)
        assert(billingResponse.body.total).isGreaterThan(BigDecimal.ZERO)
    }

    private fun createJsonRequest(plateNumber: String) = DriverRestController.Request(plateNumber, DriverType.REGULAR)

}