package com.touk.rec.kk.parking

import java.time.LocalDateTime

interface CurrentTimeProvider {
    fun getCurrentLocalDateTime(): LocalDateTime
}