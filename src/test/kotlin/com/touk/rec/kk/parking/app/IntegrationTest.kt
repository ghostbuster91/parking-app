package com.touk.rec.kk.parking.app

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `as an owner I can check my earnings which are zero at the begging`() {
        val response = restTemplate.getForEntity<String>("/owner/earnings?date={date}", "2018-09-11")
        assert(response.statusCode).isEqualTo(HttpStatus.OK)
    }
}