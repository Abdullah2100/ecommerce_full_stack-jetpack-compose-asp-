package com.example.eccomerce_app.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class PaymentType(
    val id: UUID,
    val name: String,
    val isHashCheckOperation: Boolean,
    val thumbnail: String
)
