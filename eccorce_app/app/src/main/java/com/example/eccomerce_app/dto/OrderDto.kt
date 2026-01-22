package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val deliveryFee: Double,
    val userPhone: String,
    val status:String,
    val orderItems:List<OrderItemDto>
)

@Serializable
data class OrderUpdateStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val status:String,
)
@Serializable
data class CreateOrderDto(
    val Longitude: Double,
    val Latitude: Double,
    val TotalPrice: Long,
    val Items: List<CreateOrderItemDto>,
)

@Serializable
data class OrderRequestItemsDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Price: Long,
    val Quantity: Int,
    @Serializable(with = UUIDKserialize::class)
    val ProductId: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val ProductsVariantId: List<UUID>,
)
