package ru.ettransapp.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import ru.ettransapp.data.network.ChecklistItemDto
import ru.ettransapp.data.network.LiquidTypeDto

data class LoginRequest(val login: String, val password: String)
data class LoginResponse(val status: String)

interface ApiService {
    @POST("auth.php?action=login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("cars.php")
    suspend fun getAvailableCars(
        @Query("plate") plate: String? = null,
        @Query("vin") vin: String? = null,
        @Query("status") status: String = "available"
    ): List<CarDto>

    @GET("cars.php")
    suspend fun getAllCars(@Query("status") status: String? = null): List<CarDto>

    @GET("cars.php")
    suspend fun getCarById(@Query("id") id: Int): CarDto

    @Multipart
    @POST("transfers.php")
    suspend fun createTransfer(
        @Part("metadata") metadata: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): CreateTransferResponse

    @GET("transfers.php")
    suspend fun getMyTransfers(): List<TransferSummaryDto>

    /**
     * Fetches all active transfer sessions for the current user.
     */
    @GET("transfers.php")
    suspend fun getActiveSessions(
        @Query("status") status: String = "active"
    ): List<TransferSummaryDto>

    @GET("checklist_items.php")
    suspend fun getChecklistItems(): List<ChecklistItemDto>

    @GET("liquid_types.php")
    suspend fun getLiquidTypes(): List<LiquidTypeDto>

    // другие методы по аналогии…
}

data class CarDto(
    val id: Int,
    @SerializedName("plate_number")
    val plateNumber: String,
    val vin: String,
    val make: String,
    val model: String,
    val year: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
data class TransferSummaryDto(
    val id: Int,
    @SerializedName("plate_number")
    val plateNumber: String,
    val type: String,
    val status: String,
    @SerializedName("requested_at")
    val requestedAt: String
)
data class CreateTransferResponse(
    val status: String,
    @SerializedName("session_id")
    val sessionId: Int
)