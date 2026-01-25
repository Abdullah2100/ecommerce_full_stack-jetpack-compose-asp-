package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import java.util.UUID
import kotlinx.serialization.Serializable


@Serializable()
data class ProductVariantDto(
    @kotlinx.serialization.Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name: String,
    val percentage: Int,
    @Serializable(with = UUIDKserialize::class)
    val variantId: UUID,
)

@Serializable
data class CreateProductVariantDto(
    val Name: String,
    val Percentage: Int?,
    @Serializable(with= UUIDKserialize::class)
    val VariantId: UUID,
)
