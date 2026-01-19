package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commercompose.dto.ModelToDto.toOrderRequestItemDto
import com.example.e_commercompose.model.Address
import com.example.e_commercompose.model.CartModel
import com.example.eccomerce_app.model.DtoToModel.toOrderItem
import com.example.e_commercompose.model.Order
import com.example.eccomerce_app.dto.CreateOrderDto
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.OrderItemsStatusEvent
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.OrderRepository
import com.example.eccomerce_app.dto.OrderUpdateStatusDto
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Named

class OrderViewModel(
    val orderRepository: OrderRepository,
    @Named("orderHub") val webSocket: HubConnection?

) : ViewModel() {
    private val _orderSocket = MutableStateFlow<HubConnection?>(null)

    private val _orders = MutableStateFlow<List<Order>?>(null)
    val orders = _orders.asStateFlow()

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _orderSocket.emit(webSocket)
                _orderSocket.value?.start()?.blockingAwait()
                _orderSocket.value?.on(
                    "orderStatus",
                    { response ->

                        val orderUpdateData = _orders.value?.map { data ->
                            if (data.id == response.id) {
                                data.copy(status = response.status)
                            } else data
                        }


                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orders.emit(orderUpdateData)
                        }
                    },
                    OrderUpdateStatusDto::class.java
                )

                _orderSocket.value?.on(
                    "orderItemsStatusChange",
                    { response ->

                        val orderHolder = _orders.value?.firstOrNull { it.id == response.orderId }
                        if (orderHolder != null) {
                            val orderItemsHolder = orderHolder.orderItems.map { oi ->
                                if (oi.id == response.orderItemId) {
                                    oi.copy(orderItemStatus = response.status)
                                } else oi
                            }
                            orderHolder.copy(orderItems = orderItemsHolder)

                        }
                        val userOrderList = _orders.value?.map {
                            if (it.id == response.orderId && orderHolder != null)
                                it.copy(orderItems = orderHolder.orderItems)
                            else it
                        }

                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orders.emit(userOrderList)
                        }
                    },
                    OrderItemsStatusEvent::class.java
                )

            }

        }
    }


    init {
        connection()
    }

    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_orderSocket.value != null)
                _orderSocket.value!!.stop()
        }
        super.onCleared()
    }


    suspend fun submitOrder(
        cartItems: CartModel,
        userAddress: Address?,
        clearCartData: () -> Unit
    ): String? {
        val result = orderRepository.submitOrder(
            CreateOrderDto(
                Longitude = userAddress?.longitude
                    ?: 0.0,
                Latitude = userAddress?.latitude
                    ?: 0.0,
                Items = cartItems.cartProducts.map { it.toOrderRequestItemDto() },
                TotalPrice = cartItems.totalPrice
            )
        )
        return  when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as OrderDto
                val orderList = mutableListOf<Order>()
                orderList.add(data.toOrderItem())
                if (!_orders.value.isNullOrEmpty()) {
                    orderList.addAll(_orders.value!!)
                }
                _orders.emit(orderList)
                clearCartData()

                 null
            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data as String
                 errorMessage
            }
        }

    }


    fun getMyOrders(
        pageNumber: MutableState<Int>,
        isLoading: MutableState<Boolean>? = null,

        ) {
        if (isLoading != null) isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null)
                delay(500)
            when (val result = orderRepository.getMyOrders(pageNumber.value)) {

                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<OrderDto>
                    val orderList = mutableListOf<Order>()
                    orderList.addAll(data.map { it.toOrderItem() })
                    if (!_orders.value.isNullOrEmpty()) {
                        orderList.addAll(_orders.value!!)
                    }
                    val distinctOrder = orderList.distinctBy { it.id }.toList()
                    _orders.emit(distinctOrder)

                    if (isLoading != null) isLoading.value = false
                    if (data.size == 25)
                        pageNumber.value++
                }

                is NetworkCallHandler.Error -> {
                    if (_orders.value == null) {
                        _orders.emit(emptyList())
                    }
                    val errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (isLoading != null) isLoading.value = false

                }
            }

        }
    }


    suspend fun deleteOrder(orderId: UUID): String? {
        when (val result = orderRepository.deleteOrder(orderId)) {
            is NetworkCallHandler.Successful<*> -> {
                val orderList = _orders.value?.filter { it.id != orderId }

                _orders.emit(orderList)

                return null
            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data as String
                return errorMessage
            }
        }

    }


}