package com.groze.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.preferences.UserPreferences
import com.groze.app.data.repository.ExchangeRateRepository
import com.groze.app.data.repository.ExchangeRateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EXCHANGE_RATE_API_KEY = "demo"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    val darkMode: StateFlow<String> = userPreferences.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val currency: StateFlow<String> = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    val exchangeRates: StateFlow<ExchangeRateState> = exchangeRateRepository.exchangeRates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExchangeRateState())

    init {
        viewModelScope.launch {
            try {
                refreshExchangeRates()
            } catch (e: Exception) {
                // Silently fail - will use fallback rates
            }
        }
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            exchangeRateRepository.fetchRates(EXCHANGE_RATE_API_KEY)
        }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch {
            userPreferences.setDarkMode(mode)
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            userPreferences.setCurrency(currency)
        }
    }
}