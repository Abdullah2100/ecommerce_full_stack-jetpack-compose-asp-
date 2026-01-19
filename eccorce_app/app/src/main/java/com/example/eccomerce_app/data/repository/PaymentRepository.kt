package com.example.eccomerce_app.data.repository

import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.dto.StripeClientSecret
import com.example.eccomerce_app.dto.StripeDtoRequest
import com.example.eccomerce_app.util.GeneralValue
import com.example.eccomerce_app.util.Secrets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import java.io.IOException
import java.net.UnknownHostException
class PaymentRepository(private  val httpClient: HttpClient) {
    suspend fun generatePaymentIntent(totalPrice: Double): NetworkCallHandler
    {
        return  try {
            val url = Secrets.getUrl()+"/payment/createCheckout"
            val response = httpClient.post(url){
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${GeneralValue.authData?.refreshToken}"
                    )
                }

                contentType(ContentType.Application.Json)

                setBody(StripeDtoRequest(totalPrice, "usd"))
            }

            if(response.status == HttpStatusCode.OK){
                 NetworkCallHandler.Successful(response.body<StripeClientSecret>())
            }else {
                 NetworkCallHandler.Error(response.body())

            }

        }catch (e: UnknownHostException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: IOException) {

            return NetworkCallHandler.Error(e.message)

        } catch (e: Exception) {

            return NetworkCallHandler.Error(e.message)
        }
    }
}