package com.example.eccomerce_app.di

import android.util.Log
import com.example.eccomerce_app.data.Room.Dao.AuthDao
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module



fun provideHttpClient(authDao:AuthDao): HttpClient {
    return HttpClient(Android) {

        engine {
            connectTimeout = 60_000
        }

        install(WebSockets)

        install(HttpTimeout)

        install(Auth) {
            bearer {
                //  sendWithoutRequest { true }
//                loadTokens {
//                    BearerTokens(
//                        General.authData.value?.token?:"",
//                        General.authData.value?.refreshToken ?:""
//                    )
//                }

//                    try {
//                        val refreshToken = client.
//                        post("${General.BASED_URL}/refreshToken/refresh") {
//                            url {
//                                parameters.append("tokenHolder", General.authData.value?.refreshToken ?: "")
//                            }
//                            markAsrefreshTokenRequest()
//                        }
//                        if(refreshToken.status== HttpStatusCode.OK){
//                            val result = refreshToken.body<AuthDto>()
//                            General.updateSavedToken(authDao, result)
//                            BearerTokens(
//                                Token = result.Token,
//                                refreshToken = result.refreshToken
//                            )
//                        }else if(refreshToken.status== HttpStatusCode.Unauthorized) {
//                            authDao.nukeTable()
//                            null
//                        }else {
//                            null
//                        }
//                    } catch (cause: Exception) {
//                        null
//                    }
null

                    // Update saved tokens
//                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }


    }
}


val httpClientModule = module {
    single { provideHttpClient(get()) }
}