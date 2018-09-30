package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.DriverType
import com.touk.rec.kk.parking.domain.ParkingMeter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URI

@RestController
@RequestMapping("/driver")
class DriverRestController(private val parkingMeter: ParkingMeter) {

    @PostMapping("/startMeter")
    fun startMeter(@RequestBody request: Request): ResponseEntity<URI> {
        with(request) { parkingMeter.startMeter(plateNumber, driverType) }
        return ResponseEntity.created(URI.create("/driver/${request.plateNumber}")).body(null)
    }

    @PutMapping("/{plateNumber}/stopMeter")
    fun stopMeter(@PathVariable("plateNumber") plateNumber: String): ResponseEntity<Any?> {
        parkingMeter.stopMeter(plateNumber)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{plateNumber}")
    fun getBilling(@PathVariable("plateNumber") plateNumber: String): BillingResponse {
        return parkingMeter.getTotalCost(plateNumber).let(::BillingResponse)
    }

    data class Request(
            val plateNumber: String,
            val driverType: DriverType
    )

    data class BillingResponse(val total: BigDecimal)

    @ExceptionHandler
    fun errorHandler(ex: IllegalArgumentException): ResponseEntity<Nothing> {
        return ResponseEntity.badRequest().body(null)
    }
}