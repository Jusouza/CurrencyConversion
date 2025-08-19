package com.example.currencyconversion

object ExchangeRates {
    // Base currency value in BRL (ex.: 1 USD = 5.40 BRL)
    private val brlPerUnit = mapOf(
        "BRL" to 1.00,
        "USD" to 5.40,
        "INR" to 0.065,
        "CAD" to 3.95,
        "AUD" to 3.60,
        "JPY" to 0.037,
        "GBP" to 6.90,
        "EUR" to 5.85,
        "ARS" to 0.0065,
        "UYU" to 0.14
    )

    fun convert(amount: Double, from: String, to: String): Double {
        val fromRate = brlPerUnit[from] ?: return Double.NaN
        val toRate   = brlPerUnit[to]   ?: return Double.NaN
        val brlAmount = amount * fromRate
        return brlAmount / toRate
    }
}
