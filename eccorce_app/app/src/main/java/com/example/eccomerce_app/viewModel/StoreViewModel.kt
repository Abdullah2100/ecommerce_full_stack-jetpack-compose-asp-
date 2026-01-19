package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.dto.StoreDto
import com.example.eccomerce_app.model.DtoToModel.toStore
import com.example.eccomerce_app.model.StoreModel
import com.example.eccomerce_app.dto.CreateStoreDto
import com.example.eccomerce_app.dto.StoreStatusDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.StoreRepository
import com.microsoft.signalr.HubConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Named


class StoreViewModel(
    val storeRepository: StoreRepository,
    @Named("storeHub")  val webSocket: HubConnection?
) : ViewModel() {

   private val _hub = MutableStateFlow<HubConnection?>(null)

    val storeCreateData = MutableStateFlow<CreateStoreDto?>(null)


   private val _stores = MutableStateFlow<List<StoreModel>?>(null)
    val stores = _stores.asStateFlow()

   private val _isUpdate = MutableStateFlow(false)
    val isUpdate = _isUpdate.asStateFlow()

  private  val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }

    fun connection() {

        if (webSocket != null) {
            viewModelScope.launch(Dispatchers.IO + _coroutineException) {

                _hub.emit(webSocket)
                _hub.value?.on(
                    "storeStatus",
                    { result ->

                        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
                            if (result.Status == true) {
                                val storeWithoutCurrentId =
                                    _stores.value?.filter { it.id != result.StoreId }
                                _stores.emit(storeWithoutCurrentId)
                            }
                        }
                    },
                    StoreStatusDto::class.java
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
            storeCreateData.emit(null)
        }
        super.onCleared()
    }

    fun getStoreData(storeId: UUID?) {
        if (storeId == null) return
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = storeRepository.getStoreById(storeId)
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as StoreDto

                    val storesHolder = mutableListOf<StoreModel>()
                    storesHolder.add(data.toStore())

                    if (_stores.value != null) {
                        storesHolder.addAll(_stores.value!!.toList())
                    }

                    val distinctStore = storesHolder.distinctBy { it.id }.toMutableList()
                    _stores.emit(distinctStore)

                }

                is NetworkCallHandler.Error -> {

                    val errorMessage = (result.data.toString())
                    if (errorMessage.contains(General.BASED_URL)) {
                        errorMessage.replace(General.BASED_URL, " Server ")
                    }
                    Log.d("errorFromGettingStoreData", errorMessage)
                }
            }
        }
    }


    fun setStoreCreateData(
        longitude: Double? = null,
        latitude: Double? = null,
        wallpaperImage: File? = null,
        smallImage: File? = null,
        storeTitle: String? = null,
        storeId: UUID?=null
    ) {
        var myStoreData: CreateStoreDto? = null
        if (storeCreateData.value == null)
            viewModelScope.launch {
                storeCreateData.emit(CreateStoreDto())
            }
        if (longitude != null && latitude != null) {
            myStoreData = storeCreateData.value?.copy(latitude = latitude, longitude = longitude)

        }


        if (storeTitle != null) {
            myStoreData = storeCreateData.value?.copy(name = storeTitle)
        }

        if (wallpaperImage != null) {
            myStoreData = storeCreateData.value?.copy(wallpaperImage = wallpaperImage)
        }

        if (smallImage != null) {
            myStoreData = storeCreateData.value?.copy(smallImage = smallImage)
        }
        viewModelScope.launch {
            storeCreateData.emit(myStoreData)
            _isUpdate.emit(true)

        }

    }


  suspend  fun createStore(
        name: String,
        wallpaperImage: File,
        smallImage: File,
        longitude: Double,
        latitude: Double,
        sumAdditionalFun: (id: UUID) -> Unit,
    ): String? {

        val result = storeRepository.createStore(
            name,
            wallpaperImage,
            smallImage,
            longitude,
            latitude
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                _isUpdate.emit(false)
                storeCreateData.emit(CreateStoreDto())
                val data = result.data as StoreDto
                val storesHolder = mutableListOf<StoreModel>()
                storesHolder.add(data.toStore())

                if (_stores.value != null) {
                    storesHolder.addAll(_stores.value!!.toList())
                }

                val distinctStore = storesHolder.distinctBy { it.id }.toMutableList()
                _stores.emit(distinctStore)
                sumAdditionalFun(data.id)
                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage
            }


        }
    }

    suspend fun updateStore(
        name: String?=null,
        wallpaperImage: File?=null,
        smallImage: File?=null,
        longitude: Double?,
        latitude: Double?,
    ): String? {

        val result = storeRepository.updateStore(
            name,
            wallpaperImage,
            smallImage,
            longitude,
            latitude
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                _isUpdate.emit(false)
                val storeDto = result.data as StoreDto
                val data = storeDto.toStore()


                val storeHolder = _stores.value?.map {value->
                    if(value.id==data.id)
                        value.copy(
                            name = data.name,
                            latitude = data.latitude,
                            longitude = data.longitude,
                            smallImage = data.smallImage,
                            pigImage = data.pigImage)
                    else value
                }?.toList()

                _stores.emit(storeHolder)

                return null
            }

            is NetworkCallHandler.Error -> {

                val errorMessage = (result.data.toString())
                if (errorMessage.contains(General.BASED_URL)) {
                    errorMessage.replace(General.BASED_URL, " Server ")
                }
                return errorMessage.replace("\"", "")
            }
        }
    }

//    fun getStoreInfoByStoreId(store_id: UUID) {
//        getStoreData(store_id)
//        getStoreBanner(store_id)
//        getStoreAddress(store_id, 1)
//        getStoreSubCategories(store_id, 1)
//    }


}

