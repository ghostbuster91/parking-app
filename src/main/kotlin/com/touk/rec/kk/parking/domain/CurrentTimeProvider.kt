package com.touk.rec.kk.parking.domain

import java.time.LocalDateTime

interface CurrentTimeProvider {
    fun getCurrentLocalDateTime(): LocalDateTime
}