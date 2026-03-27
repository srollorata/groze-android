package com.groze.app.ui.activetrip

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.CartItemStatus
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveTripUiState(
    val trip: TripEntity? = null,
    val items: List<CartItemEntity> = emptyList(),
    val totalItems: Int = 0,
    val checkedItems: Int = 0,
    val expectedTotal: Double = 0.0,
    val actualTotal: Double = 0.0
)

@HiltViewModel
class ActiveTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId") ?: 0L

    private val cartItems = tripRepository.getCartItems(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val checkedCount = tripRepository.getCheckedItemCount(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val uiState: StateFlow<ActiveTripUiState> = combine(
        cartItems,
        checkedCount
    ) { items, checked ->
        val expected = items.sumOf { it.plannedPrice * it.quantity }
        val actual = items.filter { it.status == CartItemStatus.CHECKED }
            .sumOf { (it.actualPrice ?: it.plannedPrice) * it.quantity }

        ActiveTripUiState(
            items = items,
            totalItems = items.size,
            checkedItems = checked,
            expectedTotal = expected,
            actualTotal = actual
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveTripUiState())

    private val _showPriceSheet = MutableStateFlow<CartItemEntity?>(null)
    val showPriceSheet: StateFlow<CartItemEntity?> = _showPriceSheet.asStateFlow()

    private val _showAddAdHocSheet = MutableStateFlow(false)
    val showAddAdHocSheet: StateFlow<Boolean> = _showAddAdHocSheet.asStateFlow()

    fun checkItem(item: CartItemEntity) {
        viewModelScope.launch {
            if (item.status == CartItemStatus.CHECKED) {
                tripRepository.uncheckItem(item.id)
            } else {
                tripRepository.checkItem(item.id, item.actualPrice ?: item.plannedPrice)
            }
            updateTotals()
        }
    }

    fun skipItem(item: CartItemEntity) {
        viewModelScope.launch {
            tripRepository.skipItem(item.id)
            updateTotals()
        }
    }

    fun showPriceUpdate(item: CartItemEntity) {
        _showPriceSheet.value = item
    }

    fun dismissPriceSheet() {
        _showPriceSheet.value = null
    }

    fun updatePrice(itemId: Long, newPrice: Double) {
        viewModelScope.launch {
            tripRepository.updateItemPrice(itemId, newPrice)
            tripRepository.checkItem(itemId, newPrice)
            _showPriceSheet.value = null
            updateTotals()
        }
    }

    fun showAddAdHoc() {
        _showAddAdHocSheet.value = true
    }

    fun dismissAddAdHoc() {
        _showAddAdHocSheet.value = false
    }

    fun addAdHocItem(name: String, price: Double, category: String, unit: String, quantity: Int = 1) {
        viewModelScope.launch {
            tripRepository.addAdHocItem(tripId, name, price, category, unit, quantity)
            _showAddAdHocSheet.value = false
            updateTotals()
        }
    }

    fun updateItemQuantity(item: CartItemEntity, newQuantity: Int) {
        if (newQuantity < 1) {
            viewModelScope.launch {
                tripRepository.deleteCartItem(item.id)
                updateTotals()
            }
        } else {
            viewModelScope.launch {
                tripRepository.updateItemQuantity(item.id, newQuantity)
                updateTotals()
            }
        }
    }

    suspend fun finishTrip(): Long {
        updateTotals()
        tripRepository.finishTrip(tripId)
        return tripId
    }

    private fun updateTotals() {
        viewModelScope.launch {
            val items = uiState.value.items
            val expected = items.sumOf { it.plannedPrice * it.quantity }
            val actual = items.filter { it.status == CartItemStatus.CHECKED }
                .sumOf { (it.actualPrice ?: it.plannedPrice) * it.quantity }
            tripRepository.updateTripTotals(tripId, expected, actual)
        }
    }
}
