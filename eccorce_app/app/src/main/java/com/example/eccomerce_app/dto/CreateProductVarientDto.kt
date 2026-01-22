package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import java.util.UUID
import kotlinx.serialization.Serializable


@Serializable()
data class ProductVarientDto(
    @kotlinx.serialization.Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name: String,
    val percentage: Double,
    @Serializable(with = UUIDKserialize::class)
    val variantId: UUID,
)

@Serializable
data class CreateProductVarientDto(
    val Name: String,
    val Percentage: Double?,
    @Serializable(with= UUIDKserialize::class)
    val VariantId: UUID,
)
