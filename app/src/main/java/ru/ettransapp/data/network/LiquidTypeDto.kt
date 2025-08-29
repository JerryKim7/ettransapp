package ru.ettransapp.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LiquidTypeDto(
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("unit")
    val unit: String
)