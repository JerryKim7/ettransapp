// MainMenuScreen.kt
package ru.ettransapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ettransapp.data.model.Car
import ru.ettransapp.ui.viewmodel.CarsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainMenuScreen(
    viewModel: CarsViewModel = hiltViewModel(),
    onSelectCar: (Car) -> Unit = {},
    onShowTransfers: () -> Unit = {},
    onShowSupport: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val cars by viewModel.cars.observeAsState(emptyList())

    LaunchedEffect(Unit) { viewModel.loadCars() }
    val filteredCars = if (query.isBlank()) emptyList() else cars.filter { car ->
        car.plateNumber.contains(query, ignoreCase = true)
        || (car.vin?.contains(query, ignoreCase = true) ?: false)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Главное меню") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Поле поиска
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск VIN / номер") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Две неактивные кнопки
            Button(
                onClick = onShowTransfers,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Последние передачи")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onShowSupport,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Поддержка")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Список машин
            if (filteredCars.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (query.isBlank()) "Введите VIN или номер для поиска" else "По Вашему запросу ничего не найдено",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCars) { car ->
                        Card(
                            elevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCar(car) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = car.plateNumber,
                                    style = MaterialTheme.typography.h6
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = car.vin ?: "—",
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}