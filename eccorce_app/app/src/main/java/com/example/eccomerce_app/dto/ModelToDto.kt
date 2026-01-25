package com.example.e_commercompose.dto

import com.example.eccomerce_app.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.eccomerce_app.model.ProductVariant
import com.example.eccomerce_app.model.ProductVariantSelection
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.CreateOrderItemDto
import com.example.eccomerce_app.dto.CreateProductVariantDto
import com.example.eccomerce_app.dto.UpdateSubCategoryDto

object ModelToDto {



    fun SubCategoryUpdate.toUpdateSubCategoryDto():UpdateSubCategoryDto{
        return UpdateSubCategoryDto(
            name=this.name,
            id=this.id,
            categoryId=this.cateogyId
        )
    }

    fun ProductVariantSelection.toProductVariantRequestDto(): CreateProductVariantDto{
        return CreateProductVariantDto(
            Name = this.name,
            Percentage = this.percentage,
            VariantId =this.variantId
        )
    }
    fun List<List<ProductVariant>>.toListOfProductVariant(): List<ProductVariantSelection> {
        return   this.map{it->it.map {
                data->
            ProductVariantSelection(name = data.name, percentage = data.percentage, variantId = data.variantId)
        }}.flatten()
    }


    fun CardProductModel.toOrderRequestItemDto(): CreateOrderItemDto{
        return CreateOrderItemDto(
            StoreId =  this.storeId,
            ProductId = this.productId,
            Price = this.price,
            Quantity = this.quantity,
            ProductsVariantId =  this.productVariants.map { it.id }
        )
    }

    fun SubCategoryUpdate.toSubCategoryUpdateDto(): UpdateSubCategoryDto{
        return UpdateSubCategoryDto(
            id=this.id,
            categoryId = this.cateogyId,
            name = this.name,
        )
    }

}