package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.model.DtoToModel.toDeliveryInfo
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.DeliveryRepository
import com.example.eccomerce_app.dto.DeliveryDto
import com.example.eccomerce_app.model.Delivery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class DeliveryViewModel(
    private val deliveryRepository: DeliveryRepository,
) : ViewModel() {

    val deliveries = MutableStateFlow<List<Delivery>?>(null)
    suspend fun createDelivery(userId: UUID): String? {
        return when (val result = deliveryRepository.createNewDelivery(userId)) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as? DeliveryDto
                val newDeliveriesList = mutableListOf<Delivery>()
                if (data != null) {
                    newDeliveriesList.add(data.toDeliveryInfo())
                }
                if (deliveries.value != null)
                    newDeliveriesList.addAll(deliveries.value!!)

                deliveries.emit(newDeliveriesList)

                null;
            }

            is NetworkCallHandler.Error -> {
                return result.data
            }
        }
    }

    fun getDeliveryBelongToStore(
        pageNumber: Int,
        updatePageNumber: ((value: Int) -> Unit)? = null
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = deliveryRepository.getDeliveriesBelongToStore(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as? List<DeliveryDto>
                    val newDeliveriesList = mutableListOf<Delivery>()

                    if (data != null) {
                        updatePageNumber?.invoke(pageNumber + 1);
                        newDeliveriesList.addAll(data.map { it.toDeliveryInfo() })
                    }
                    if (deliveries.value != null)
                        newDeliveriesList.addAll(deliveries.value!!)

                    val distinctDelivery = newDeliveriesList.distinctBy { it.id }

                    deliveries.emit(distinctDelivery)

                }

                is NetworkCallHandler.Error -> {
                    Log.d(
                        "Error",
                        "This Error from getting delivery Belong to my Store ${result.data}"
                    )
                }
            }
        }
    }
}
