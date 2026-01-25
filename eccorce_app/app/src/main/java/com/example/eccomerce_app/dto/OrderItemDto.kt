package com.example.eccomerce_app.dto


import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import com.example.hotel_mobile.services.kSerializeChanger.UUIDListKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    val price: Double,
    val quantity:Int,
    val product: OrderProductDto,
    val productVariant:List<OrderVariantDto>?=null,
    val orderItemStatus: String,
    val orderStatusName:String,
)


@Serializable
data class CreateOrderItemDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Price: Int,
    val Quantity: Int,
    @Serializable(with = UUIDKserialize::class)
    val ProductId: UUID,
    @Serializable(with = UUIDListKserialize::class)
    val ProductsVariantId: List<UUID>,
)



@Serializable()
data class OrderItemsStatusEvent(
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderItemId: UUID,
    val status: String
)

@Serializable
data class UpdateOrderItemStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    val Status:Int
)
