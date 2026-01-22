package com.example.eccomerce_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.PaymentRepository
import com.example.eccomerce_app.dto.StripeClientSecret

class PaymentViewModel(private val paymentRepository: PaymentRepository): ViewModel() {


    suspend fun  submitOrderToStripe(totalPrice: Long):String?{
      return  when(val result = paymentRepository.generatePaymentIntent(totalPrice)){
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as StripeClientSecret
                data.client_secret
            }
            is NetworkCallHandler.Error -> {
               null
            }
        }
    }
}