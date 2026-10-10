package com.groze.app.data.repository

import com.groze.app.data.remote.ExchangeRateApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ExchangeRateState(
    val rates: Map<String, Double> = FallbackRates.rates,
    val lastUpdated: Long = 0L,
    val isUsingFallback: Boolean = true,
    val error: String? = null
)

@Singleton
class ExchangeRateRepository @Inject constructor(
    private val apiService: ExchangeRateApiService
) {
    private val _exchangeRates = MutableStateFlow(
        ExchangeRateState(
            rates = FallbackRates.rates,
            lastUpdated = 0L,
            isUsingFallback = true,
            error = null
        )
    )
    val exchangeRates: StateFlow<ExchangeRateState> = _exchangeRates.asStateFlow()

    private val cacheExpiryMs = 60 * 60 * 1000L

    suspend fun fetchRates(apiKey: String) {
        try {
            val response = apiService.getExchangeRates(apiKey)
            if (response.result == "success") {
                _exchangeRates.value = ExchangeRateState(
                    rates = response.conversionRates,
                    lastUpdated = System.currentTimeMillis(),
                    isUsingFallback = false,
                    error = null
                )
            } else {
                useFallbackWithError("API returned error: ${response.result}")
            }
        } catch (e: Exception) {
            useFallbackWithError(e.message ?: "Unknown error")
        }
    }

    private fun useFallbackWithError(errorMsg: String) {
        _exchangeRates.value = ExchangeRateState(
            rates = FallbackRates.rates,
            lastUpdated = System.currentTimeMillis(),
            isUsingFallback = true,
            error = errorMsg
        )
    }

    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return amount

        val rates = _exchangeRates.value.rates
        val fromRate = rates[fromCurrency] ?: 1.0
        val toRate = rates[toCurrency] ?: 1.0

        return amount * (toRate / fromRate)
    }

    fun getRate(fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return 1.0

        val rates = _exchangeRates.value.rates
        val fromRate = rates[fromCurrency] ?: 1.0
        val toRate = rates[toCurrency] ?: 1.0

        return toRate / fromRate
    }

    fun isCacheValid(): Boolean {
        val lastUpdated = _exchangeRates.value.lastUpdated
        return lastUpdated > 0 && (System.currentTimeMillis() - lastUpdated) < cacheExpiryMs
    }
}