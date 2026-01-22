package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.model.Address
import com.example.eccomerce_app.model.CardProductModel
import com.example.e_commercompose.model.CartModel
import com.example.eccomerce_app.model.StoreModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Math.toRadians
import java.util.UUID
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class CartViewModel() : ViewModel() {


    val cartItems = MutableStateFlow(
        CartModel(
            0,
            0.0,
            0.0,
            UUID.randomUUID(),
            emptyList()
        )
    )
   private  val _distance = MutableStateFlow(0.0)
    val distance = _distance.asStateFlow()


 private    val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun addToCart(product: CardProductModel) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val cardProducts = mutableListOf<CardProductModel>()

            cardProducts.add(product)
            cardProducts.addAll(cartItems.value.cartProducts)
            val productLength = cardProducts.size
            val distanceProduct = cardProducts.distinctBy { it.productVariants }.toList()
            val distanceProductsLength = distanceProduct.size

            if (productLength == distanceProductsLength) {
                var variantPrice = product.price
                product.productVariants.forEach { it ->
                    variantPrice *= it.percentage
                }
                val price = ((cartItems.value.totalPrice)) + variantPrice
                val copyCardItem = cartItems.value.copy(
                    totalPrice = price, cartProducts = cardProducts
                )
                cartItems.emit(copyCardItem)
            }

        }
    }


    fun increaseCardItem(productId: UUID) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            var copyProduct: CardProductModel? = null
            val productHolders = cartItems.value.cartProducts.map { product ->
                if (product.id == productId) {
                    copyProduct = product.copy(quantity = product.quantity + 1)
                    copyProduct
                } else {
                    product
                }
            }
            var variantProduct = copyProduct!!.price


            copyProduct.productVariants.forEach { it ->
                variantProduct = variantProduct * it.percentage
            }
            val price = (cartItems.value.totalPrice) + (variantProduct)


            val copyCardItem =
                cartItems.value.copy(cartProducts = productHolders, totalPrice = price)

            cartItems.emit(copyCardItem)

        }
    }


    fun decreaseCardItem(productId: UUID) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            var productList = cartItems.value.cartProducts
            val firstProduct = productList.first { it.id == productId }
            var variantPrice = firstProduct.price


            firstProduct.productVariants.forEach { it ->
                variantPrice *= it.percentage
            }

            val totalPrice = (cartItems.value.totalPrice) - (variantPrice)

            productList = when (firstProduct.quantity > 1) {
                true -> {
                    cartItems.value.cartProducts.map { product ->
                        if (product.id == productId) {
                            product.copy(quantity = product.quantity - 1)
                        } else {
                            product
                        }
                    }
                }

                else -> {
                    cartItems.value.cartProducts.filter { it.id != productId }
                }
            }

            val copyCardItem =
                cartItems.value.copy(cartProducts = productList, totalPrice = totalPrice)

            cartItems.emit(copyCardItem)

        }
    }

    fun removeItemFromCard(productId: UUID) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            var productList = cartItems.value.cartProducts
            val firstProduct = productList.first { it.id == productId }
            var variantPrice = firstProduct.price


            firstProduct.productVariants.forEach { it ->
                variantPrice *= it.percentage
            }

            val totalPrice =
                (cartItems.value.totalPrice) - (variantPrice)

            productList = cartItems.value.cartProducts.filter { it.id != productId }

            val copyCardItem =
                cartItems.value.copy(cartProducts = productList, totalPrice = totalPrice)

            cartItems.emit(copyCardItem)

        }
    }


    fun clearCart() {
        viewModelScope.launch {
            cartItems.emit(
                CartModel(
                    totalPrice = 0,
                    latitude = 0.0,
                    longitude = 0.0,
                    userId = UUID.randomUUID(),
                    cartProducts = listOf()
                )
            )
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // km
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))
        return r * c
    }
    suspend fun calculateOrderDistanceToUser(
        stores: List<StoreModel>?,
        currentAddress: Address?
    ) {

        if (stores == null || currentAddress == null) return
        var copyDistance = 0.0
        cartItems.value.cartProducts.distinctBy { it.storeId }.forEach { product ->

           val distanceKm = haversineDistance(stores.firstOrNull { it.id == product.storeId }!!.latitude,stores.firstOrNull { it.id == product.storeId }!!.longitude,currentAddress.latitude,currentAddress.longitude)
            copyDistance = ceil(max(1.0, (distanceKm)))

        }

        _distance.emit(copyDistance)
    }


}

