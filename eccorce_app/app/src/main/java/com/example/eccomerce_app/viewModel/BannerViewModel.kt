package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.BannerModel
import com.example.eccomerce_app.model.DtoToModel.toBanner
import com.example.eccomerce_app.dto.BannerDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.BannerRepository
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Named

class BannerViewModel(
    @Named("bannerHub")   val bannerRepository: BannerRepository,
    val webSocket: HubConnection?

) : ViewModel() {
     val _hub = MutableStateFlow<HubConnection?>(null)

     val _banners = MutableStateFlow<List<BannerModel>?>(null)
    val banners = _banners.asStateFlow()

     val _bannersRadom = MutableStateFlow<List<BannerModel>?>(null)
    val bannersRadom = _bannersRadom.asStateFlow()


     val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _hub.emit(webSocket)
                _hub.value?.start()?.blockingAwait()
                _hub.value?.on(
                    "createdBanner",
                    { result ->
                        val banners = mutableListOf<BannerModel>()
                        if (_banners.value == null) {
                            banners.add(result.toBanner())
                        } else {
                            banners.add(result.toBanner())
                            banners.addAll(_banners.value!!)
                        }
                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            Log.d("bannerCreationData", banners.toString())

                            _banners.emit(banners)
                        }
                    },
                    BannerDto::class.java
                )

            }

        }
    }

    init {
        connection()
    }


    override fun onCleared() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            if (_hub.value != null)
                _hub.value!!.stop()
        }
        super.onCleared()
    }


    fun getStoresBanner() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = bannerRepository.getRandomBanner()
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<BannerDto>

                    val bannersResponse = data.map { it.toBanner() }.toList()

                    _bannersRadom.emit(bannersResponse)
                }

                is NetworkCallHandler.Error -> {
                    _bannersRadom.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                }
            }
        }

    }


    suspend fun createBanner(endDate: String, image: File): String? {

        val result = bannerRepository.createBanner(
            endDate,
            image
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as BannerDto

                val bannersHolder = mutableListOf<BannerModel>()
                val bannersResponse = data.toBanner()

                bannersHolder.add(bannersResponse)
                if (_banners.value != null) {
                    bannersHolder.addAll(_banners.value!!)
                }

                val distinctBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                if (distinctBanner.isNotEmpty())
                    _banners.emit(distinctBanner)
                else
                    _banners.emit(emptyList())
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString().replace("", ""))
                if (errorMessage.contains(General.BASED_URL.substring(8, 20))) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }

        }
    }

    suspend fun deleteBanner(bannerId: UUID): String? {

        val result = bannerRepository.deleteBanner(bannerId)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val copyBanner = _banners.value?.filter { it.id != bannerId }
                if (!copyBanner.isNullOrEmpty())
                    _banners.emit(copyBanner)
                else
                    _banners.emit(emptyList())
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString().replace("", ""))
                if (errorMessage.contains(General.BASED_URL.substring(8, 20))) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }


        }
    }


    fun getStoreBanner(storeId: UUID, pageNumber: Int = 1) {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = bannerRepository.getBannerByStoreId(storeId, pageNumber)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as List<BannerDto>

                    val bannersHolder = mutableListOf<BannerModel>()
                    val bannersResponse = data.map { it.toBanner() }.toList()

                    bannersHolder.addAll(bannersResponse)
                    if (_banners.value != null) {
                        bannersHolder.addAll(_banners.value!!)
                    }

                    val distinctBanner = bannersHolder.distinctBy { it.id }.toMutableList()
                    if (distinctBanner.isNotEmpty())
                        _banners.emit(distinctBanner)
                    else
                        _banners.emit(emptyList())

                }

                is NetworkCallHandler.Error -> {
                    _banners.emit(emptyList())

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }

    }


}