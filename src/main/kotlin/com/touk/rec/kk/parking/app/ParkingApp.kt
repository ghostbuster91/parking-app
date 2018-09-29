package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.touk.rec.kk.parking"])
@EntityScan(basePackageClasses = [ParkingMeterRecord::class])
class ParkingApp

fun main(args: Array<String>) {
    runApplication<ParkingApp>()
}