package com.groze.app.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.local.entity.VaultItemEntity
import com.groze.app.data.preferences.UserPreferences
import com.groze.app.data.repository.TripRepository
import com.groze.app.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val tripRepository: TripRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val vaultItems: StateFlow<List<VaultItemEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) vaultRepository.getAllItems()
            else vaultRepository.searchItems(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddEditSheet = MutableStateFlow<VaultItemEntity?>(null)
    val showAddEditSheet: StateFlow<VaultItemEntity?> = _showAddEditSheet.asStateFlow()

    private val _isAddMode = MutableStateFlow(false)
    val isAddMode: StateFlow<Boolean> = _isAddMode.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun showAddSheet() {
        _isAddMode.value = true
        _showAddEditSheet.value = VaultItemEntity(name = "", category = "", lastPrice = 0.0)
    }

    fun showEditSheet(item: VaultItemEntity) {
        _isAddMode.value = false
        _showAddEditSheet.value = item
    }

    fun dismissSheet() {
        _showAddEditSheet.value = null
    }

    fun saveItem(name: String, category: String, price: Double, unit: String) {
        viewModelScope.launch {
            val currentItem = _showAddEditSheet.value ?: return@launch
            if (_isAddMode.value) {
                vaultRepository.insertItem(
                    VaultItemEntity(
                        name = name,
                        category = category,
                        lastPrice = price,
                        unit = unit,
                        iconName = getCategoryIcon(category)
                    )
                )
            } else {
                vaultRepository.updateItem(
                    currentItem.copy(
                        name = name,
                        category = category,
                        lastPrice = price,
                        unit = unit,
                        iconName = getCategoryIcon(category),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            _showAddEditSheet.value = null
        }
    }

    fun deleteItem(item: VaultItemEntity) {
        viewModelScope.launch {
            vaultRepository.deleteItem(item)
        }
    }

    suspend fun createNewCart(): Long {
        return tripRepository.createTrip()
    }

    fun markOnboardingComplete() {
        viewModelScope.launch {
            userPreferences.setOnboardingComplete()
        }
    }

    companion object {
        fun getCategoryIcon(category: String): String {
            return when (category.uppercase()) {
                "DAIRY" -> "egg"
                "PRODUCE" -> "eco"
                "PANTRY" -> "coffee"
                "BAKERY" -> "bakery_dining"
                "BEVERAGE" -> "water_drop"
                "FROZEN" -> "ac_unit"
                "MEAT" -> "restaurant"
                "SNACKS" -> "cookie"
                else -> "inventory_2"
            }
        }
    }
}
