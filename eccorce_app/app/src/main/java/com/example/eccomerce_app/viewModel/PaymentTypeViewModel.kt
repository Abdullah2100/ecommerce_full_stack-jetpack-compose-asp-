package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.PaymentTypeRepository
import com.example.eccomerce_app.dto.PaymentTypeDto
import com.example.eccomerce_app.model.DtoToModel.toPaymentType
import com.example.eccomerce_app.model.PaymentType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentTypeViewModel(private val paymentTypeRepository: PaymentTypeRepository): ViewModel() {
    private val _paymentTypes = MutableStateFlow<List<PaymentType>>(emptyList())
     val paymentTypes = _paymentTypes.asStateFlow()

    private val _pageNum = MutableStateFlow<Int>(1)
    val pageNum = _pageNum.asStateFlow()
    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun getPaymentType(pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            when (val result = paymentTypeRepository.getPaymentTypes(pageNumber)) {
                is NetworkCallHandler.Successful<*> -> {
                    val paymentTypesDto = result.data as List<PaymentTypeDto>

                    val mutablePaymentTypes = mutableListOf<PaymentType>()

                    if (pageNumber != 1 && _paymentTypes.value.isEmpty()) {
                        mutablePaymentTypes.addAll(_paymentTypes.value.toList())
                    }

                    if (paymentTypesDto.isNotEmpty())
                        mutablePaymentTypes.addAll(
                            paymentTypesDto.map { it.toPaymentType() }.toList()
                        )

                    if (mutablePaymentTypes.isNotEmpty()) {
                        _paymentTypes.emit(
                            mutablePaymentTypes
                        )
                    }

                    if(paymentTypesDto.count()==25){
                        _pageNum.emit(_pageNum.value+1)
                    }

                }

                is NetworkCallHandler.Error -> {
                    if (_paymentTypes.value.isEmpty())
                        _paymentTypes.emit(mutableListOf())
                }
            }
        }
    }


}