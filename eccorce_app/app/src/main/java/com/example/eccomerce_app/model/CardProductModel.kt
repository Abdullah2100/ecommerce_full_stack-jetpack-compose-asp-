package com.example.eccomerce_app.model

import java.util.UUID

data class CardProductModel(
    val id: UUID,
    val productId: UUID,
    val name: String,
    val thumbnail:String,
    val storeId: UUID,
    val price: Int,
    val productVariants:List<ProductVariant>,
    val quantity:Int=1
) 