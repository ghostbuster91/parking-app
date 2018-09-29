package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.EarningsCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RestController
@RequestMapping("/owner")
class OwnerRestController(private val earningsCalculator: EarningsCalculator) {

    @GetMapping("/earnings")
    fun earnings(@RequestParam("date") stringDate: String): EarningsResponse {
        val dateTimeFormat = DateTimeFormatter.ISO_LOCAL_DATE
        val localDate = dateTimeFormat.parse(stringDate, LocalDate::from)
        return earningsCalculator.getEarnings(localDate)
                .let(::EarningsResponse)
    }

    data class EarningsResponse(val value: BigDecimal)
}