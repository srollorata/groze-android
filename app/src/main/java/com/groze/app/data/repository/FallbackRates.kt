package com.groze.app.data.repository

object FallbackRates {
    val rates: Map<String, Double> = mapOf(
        "USD" to 1.0,
        "PHP" to 58.0
    )

    const val USD_TO_PHP = 58.0
    const val PHP_TO_USD = 1.0 / 58.0

    fun getRate(from: String, to: String): Double {
        if (from == to) return 1.0
        return rates[to] ?: 1.0
    }
}