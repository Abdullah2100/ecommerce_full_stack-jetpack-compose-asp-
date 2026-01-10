package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.model.DtoToModel.toOrderItem
import com.example.e_commercompose.model.OrderItem
import com.example.eccomerce_app.dto.OrderDto
import com.example.eccomerce_app.dto.OrderItemDto
import com.example.eccomerce_app.dto.OrderItemsStatusEvent
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.OrderItemRepository
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

class OrderItemsViewModel(
    val orderItemRepository: OrderItemRepository,
    @Named("orderItemHub") val orderItmeHub: HubConnection?,
    @Named("orderHub") val orderHub: HubConnection?,
) : ViewModel() {

    private val _orderItemSocket = MutableStateFlow<HubConnection?>(null)
    private val _orderSocket = MutableStateFlow<HubConnection?>(null)


    private val _orderItemForMyStore = MutableStateFlow<List<OrderItem>?>(null)
    val orderItemForMyStore = _orderItemForMyStore.asStateFlow()

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun connection() {

        if (orderItmeHub != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _orderItemSocket.emit(orderItmeHub)
                _orderSocket.emit(orderHub)

                _orderItemSocket.value?.start()?.blockingAwait()
                _orderSocket.value?.start()?.blockingAwait()
                _orderSocket.value?.on(
                    "orderStatus",
                    { response ->

                        val orderUpdateData = _orderItemForMyStore.value?.map { data ->
                            if (data.orderId == response.id) {
                                data.copy(orderStatusName = response.status)
                            } else data
                        }


                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orderItemForMyStore.emit(orderUpdateData)
                        }
                    },
                    OrderUpdateStatusDto::class.java
                )

                _orderItemSocket.value?.on(
                    "orderExceptedByAdmin",
                    { response ->
                        val orderItemList = mutableListOf<OrderItem>()


                        if (_orderItemForMyStore.value != null) {
                            orderItemList.addAll(_orderItemForMyStore.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            _orderItemForMyStore.emit(orderItemList.distinctBy { it.id }.toList())
                        }
                    },
                    OrderDto::class.java
                )
                _orderItemSocket.value?.on(
                    "orderItemsStatusChange",
                    { response ->
                        val myStoreOrderItemHolder = _orderItemForMyStore.value?.map {
                            if (it.id == response.orderItemId) {
                                it.copy(orderItemStatus = response.status)

                            } else it
                        }


                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            if (myStoreOrderItemHolder?.isNotEmpty() == true)
                                _orderItemForMyStore.emit(myStoreOrderItemHolder)
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
            if (_orderItemSocket.value != null) {
                _orderItemSocket.value?.stop()
                _orderSocket.value?.stop()
            }
        }
        super.onCleared()
    }

    fun getMyOrderItemBelongToMyStore(
        pageNumber: Int,
        isLoading: Boolean? = null,
        updatePageNumber: ((number: Int) -> Unit)? = null,
        updateLoadingState: ((state: Boolean) -> Unit)? = null
    ) {

        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (isLoading != null) {
                updateLoadingState?.invoke(true)
                delay(500)
            }
            when (val result = orderItemRepository.getMyOrderItemForStoreId(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<OrderItemDto>
                    val orderItemList = mutableListOf<OrderItem>()
                    orderItemList.addAll(data.map { it.toOrderItem() })
                    if (!_orderItemForMyStore.value.isNullOrEmpty()) {
                        orderItemList.addAll(_orderItemForMyStore.value!!)
                    }
                    val distinctOrderItem = orderItemList.distinctBy { it.id }.toList()
                    _orderItemForMyStore.emit(distinctOrderItem)
                    if (isLoading != null) updateLoadingState?.invoke(false)
                    if (data.size == 25) updatePageNumber?.invoke(pageNumber + 1)

                }

                is NetworkCallHandler.Error -> {
                    if (_orderItemForMyStore.value == null) {
                        _orderItemForMyStore.emit(emptyList())
                    }
                    if (isLoading != null) updateLoadingState?.invoke(false)

                    val errorMessage = result.data as String
                    Log.d("errorFromGettingOrder", errorMessage)
                    if (_orderItemForMyStore.value == null) {
                        _orderItemForMyStore.emit(emptyList())
                    }
                }
            }

        }
    }


    suspend fun updateOrderItemStatusFromStore(id: UUID, status: Int): String? {
        when (val result = orderItemRepository.updateOrderItemStatus(id, status)) {
            is NetworkCallHandler.Successful<*> -> {
                val orderItemStatus = when (status) {
                    0 -> "Excepted"
                    else -> "Cancelled"
                }
                val updateOrderItem = _orderItemForMyStore.value?.map { it ->
                    if (it.id == id) {
                        it.copy(orderItemStatus = orderItemStatus)
                    } else {
                        it
                    }
                }
                _orderItemForMyStore.emit(updateOrderItem)
                return null
            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data as String
                Log.d("errorFromGettingOrder", errorMessage)
                if (_orderItemForMyStore.value == null) {
                    _orderItemForMyStore.emit(emptyList())
                }
                return errorMessage
            }
        }

    }


}