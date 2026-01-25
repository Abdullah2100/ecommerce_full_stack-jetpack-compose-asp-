package com.example.eccomerce_app.model

import java.util.UUID

data class ProductVariantSelection(
    val name: String,
    val percentage: Int?,
    val variantId: UUID,
    )
