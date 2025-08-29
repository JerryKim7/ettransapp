package ru.ettransapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.ettransapp.data.CarRepository
import ru.ettransapp.data.model.Car
import android.util.Log

@HiltViewModel
class CarsViewModel @Inject constructor(
    private val repo: CarRepository
) : ViewModel() {
    companion object {
        private const val TAG = "CarsViewModel"
    }

    private val _cars = MutableLiveData<List<Car>>()
    val cars: LiveData<List<Car>> = _cars
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadCars()
    }

    fun loadCars(query: String? = null) = viewModelScope.launch {
        _loading.value = true
        try {
            // treat empty or blank query as no filter
            val plateQuery = query?.takeIf { it.isNotBlank() }
            Log.d(TAG, "loadCars() called with query=$query")
            val result = repo.loadCars(plateQuery)
            Log.d(TAG, "Received ${result.size} cars: $result")
            _cars.value = result
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        } finally {
            _loading.value = false
        }
    }
}