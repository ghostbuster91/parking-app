package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.ParkingMeterRecord
import org.springframework.data.jpa.repository.JpaRepository

interface ParkingMeterPersistantRepository : JpaRepository<ParkingMeterRecord, String>