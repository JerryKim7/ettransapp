package ru.ettransapp.data.model

import com.google.gson.annotations.SerializedName

data class Car(
    val id: Int,
    @SerializedName("plate_number") val plateNumber: String,
    val vin: String?,
    val make: String?,
    val model: String?,
    val year: String?
)