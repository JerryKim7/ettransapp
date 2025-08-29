package ru.ettransapp.ui.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.ettransapp.data.TransferRepository
import ru.ettransapp.data.network.TransferSummaryDto

sealed class SessionUiState<out T> {
    data object Loading : SessionUiState<Nothing>()
    data class Success<T>(val data: T) : SessionUiState<T>()
    data class Error(val message: String) : SessionUiState<Nothing>()
}

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    private val repository: TransferRepository
) : ViewModel() {

    // Состояние списка сессий
    private val _sessions = MutableStateFlow<SessionUiState<List<TransferSummaryDto>>>(SessionUiState.Loading)
    val sessions: StateFlow<SessionUiState<List<TransferSummaryDto>>> = _sessions

    init {
        loadSessions()
    }

    /** Загружает список активных TransferSummaryDto для текущего пользователя */
    private fun loadSessions() {
        viewModelScope.launch {
            _sessions.value = SessionUiState.Loading
            try {
                val list = repository.getActiveSessions()
                _sessions.value = SessionUiState.Success(list)
            } catch (e: Exception) {
                _sessions.value = SessionUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
