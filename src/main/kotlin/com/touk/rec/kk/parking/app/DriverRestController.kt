package com.touk.rec.kk.parking.app

import com.fasterxml.jackson.annotation.JsonCreator
import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/driver")
class DriverRestController(private val parkingMeter: ParkingMeter) {

    @PostMapping("/startMeter")
    fun startMeter(@RequestBody request: Request): ResponseEntity<URI> {
        with(request) { parkingMeter.startMeter(plateNumber, driverType) }
        return ResponseEntity.created(URI.create("/driver/${request.plateNumber}")).body(null)
    }

    data class Request @JsonCreator constructor(
            val plateNumber: String,
            val driverType: DriverType
    )
}