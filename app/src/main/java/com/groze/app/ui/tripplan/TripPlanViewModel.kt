package com.groze.app.ui.tripplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.local.entity.CartItemEntity
import com.groze.app.data.local.entity.TripEntity
import com.groze.app.data.local.entity.VaultItemEntity
import com.groze.app.data.repository.TripRepository
import com.groze.app.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripPlanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vaultRepository: VaultRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    val tripId: Long = savedStateHandle.get<Long>("tripId") ?: 0L

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val vaultResults: StateFlow<List<VaultItemEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) vaultRepository.getAllItems()
            else vaultRepository.searchItems(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = tripRepository.getCartItems(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemCount: StateFlow<Int> = tripRepository.getCartItemCount(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val expectedTotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.plannedPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addVaultItemToCart(vaultItem: VaultItemEntity) {
        viewModelScope.launch {
            tripRepository.addCartItem(
                CartItemEntity(
                    tripId = tripId,
                    vaultItemId = vaultItem.id,
                    name = vaultItem.name,
                    category = vaultItem.category,
                    quantity = 1,
                    unit = vaultItem.unit,
                    plannedPrice = vaultItem.lastPrice,
                    iconName = vaultItem.iconName
                )
            )
        }
    }

    fun addNewItemToVaultAndCart(name: String, category: String, price: Double, unit: String) {
        viewModelScope.launch {
            // Add to vault first
            val vaultId = vaultRepository.insertItem(
                VaultItemEntity(
                    name = name,
                    category = category,
                    lastPrice = price,
                    unit = unit,
                    iconName = com.groze.app.ui.vault.VaultViewModel.getCategoryIcon(category)
                )
            )
            // Then add to cart
            tripRepository.addCartItem(
                CartItemEntity(
                    tripId = tripId,
                    vaultItemId = vaultId,
                    name = name,
                    category = category,
                    quantity = 1,
                    unit = unit,
                    plannedPrice = price,
                    iconName = com.groze.app.ui.vault.VaultViewModel.getCategoryIcon(category)
                )
            )
        }
    }

    fun removeCartItem(item: CartItemEntity) {
        viewModelScope.launch {
            tripRepository.updateCartItem(item) // Using delete via repository
        }
    }

    suspend fun startShopping(): Long {
        // Update trip with expected total
        val total = cartItems.value.sumOf { it.plannedPrice * it.quantity }
        tripRepository.updateTripTotals(tripId, total, 0.0)
        tripRepository.startShopping(tripId)
        return tripId
    }
}
