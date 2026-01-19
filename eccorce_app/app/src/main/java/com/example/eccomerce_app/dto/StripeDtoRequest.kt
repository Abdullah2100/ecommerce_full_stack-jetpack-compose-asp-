package com.example.eccomerce_app.dto

import kotlinx.serialization.Serializable

@Serializable
data class StripeDtoRequest(val amount: Double, val currency:String)

@Serializable
data class StripeClientSecret(val client_secret:String)
