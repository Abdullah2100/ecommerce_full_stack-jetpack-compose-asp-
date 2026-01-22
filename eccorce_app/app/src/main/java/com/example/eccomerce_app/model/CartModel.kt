package com.example.e_commercompose.model

import com.example.eccomerce_app.model.CardProductModel
import java.util.UUID

data class CartModel(
    val totalPrice: Long,
    val longitude: Double,
    val latitude: Double,
    val userId: UUID,
    val cartProducts:List<CardProductModel>
)