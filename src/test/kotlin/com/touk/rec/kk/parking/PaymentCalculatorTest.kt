package com.touk.rec.kk.parking

import assertk.assert
import assertk.assertions.isEqualTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

class PaymentCalculatorTest {

    private val repository = mock<ParkingMeterRepository>()
    private val paymentCalculator = PaymentCalculator(repository)

    @Test
    fun `should return zero for driver who didn't start parking meter`() {
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should return 1 PLN if parking time is less or equal to one hour for completed parking`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(30)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ONE)

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusHours(1)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal.ONE)
    }

    @Test
    fun `should return 3 PLN if parking time is less or equal to two hours for completed parking`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(61)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(3))

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusHours(2)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(3))
    }

    @Test
    fun `should return 6 PLN if parking time is less or equal to three hours for completed parking`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(121)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(6).setScale(2))

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusHours(3)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(6).setScale(2))
    }

    @Test
    fun `should return 10,50 PLN if parking time is less or equal to four hours for completed parking`() {
        val startTime = LocalDateTime.MIN
        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusMinutes(181)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(10.5).setScale(2))

        whenever(repository.find(PLATE_NUMBER_ONE)).thenReturn(ParkingMeterRecord(PLATE_NUMBER_ONE, startTime, startTime.plusHours(4)))
        assert(paymentCalculator.calculateTotal(PLATE_NUMBER_ONE)).isEqualTo(BigDecimal(10.5).setScale(2))
    }

    companion object {
        private const val PLATE_NUMBER_ONE = "wn1111"
    }
}

class PaymentCalculator(private val repository: ParkingMeterRepository) {
    fun calculateTotal(plateNumber: String): BigDecimal {
        val meterRecord = repository.find(plateNumber) ?: return BigDecimal.ZERO
        val totalHours = Duration.between(meterRecord.startDate, meterRecord.endDate!!.plusMinutes(59)).toHours().toInt()
        return (1..totalHours)
                .fold(listOf<BigDecimal>()) { acc, item ->
                    acc + when {
                        item <= 1 -> BigDecimal(1)
                        item in 2..2 -> BigDecimal(2)
                        else -> acc.last().multiply(BigDecimal(1.5)).setScale(2)
                    }
                }
                .reduce { first, second -> first + second }
    }
}
