package com.example.eccomerce_app.util

object Secrets {
    init {
        System.loadLibrary("nativelib")
    }

    external fun getBaseUrlFromNdk(): String

    fun getBaseUrl(): String = getBaseUrlFromNdk()
    fun getUrl(): String="http://192.168.1.45:5077/api"
//     fun getBaseUrl(): String="http://192.168.1.109:5077/api"

    //const val imageUrl = "192.168.1.109"
    const val imageUrl = "192.168.1.45"

}
