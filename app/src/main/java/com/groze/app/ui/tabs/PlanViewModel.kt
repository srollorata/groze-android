package com.groze.app.ui.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groze.app.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _planningTrip = MutableStateFlow<Long?>(null)
    val planningTrip: StateFlow<Long?> = _planningTrip.asStateFlow()

    init {
        viewModelScope.launch {
            tripRepository.getPlanningTripFlow().collect { trip ->
                _planningTrip.value = trip?.id
            }
        }
    }

    suspend fun createNewCart(): Long {
        return tripRepository.createTrip()
    }
}
