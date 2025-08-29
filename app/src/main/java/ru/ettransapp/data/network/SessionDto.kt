package ru.ettransapp.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
/**
 * Data Transfer Object representing an active or requested transfer session.
 */
data class SessionDto(
    @SerialName("id")
    val id: Int,

    @SerialName("plate_number")
    val plateNumber: String,

    @SerialName("type")
    val type: String,

    @SerialName("status")
    val status: String,

    @SerialName("requested_at")
    val requestedAt: String
)