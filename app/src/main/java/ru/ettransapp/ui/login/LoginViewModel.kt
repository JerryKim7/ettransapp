package ru.ettransapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ettransapp.data.TransferRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repo: TransferRepository) : ViewModel() {
    val uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    fun doLogin(login: String, password: String) {
        uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val resp = repo.login(login, password)
                if (resp.status == "ok") {
                    uiState.value = LoginUiState.Success
                } else {
                    uiState.value = LoginUiState.Error("Неверные данные")
                }
            } catch (e: Exception) {
                uiState.value = LoginUiState.Error(e.localizedMessage ?: "Ошибка сети")
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val msg: String) : LoginUiState()
}