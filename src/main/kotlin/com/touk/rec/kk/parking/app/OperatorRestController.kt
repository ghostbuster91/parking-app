package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.OperatorGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/operator")
class OperatorRestController(private val operatorGateway: OperatorGateway) {

    @GetMapping("/{plateNumber}")
    fun checkMeter(@PathVariable("plateNumber") plateNumber: String) {
        if (!operatorGateway.checkMeter(plateNumber)) {
            throw ValidParkingMeterNotFound()
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class ValidParkingMeterNotFound : RuntimeException()
}