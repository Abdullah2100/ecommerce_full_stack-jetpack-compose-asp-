package com.example.eccomerce_app.model

import com.example.e_commercompose.model.Address
import com.example.e_commercompose.model.BannerModel
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.GeneralSetting
import com.example.e_commercompose.model.Order
import com.example.e_commercompose.model.OrderItem
import com.example.e_commercompose.model.OrderProduct
import com.example.e_commercompose.model.OrderVariant
import com.example.e_commercompose.model.ProductModel
import com.example.e_commercompose.model.ProductVariant
import com.example.e_commercompose.model.SubCategory
import com.example.e_commercompose.model.UserModel
import com.example.e_commercompose.model.VarirntModel
import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.dto.CategoryDto
import com.example.eccomerce_app.dto.DeliveryDto
import com.example.eccomerce_app.dto.DeliveryUserInfoDto
import com.example.eccomerce_app.dto.StoreDto
import com.example.eccomerce_app.dto.SubCategoryDto
import com.example.eccomerce_app.dto.VariantDto
import com.example.eccomerce_app.dto.GeneralSettingDto
import com.example.eccomerce_app.dto.OrderProductDto
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.OrderVariantDto
import com.example.eccomerce_app.dto.*
import com.example.eccomerce_app.util.Secrets
import kotlin.text.replace

object DtoToModel {
    fun AddressDto.toAddress(): Address {
        return Address(
            id = this.id,
            title = this.title,
            latitude = this.latitude,
            longitude = this.longitude,
            isCurrent = this.isCurrent ?: false
        )
    }

    fun CategoryDto.toCategory(): Category {
        return Category(
            id = this.id,
            name = this.name,
            image = this.image.replace("localhost", Secrets.imageUrl)
        )
    }

    fun SubCategoryDto.toSubCategory(): SubCategory {
        return SubCategory(
            id = this.id,
            name = this.name,
            categoryId = this.categoryId,
            storeId = this.storeId
        )
    }


    fun StoreDto.toStore(): StoreModel {
        return StoreModel(
            id = this.id,
            name = this.name,
            userId = this.userId,
            pigImage = this.wallpaperImage.replace("localhost", Secrets.imageUrl),
            smallImage = this.smallImage.replace("localhost", Secrets.imageUrl),
            latitude = this.latitude,
            longitude = this.longitude
        )
    }

    fun UserDto.toUser(): UserModel {
        return UserModel(
            id = this.id,
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail = if (!this.thumbnail.isEmpty()) this.thumbnail.replace(
                "localhost",
                Secrets.imageUrl
            ) else "",
            address = this.address?.map { it.toAddress() }?.toList(),
            storeId = this.storeId
        )
    }


    fun DeliveryUserInfoDto.toDeliveryUserInfo(): DeliveryUserInfo {
        return DeliveryUserInfo(
            name = this.name,
            phone = this.phone,
            email = this.email,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "localhost",
                Secrets.imageUrl
            ),
        )
    }

    fun DeliveryDto.toDeliveryInfo(): Delivery {
        return Delivery(
            id = this.id,
            userId = this.userId,
            isAvailable = this.isAvailable,
            thumbnail = if (this.thumbnail.isNullOrEmpty()) "" else this.thumbnail.replace(
                "localhost",
                "10.0.2.2"
            ),
            address = this.address?.toAddress(),
            user = this.user.toDeliveryUserInfo()

        )

    }

    fun BannerDto.toBanner(): BannerModel {
        return BannerModel(
            id = this.id,
            image = if (this.image.isNotEmpty()) this.image.replace(
                "localhost",
                Secrets.imageUrl
            ) else "",
            storeId = this.storeId
        )
    }

    fun VariantDto.toVariant(): VarirntModel {
        return VarirntModel(
            id = this.id,
            name = this.name
        )
    }

    fun ProductVarientDto.toProductVariant(): ProductVariant {
        return ProductVariant(
            id = this.id,
            name = this.name,
            percentage = this.percentage,
            variantId = this.variantId
        )
    }

    fun ProductDto.toProduct(): ProductModel {
        return ProductModel(
            id = this.id,
            name = this.name,
            description = this.description,
            thumbnail = if (this.thumbnail.isNotEmpty()) this.thumbnail.replace(
                "localhost",
                Secrets.imageUrl
            ) else "",
            subcategoryId = this.subcategoryId,
            storeId = this.storeId,
            price = this.price,
            symbol = this.symbol,
            categoryId = this.categoryId,
            productVariants = this.productVariants?.map {data->
                data.map { it.toProductVariant() }
            },
            productImages = this.productImages.map { it ->
                if (it.isNotEmpty()) it.replace(
                    "localhost",
                    Secrets.imageUrl
                ) else ""
            }
        )
    }

    fun OrderVariantDto.toOrderVariant(): OrderVariant {
        return OrderVariant(
            variantName = this.variantName,
            productVariantName = this.productVariantName
        )
    }

    fun OrderProductDto.toOrderProduct(): OrderProduct {
        return OrderProduct(
            id = this.id,
            name = this.name,
            thumbnail = if (this.thmbnail != null && this.thmbnail.isNotEmpty()) this.thmbnail.replace(
                "localhost",
                Secrets.imageUrl
            ) else ""
        )
    }

    fun OrderItemDto.toOrderItem(): OrderItem {
        return OrderItem(
            id = this.id,
            quantity = this.quantity,
            price = this.price,
            product = this.product.toOrderProduct(),
            productVariant = if (this.productVariant.isNullOrEmpty())
                listOf()
            else this.productVariant.map { it.toOrderVariant() },
            orderItemStatus = this.orderItemStatus,
            orderStatusName = this.orderStatusName,
            orderId = this.orderId
        )
    }

    fun OrderDto.toOrderItem(): Order {
        return Order(
            id = this.id,
            latitude = this.latitude,
            longitude = this.longitude,
            deliveryFee = this.deliveryFee,
            userPhone = this.userPhone,
            totalPrice = this.totalPrice,
            orderItems = this.orderItems.map { it.toOrderItem() },
            status = this.status
        )
    }

    fun GeneralSettingDto.toGeneralSetting(): GeneralSetting {
        return GeneralSetting(
            name = this.name,
            value = this.value
        )
    }

    fun PaymentTypeDto.toPaymentType(): PaymentType{
        return PaymentType(
            id=this.id,
            name=this.name,
            isHashCheckOperation = this.isHashCheckOperation,
            thumbnail = this.thumbnail.replace("localhost", Secrets.imageUrl)
        )
    }
}