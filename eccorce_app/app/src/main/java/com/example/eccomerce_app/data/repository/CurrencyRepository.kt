package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.CreateAddressDto
import com.example.eccomerce_app.dto.UpdateAddressDto
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.dto.CurrencyDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.IOException
import java.net.UnknownHostException
import java.util.UUID

class CurrencyRepository(val client: HttpClient) {

     suspend fun getStoreCurrencies(pageNumber: Int): NetworkCallHandler {
        return try {
            val fullUrl = Secrets.getBaseUrl() + "/Currencies/all/${pageNumber}"
            val result = client.get(fullUrl) {
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${General.authData.value?.refreshToken}"
                    )
                }
            }

            when (result.status) {
                HttpStatusCode.Companion.OK -> {
                    NetworkCallHandler.Successful(result.body<List<CurrencyDto>>())
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