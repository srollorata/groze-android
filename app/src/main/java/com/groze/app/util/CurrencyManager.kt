package com.groze.app.util

import com.groze.app.data.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyManager @Inject constructor(
    private val userPreferences: com.groze.app.data.preferences.UserPreferences,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    val currentCurrency: Flow<String> = userPreferences.currency
    val exchangeRates: Flow<com.groze.app.data.repository.ExchangeRateState> = exchangeRateRepository.exchangeRates

    fun format(amount: Double, storedCurrency: String = "USD"): Flow<String> {
        return combine(currentCurrency, exchangeRates) { currency, rates ->
            val convertedAmount = if (currency != storedCurrency) {
                exchangeRateRepository.convert(amount, storedCurrency, currency)
            } else {
                amount
            }
            formatValue(convertedAmount, currency)
        }
    }

    fun formatSync(amount: Double, storedCurrency: String = "USD", currency: String): String {
        val convertedAmount = if (currency != storedCurrency) {
            exchangeRateRepository.convert(amount, storedCurrency, currency)
        } else {
            amount
        }
        return formatValue(convertedAmount, currency)
    }

    private fun formatValue(amount: Double, currency: String): String {
        val symbol = getCurrencySymbol(currency)
        return "$symbol${String.format("%.2f", amount)}"
    }

    fun getCurrencySymbol(currency: String): String {
        return when (currency.uppercase()) {
            "PHP" -> "₱"
            "USD" -> "$"
            else -> "$"
        }
    }
}