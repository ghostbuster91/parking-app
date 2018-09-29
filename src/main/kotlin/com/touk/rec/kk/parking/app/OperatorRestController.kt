package com.touk.rec.kk.parking.app

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/operator")
class OperatorRestController {

    @GetMapping("/{plateNumber}")
    fun checkMeter(@PathVariable("plateNumber") plateNumber: String) {

    }
}