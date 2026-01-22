package com.example.e_commercompose.dto

import com.example.eccomerce_app.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.eccomerce_app.model.ProductVariant
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.CreateOrderItemDto
import com.example.eccomerce_app.dto.CreateProductVarientDto
import com.example.eccomerce_app.dto.UpdateSubCategoryDto

object ModelToDto {



    fun SubCategoryUpdate.toUpdateSubCategoryDto():UpdateSubCategoryDto{
        return UpdateSubCategoryDto(
            name=this.name,
            id=this.id,
            categoryId=this.cateogyId
        )
    }

    fun ProductVarientSelection.toProdcutVarientRequestDto(): CreateProductVarientDto{
        return CreateProductVarientDto(
            Name = this.name,
            Percentage = this.percentage,
            VariantId =this.variantId
        )
    }
    fun List<List<ProductVariant>>.toListOfProductVarient(): List<ProductVarientSelection> {
        return   this.map{it->it.map {
                data->
            ProductVarientSelection(name = data.name, percentage = data.percentage, variantId = data.variantId)

        }}.flatten()
    }



    fun CartModel.toOrderRequestDto(): CreateOrderDto{
        return CreateOrderDto(
            Items = this.cartProducts.map { it.toOrderRequestItemDto() },
            TotalPrice = this.totalPrice ,
            Latitude = this.latitude,
            Longitude=this.longitude
        )
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