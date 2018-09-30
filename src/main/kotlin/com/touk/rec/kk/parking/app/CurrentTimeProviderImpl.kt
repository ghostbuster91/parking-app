package com.touk.rec.kk.parking.app

import com.touk.rec.kk.parking.domain.CurrentTimeProvider
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CurrentTimeProviderImpl : CurrentTimeProvider {
    override fun getCurrentLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}