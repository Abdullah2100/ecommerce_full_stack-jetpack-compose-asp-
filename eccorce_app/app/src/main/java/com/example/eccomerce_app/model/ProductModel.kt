package com.example.e_commercompose.model

import com.example.eccomerce_app.model.ProductVariant
import java.util.UUID

data class ProductModel(
    val id: UUID,
    val name: String,
    val description:String,
    val thumbnail:String,
    val subcategoryId: UUID,
    val storeId: UUID,
    val categoryId: UUID,
    val price: Double,
    val symbol: String,
    val productVariants:List<List<ProductVariant>>?=null,
    val productImages:List<String>
) 