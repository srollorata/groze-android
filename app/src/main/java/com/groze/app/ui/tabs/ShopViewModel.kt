package com.groze.app.ui.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.data.preferences.UserPreferences
import com.groze.app.data.repository.ExchangeRateRepository
import com.groze.app.data.repository.ExchangeRateState
import com.groze.app.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val userPreferences: UserPreferences,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    val activeTrips: StateFlow<List<TripEntity>> = tripRepository.getActiveTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currency: StateFlow<String> = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    val exchangeRates: StateFlow<ExchangeRateState> = exchangeRateRepository.exchangeRates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExchangeRateState())

    fun formatPrice(price: Double): String {
        val currentCurrency = currency.value
        val symbol = getCurrencySymbol(currentCurrency)
        return "$symbol${String.format("%.2f", price)}"
    }

    private fun getCurrencySymbol(currency: String): String {
        return when (currency.uppercase()) {
            "PHP" -> "₱"
            else -> "$"
        }
    }
}
