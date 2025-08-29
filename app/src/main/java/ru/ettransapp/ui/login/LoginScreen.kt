package ru.ettransapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var login by remember { mutableStateOf("") }
    var pass  by remember { mutableStateOf("") }

    when(state) {
        is LoginUiState.Success -> onLoginSuccess()
        else -> Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("ETT Приложение", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 32.dp))
        OutlinedTextField(
            value = login, onValueChange = { login = it },
            label = { Text("Логин") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            label = { Text("Пароль") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.doLogin(login, pass) },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state is LoginUiState.Loading) CircularProgressIndicator(modifier=Modifier.size(24.dp))
            else Text("Войти")
        }
        if (state is LoginUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text((state as LoginUiState.Error).msg, color = MaterialTheme.colorScheme.error)
        }
    }
}