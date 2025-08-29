package ru.ettransapp.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChecklistItemDto(
    val id: Int,
    @SerialName("category_id") val categoryId: Int,
    val code: String,
    val description: String,
    val severity: String
)