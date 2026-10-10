package com.groze.app.util

import com.groze.app.data.repository.ExchangeRateRepository

object CurrencyFormatter {
    private var exchangeRateRepository: ExchangeRateRepository? = null

    fun initialize(repository: ExchangeRateRepository) {
        exchangeRateRepository = repository
    }

    fun format(
        amount: Double,
        fromCurrency: String = "USD",
        toCurrency: String,
        showSymbol: Boolean = true
    ): String {
        val convertedAmount = if (fromCurrency != toCurrency && exchangeRateRepository != null) {
            exchangeRateRepository!!.convert(amount, fromCurrency, toCurrency)
        } else {
            amount
        }

        val symbol = getCurrencySymbol(toCurrency)
        val formatted = String.format("%.2f", convertedAmount)

        return if (showSymbol) {
            "$symbol$formatted"
        } else {
            formatted
        }
    }

    fun convert(
        amount: Double,
        fromCurrency: String = "USD",
        toCurrency: String
    ): Double {
        if (fromCurrency == toCurrency) return amount

        return exchangeRateRepository?.convert(amount, fromCurrency, toCurrency) ?: amount
    }

    fun getCurrencySymbol(currency: String): String {
        return when (currency.uppercase()) {
            "PHP", "PHPH" -> "₱"
            "USD", "US" -> "$"
            "EUR", "€" -> "€"
            "GBP", "£" -> "£"
            "JPY", "¥" -> "¥"
            else -> "$"
        }
    }

    fun getCurrencyLabel(currency: String): String {
        return when (currency.uppercase()) {
            "PHP" -> "PHP (₱)"
            "USD" -> "USD ($)"
            "EUR" -> "EUR (€)"
            "GBP" -> "GBP (£)"
            "JPY" -> "JPY (¥)"
            else -> "USD ($)"
        }
    }
}