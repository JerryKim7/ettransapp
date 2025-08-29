package ru.ettransapp.ui.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.ettransapp.data.network.ChecklistItemDto
import ru.ettransapp.data.network.LiquidTypeDto
import ru.ettransapp.data.TransferRepository
import java.io.File

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: TransferRepository
) : ViewModel() {

    private val _checklistItems = MutableStateFlow<List<ChecklistItemDto>>(emptyList())
    val checklistItems: StateFlow<List<ChecklistItemDto>> = _checklistItems

    private val _liquidTypes = MutableStateFlow<List<LiquidTypeDto>>(emptyList())
    val liquidTypes: StateFlow<List<LiquidTypeDto>> = _liquidTypes

    private val _checkoutState = MutableStateFlow<Int?>(null)
    val checkoutState: StateFlow<Int?> = _checkoutState

    private val _checkinState = MutableStateFlow<Int?>(null)
    val checkinState: StateFlow<Int?> = _checkinState

    init {
        loadChecklistItems()
        loadLiquidTypes()
    }

    fun submitCheckin(
        carId: Int,
        odometer: Int,
        checklist: Map<String, Boolean>,
        liquids: Map<String, Float>,
        photos: List<File>
    ) {
        viewModelScope.launch {
            _checkinState.value = null
            try {
                val sessionId = repository.checkinCar(carId, odometer, checklist, liquids, photos)
                _checkinState.value = sessionId
            } catch (e: Exception) {
                _checkinState.value = null
            }
        }
    }

    private fun loadChecklistItems() = viewModelScope.launch {
        try {
            val items = repository.getChecklistItems()
            _checklistItems.value = items
        } catch (e: Exception) {
            _checklistItems.value = emptyList()
        }
    }

    private fun loadLiquidTypes() = viewModelScope.launch {
        try {
            val types = repository.getLiquidTypes()
            _liquidTypes.value = types
        } catch (e: Exception) {
            _liquidTypes.value = emptyList()
        }
    }

    fun submitCheckout(
        carId: Int,
        odometer: Int,
        checklist: Map<String, Boolean>,
        liquids: Map<String, Float>,
        photos: List<File>
    ) {
        viewModelScope.launch {
            _checkoutState.value = null
            try {
                val sessionId = repository.checkoutCar(carId, odometer, checklist, liquids, photos)
                _checkoutState.value = sessionId
            } catch (e: Exception) {
                _checkoutState.value = null
            }
        }
    }
}
