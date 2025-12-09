package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class CurrencyDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    var name: String,
    var symbol: String,
    var value: Int,
    var isDefault: Boolean
)
