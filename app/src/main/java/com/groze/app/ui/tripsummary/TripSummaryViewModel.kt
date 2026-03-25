package com.groze.app.ui.tripsummary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.CartItemStatus
import com.groze.app.data.local.entity.VaultItemEntity
import com.groze.app.data.repository.TripRepository
import com.groze.app.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripDelta(
    val item: CartItemEntity,
    val type: DeltaType,
    val priceDifference: Double = 0.0
)

enum class DeltaType {
    PRICE_CHANGE,
    NEW_ITEM,
    SKIPPED
}

data class TripSummaryUiState(
    val originalPlan: Double = 0.0,
    val actualSpend: Double = 0.0,
    val percentChange: Double = 0.0,
    val deltas: List<TripDelta> = emptyList(),
    val synced: Boolean = false
)

@HiltViewModel
class TripSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val vaultRepository: VaultRepository
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId") ?: 0L

    private val cartItems = tripRepository.getCartItems(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<TripSummaryUiState> = cartItems.map { items ->
        val expectedTotal = items.sumOf { it.plannedPrice * it.quantity }
        val actualTotal = items.filter { it.status == CartItemStatus.CHECKED }
            .sumOf { (it.actualPrice ?: it.plannedPrice) * it.quantity }

        val percentChange = if (expectedTotal > 0) {
            ((actualTotal - expectedTotal) / expectedTotal) * 100
        } else 0.0

        val deltas = mutableListOf<TripDelta>()

        items.forEach { item ->
            when {
                item.isAdHoc -> {
                    deltas.add(TripDelta(item, DeltaType.NEW_ITEM, item.actualPrice ?: 0.0))
                }
                item.status == CartItemStatus.SKIPPED -> {
                    deltas.add(TripDelta(item, DeltaType.SKIPPED, -(item.plannedPrice * item.quantity)))
                }
                item.actualPrice != null && item.actualPrice != item.plannedPrice -> {
                    deltas.add(TripDelta(
                        item,
                        DeltaType.PRICE_CHANGE,
                        (item.actualPrice - item.plannedPrice) * item.quantity
                    ))
                }
            }
        }

        TripSummaryUiState(
            originalPlan = expectedTotal,
            actualSpend = actualTotal,
            percentChange = percentChange,
            deltas = deltas
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TripSummaryUiState())

    private val _synced = MutableStateFlow(false)
    val synced: StateFlow<Boolean> = _synced.asStateFlow()

    fun confirmUpdates(onComplete: () -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value

            // Write updated prices back to vault
            items.forEach { item ->
                if (item.vaultItemId != null && item.status == CartItemStatus.CHECKED) {
                    val newPrice = item.actualPrice ?: item.plannedPrice
                    vaultRepository.updatePrice(item.vaultItemId, newPrice)
                }

                // Add ad-hoc items to vault
                if (item.isAdHoc && item.status == CartItemStatus.CHECKED) {
                    vaultRepository.insertItem(
                        VaultItemEntity(
                            name = item.name,
                            category = item.category,
                            lastPrice = item.actualPrice ?: 0.0,
                            unit = item.unit,
                            iconName = item.iconName
                        )
                    )
                }
            }

            // Clear the cart
            tripRepository.clearCart(tripId)
            _synced.value = true
            onComplete()
        }
    }

    fun dismissUpdates(onComplete: () -> Unit) {
        viewModelScope.launch {
            tripRepository.clearCart(tripId)
            onComplete()
        }
    }
}
