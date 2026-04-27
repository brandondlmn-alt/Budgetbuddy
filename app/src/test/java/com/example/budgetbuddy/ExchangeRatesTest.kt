package com.example.budgetbuddy

import com.example.budgetbuddy.data.converter.ExchangeRates
import org.junit.Assert.assertEquals
import org.junit.Test

class ExchangeRatesTest {

    @Test
    fun convert_ZAR_to_USD_returnsCorrectValue() {
        val result = ExchangeRates.convert(100.0, "ZAR", "USD")
        // 100 ZAR / 17.85 ≈ 5.602
        assertEquals(5.602, result, 0.001)
    }

    @Test
    fun convert_USD_to_EUR_returnsCorrectValue() {
        val result = ExchangeRates.convert(100.0, "USD", "EUR")
        // 100 USD → 1785 ZAR → 1785/19.64 ≈ 90.885
        assertEquals(90.885, result, 0.001)
    }

    @Test
    fun convert_sameCurrency_returnsSameAmount() {
        val result = ExchangeRates.convert(50.0, "GBP", "GBP")
        assertEquals(50.0, result, 0.001)
    }

    @Test
    fun getRate_knownCurrency_returnsCorrectRate() {
        assertEquals(17.85, ExchangeRates.getRate("USD"), 0.001)
    }

    @Test
    fun getRate_unknownCurrency_returnsOne() {
        assertEquals(1.0, ExchangeRates.getRate("XYZ"), 0.001)
    }
}