package ru.ettransapp.data

import javax.inject.Inject
import javax.inject.Singleton

import ru.ettransapp.data.network.ApiService
import ru.ettransapp.data.model.Car
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Singleton
class CarRepository @Inject constructor(
    private val api: ApiService
) {
    companion object {
        private const val TAG = "CarRepository"
    }

    suspend fun loadCars(query: String? = null): List<Car> {
        Log.d(TAG, "loadCars called with query=$query")
        // fetch full list then filter client-side, with detailed error logging
        val dtos = try {
            val list = api.getAvailableCars()
            Log.d(TAG, "API getAvailableCars returned size=${list.size}")
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cars from API", e)
            emptyList()
        }
        Log.d(TAG, "Fetched ${dtos.size} cars from API.")
        Log.d(TAG, "DTO plateNumbers: ${dtos.map { it.plateNumber }}")
        val result = dtos.map { dto ->
            Car(
                id = dto.id,
                plateNumber = dto.plateNumber,
                vin = dto.vin,
                make = dto.make,
                model = dto.model,
                year = dto.year
            )
        }.filter { car ->
            query.isNullOrBlank() || car.plateNumber.contains(query, ignoreCase = true)
        }
        Log.d(TAG, "Filtered plateNumbers: ${result.map { it.plateNumber }}")
        Log.d(TAG, "Returning ${result.size} cars after filtering by query=$query")
        Log.d(TAG, "Final filtered result count=${result.size}")
        return result
    }

    @Suppress("unused")
    suspend fun checkoutCar(
        carId: Int,
        odometer: Int,
        checklist: Map<String, Boolean>,
        liquids: Map<String, Float>,
        photos: List<File>
    ): Int {
        // 1) сформировать JSON метаданных:
        val metaJson = Json.encodeToString(
            mapOf(
                "car_id" to carId,
                "type" to "checkout",
                "odometer" to odometer,
                "checklist" to checklist,
                "liquids" to liquids
            )
        )
        val metadata = metaJson.toRequestBody("application/json".toMediaType())
        // 2) превратить каждую photo: File в MultipartBody.Part:
        val parts = photos.mapIndexed { _, file ->
            val reqBody = file.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("photos[]", file.name, reqBody)
        }
        val resp = api.createTransfer(metadata, parts)
        return resp.sessionId
    }
}