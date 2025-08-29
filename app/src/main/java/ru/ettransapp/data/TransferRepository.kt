package ru.ettransapp.data
import javax.inject.Inject

import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody
import ru.ettransapp.data.network.ApiService
import ru.ettransapp.data.network.LoginRequest
import ru.ettransapp.data.network.CarDto
import ru.ettransapp.data.network.TransferSummaryDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import ru.ettransapp.data.network.ChecklistItemDto
import ru.ettransapp.data.network.LiquidTypeDto

class TransferRepository @Inject constructor(private val api: ApiService) {
    suspend fun login(login: String, pass: String) =
        api.login(LoginRequest(login, pass))

    suspend fun loadCars(query: String? = null): List<CarDto> {
        val cars = api.getAvailableCars()
        return if (query.isNullOrBlank()) {
            cars
        } else {
            cars.filter {
                it.plateNumber.contains(query, ignoreCase = true) ||
                it.vin.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun loadMyTransfers() = api.getMyTransfers()

    /**
     * Fetches only active transfer summaries.
     */
    suspend fun getActiveSessions(): List<TransferSummaryDto> =
        api.getActiveSessions()

    suspend fun createTransfer(metadata: RequestBody, photos: List<Part>) =
        api.createTransfer(metadata, photos)

    /**
     * Fetch all checklist items.
     */
    suspend fun getChecklistItems(): List<ChecklistItemDto> =
        api.getChecklistItems()

    /**
     * Fetch all liquid types.
     */
    suspend fun getLiquidTypes(): List<LiquidTypeDto> =
        api.getLiquidTypes()

    /**
     * Checkout a car and return the new session ID.
     */
    suspend fun checkoutCar(
        carId: Int,
        odometer: Int,
        checklist: Map<String, Boolean>,
        liquids: Map<String, Float>,
        photos: List<File>
    ): Int {
        // 1) build metadata JSON
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

        // 2) build multipart photo parts
        val parts = photos.mapIndexed { idx, file ->
            val reqBody = file.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("photos[]", file.name, reqBody)
        }

        // 3) call API and return session ID
        return api.createTransfer(metadata, parts).sessionId
    }

    /**
     * Checkin a car and return the new session ID.
     */
    suspend fun checkinCar(
        carId: Int,
        odometer: Int,
        checklist: Map<String, Boolean>,
        liquids: Map<String, Float>,
        photos: List<File>
    ): Int {
        // 1) build metadata JSON
        val metaJson = Json.encodeToString(
            mapOf(
                "car_id" to carId,
                "type" to "checkin",
                "odometer" to odometer,
                "checklist" to checklist,
                "liquids" to liquids
            )
        )
        val metadata = metaJson.toRequestBody("application/json".toMediaType())

        // 2) build multipart photo parts
        val parts = photos.mapIndexed { idx, file ->
            val reqBody = file.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("photos[]", file.name, reqBody)
        }

        // 3) call API and return session ID
        return api.createTransfer(metadata, parts).sessionId
    }
}
