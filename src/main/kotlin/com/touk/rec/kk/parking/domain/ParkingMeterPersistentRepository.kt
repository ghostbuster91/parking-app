package com.touk.rec.kk.parking.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ParkingMeterPersistentRepository : JpaRepository<ParkingMeterRecord, String>