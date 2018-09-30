package com.touk.rec.kk.parking.app

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.nhaarman.mockito_kotlin.whenever
import com.touk.rec.kk.parking.app.DriverRestController.BillingResponse
import com.touk.rec.kk.parking.app.DriverRestController.Request
import com.touk.rec.kk.parking.app.OwnerRestController.EarningsResponse
import com.touk.rec.kk.parking.domain.CurrentTimeProvider
import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeterPersistentRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Autowired
    private lateinit var repository: ParkingMeterPersistentRepository

    @MockBean
    private lateinit var timeProvider: CurrentTimeProvider

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Before
    fun setUp() {
        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(A_DATETIME)
    }

    @Test
    fun `as an owner I can check my earnings which are zero at the begging`() {
        val response = restTemplate.getForEntity<EarningsResponse>("/owner/earnings?date={date}", A_DATETIME.toLocalDate())
        assert(response.statusCode).isEqualTo(HttpStatus.OK)
        assert(response.body.value).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `as a driver I can start parking meter and check my billing`() {
        val localDateTime = timeProvider.getCurrentLocalDateTime()
        val plateNumber = "wn1111"
        val startMeterResponse = startMeter(plateNumber)
        assert(startMeterResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(localDateTime.plusHours(3))
        val billingResponse = restTemplate.getForEntity<BillingResponse>("/driver/{plateNumber}", plateNumber)
        assert(billingResponse.statusCode).isEqualTo(HttpStatus.OK)
        assert(billingResponse.body.total).isGreaterThan(BigDecimal.ZERO)
    }

    @Test
    fun `as a driver I can stop parking meter`() {
        val plateNumber = "wn1111"
        startMeter(plateNumber)
        val response = stopMeter(plateNumber)
        assert(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `as an operator I can checkMeter for given plateNumber`() {
        val plateNumber = "wn1111"
        startMeter(plateNumber)
        val response = restTemplate.getForEntity<Any>("/operator/{plateNumber}", plateNumber)
        assert(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `as an owner I can check how rich am I`() {
        startMeter("wn1111")
        startMeter("wn1122")
        startMeter("wn1133")
        whenever(timeProvider.getCurrentLocalDateTime()).thenReturn(A_DATETIME.plusHours(4))
        stopMeter("wn1111")
        stopMeter("wn1122")
        stopMeter("wn1133")
        val response = restTemplate.getForEntity<EarningsResponse>("/owner/earnings?date={date}",
                A_DATETIME.toLocalDate())
        assert(response.statusCode).isEqualTo(HttpStatus.OK)
        assert(response.body.value).isEqualTo(BigDecimal.valueOf(31.50).setScale(2))
    }

    private fun startMeter(plateNumber: String): ResponseEntity<Any> {
        return restTemplate.postForEntity("/driver/startMeter", createJsonRequest(plateNumber))
    }

    private fun stopMeter(plateNumber: String) =
            restTemplate.execute("/driver/$plateNumber/stopMeter", HttpMethod.PUT, RequestCallback {}, ResponseExtractor { it })

    private fun createJsonRequest(plateNumber: String) = Request(plateNumber, DriverType.REGULAR)

    @After
    fun tearDown() {
        repository.deleteAll()
    }

    companion object {
        private val A_DATETIME = LocalDateTime.of(LocalDate.of(2018, 11, 11), LocalTime.of(16, 30))
    }
}