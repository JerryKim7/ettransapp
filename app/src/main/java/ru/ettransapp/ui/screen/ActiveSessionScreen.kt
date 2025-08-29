package ru.ettransapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.ettransapp.data.network.TransferSummaryDto
import ru.ettransapp.ui.viewmodel.ActiveSessionViewModel
import ru.ettransapp.ui.viewmodel.SessionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    navController: NavController,
    viewModel: ActiveSessionViewModel = hiltViewModel()
) {
    // Подписываемся на состояние списка сессий
    val state by viewModel.sessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои сессии") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is SessionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SessionUiState.Error -> {
                    Text(
                        text = (state as SessionUiState.Error).message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SessionUiState.Success -> {
                    val sessions = (state as SessionUiState.Success<List<TransferSummaryDto>>).data
                    if (sessions.isEmpty()) {
                        Text(
                            "Нет активных сессий",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sessions) { session ->
                                SessionListItem(
                                    session = session,
                                    onClick = {
                                        // Предполагаем маршрут "session/{id}"
                                        navController.navigate("session/${session.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionListItem(
    session: TransferSummaryDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = session.plateNumber,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Тип: ${session.type}, Статус: ${session.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Дата: ${session.requestedAt}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}