package ru.ettransapp.ui.screen

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.ettransapp.ui.viewmodel.CheckoutViewModel
import java.io.File

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    carId: Int,
    onDone: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val checklistItems by viewModel.checklistItems.collectAsState(initial = emptyList())
    val liquidTypes by viewModel.liquidTypes.collectAsState(initial = emptyList())

    var odometer by rememberSaveable { mutableStateOf("") }
    val checklistValues = remember { mutableStateMapOf<String, Boolean>() }
    val liquidValues = remember { mutableStateMapOf<String, String>() }
    val photoFiles = remember { mutableStateListOf<File>() }

    // Image picker
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val input = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "photo_${photoFiles.size}.jpg")
            input?.use { stream -> file.outputStream().use { out -> stream.copyTo(out) } }
            photoFiles.add(file)
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Приём машины") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { launcher.launch("image/*") }) {
                Text("+Фото")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = odometer,
                    onValueChange = { odometer = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Одометр (км)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            items(checklistItems) { item ->
                val checked = checklistValues[item.code] ?: false
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checklistValues[item.code] = it }
                    )
                    Text(text = item.description)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            items(liquidTypes, key = { it.name }) { liquid ->
                var text by rememberSaveable(liquid.name) { mutableStateOf(liquidValues[liquid.name] ?: "") }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(liquid.name + " (${liquid.unit})")
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            val filtered = it.filter { ch -> ch.isDigit() || ch == '.' }
                            text = filtered
                            liquidValues[liquid.name] = filtered
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            items(photoFiles) { file ->
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 4.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.submitCheckout(
                                carId = carId,
                                odometer = odometer.toInt(),
                                checklist = checklistValues.toMap(),
                                liquids = liquidValues.mapValues { it.value.toFloatOrNull() ?: 0f },
                                photos = photoFiles.toList()
                            )
                            onDone()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Завершить приём")
                }
            }
        }
    }
}
