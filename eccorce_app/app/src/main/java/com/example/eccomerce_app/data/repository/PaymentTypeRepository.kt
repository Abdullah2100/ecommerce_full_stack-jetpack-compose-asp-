package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.dto.PaymentTypeDto
import com.example.eccomerce_app.util.GeneralValue
import com.example.eccomerce_app.util.Secrets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.UnknownHostException

class PaymentTypeRepository(private  val client: HttpClient) {
    suspend fun getPaymentTypes(pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getUrl() + "/api/paymentType/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.OK -> {
                    NetworkCallHandler.Successful(result.body<List<PaymentTypeDto>>())
                }
                HttpStatusCode.NoContent -> {
                    NetworkCallHandler.Error("No Data Found")
                }
                else -> {
                    NetworkCallHandler.Error(result.body<String>())
                }
            }

        } catch (e: UnknownHostException) {
            return NetworkCallHandler.Error(e.message)
        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }


}