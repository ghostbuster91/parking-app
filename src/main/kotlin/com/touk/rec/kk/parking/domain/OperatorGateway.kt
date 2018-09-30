package com.touk.rec.kk.parking.domain

interface OperatorGateway {
    fun checkMeter(plateNumber: String): Boolean
}