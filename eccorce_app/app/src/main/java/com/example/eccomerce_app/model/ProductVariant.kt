package com.example.eccomerce_app.model

import java.util.UUID

data class ProductVariant(
    val id: UUID,
    val name:String,
    val percentage: Short,
    val variantId: UUID,
    )
