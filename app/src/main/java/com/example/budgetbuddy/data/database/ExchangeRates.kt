package com.example.budgetbuddy.data.converter

object ExchangeRates {
    private val rates = mapOf(
        "ZAR" to 1.0,
        "USD" to 17.85,
        "EUR" to 19.64,
        "GBP" to 22.95,
        "JPY" to 0.12
    )
    const val LAST_UPDATED = "2025-01-01"

    fun convert(amount: Double, from: String, to: String): Double {
        val zar = amount * (rates[from] ?: 1.0)
        return zar / (rates[to] ?: 1.0)
    }

    fun getRate(currency: String): Double = rates[currency] ?: 1.0
}