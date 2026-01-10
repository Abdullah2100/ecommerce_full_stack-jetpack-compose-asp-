package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.data.Room.Dao.AuthDao
import com.example.eccomerce_app.data.Room.Model.IsPassLocationScreen
import com.example.e_commercompose.model.Address
import com.example.eccomerce_app.model.DtoToModel.toAddress
import com.example.eccomerce_app.model.DtoToModel.toUser
import com.example.eccomerce_app.dto.UpdateMyInfoDto
import com.example.e_commercompose.model.UserModel
import com.example.eccomerce_app.dto.AddressDto
import com.example.eccomerce_app.dto.CreateAddressDto
import com.example.eccomerce_app.dto.UpdateAddressDto
import com.example.eccomerce_app.dto.UserDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.Room.Dao.LocaleDao
import com.example.eccomerce_app.data.Room.Model.CurrentLocal
import com.example.eccomerce_app.data.Room.Model.IsPassOnBoardingScreen
import com.example.eccomerce_app.data.repository.AddressRepository
import com.example.eccomerce_app.data.repository.UserRepository
import com.example.eccomerce_app.util.General.currentLocal
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel(
    val dao: AuthDao,
    val userRepository: UserRepository,
    val addressRepository: AddressRepository,
    val localeDao: LocaleDao
) : ViewModel() {
    val _userInfo = MutableStateFlow<UserModel?>(null)
    val userInfo = _userInfo.asStateFlow()

    val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
    }


    fun setIsPassOnBoardingScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            var isPassOnBoarding = IsPassOnBoardingScreen()
            isPassOnBoarding.condition = true;
            val result = dao.savePassingOnBoarding(isPassOnBoarding)
            Log.d("insertNewPassingOnBoarding", result.toString())


        }
    }


   suspend fun updateCurrentLocale(locale:String) {
             localeDao.saveCurrentLocale(CurrentLocal(0, locale))
            currentLocal.emit(locale);
    }
    suspend fun userPassLocation(status: Boolean? = false) {
        var isPassLocation = IsPassLocationScreen()
        isPassLocation.condition = status ?: false;
        dao.savePassingLocation(isPassLocation)
    }


    fun updateMyStoreId(id: UUID) {
        viewModelScope.launch {
            if (_userInfo.value != null) {
                val copyMyInfo = _userInfo.value!!.copy(storeId = id);
                _userInfo.emit(copyMyInfo)
            }
        }
    }

    fun getMyInfo() {
        if (_userInfo.value != null) return
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            val result = userRepository.getMyInfo()
            when (result) {
                is NetworkCallHandler.Successful<*> -> {
                    val data = result.data as UserDto
                    _userInfo.emit(data.toUser())
                }

                is NetworkCallHandler.Error -> {
                    if (_userInfo.value == null) {
                        _userInfo.emit(
                            null
                        )
                    }
                    val resultError = result.data as String
                    Log.d("errorFromNetwork", resultError)
                }


            }
        }
    }


    suspend fun updateMyInfo(userData: UpdateMyInfoDto): String? {
        val result = userRepository.UpdateMyInfo(
            userData
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val data = result.data as UserDto
                _userInfo.emit(data.toUser())
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


    suspend fun addUserAddress(
        longitude: Double? = null,
        latitude: Double? = null,
        title: String? = null,
    ): String? {
        delay(100)
        val result = addressRepository
            .userAddNewAddress(
                CreateAddressDto(
                    Longitude = longitude ?: 5.5,
                    Latitude = latitude ?: 5.5,
                    Title = title ?: "home"
                )
            )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val address = result.data as AddressDto?
                if (address != null) {
                    val locationsCopy = mutableListOf<Address>()

                    if (_userInfo.value?.address != null) {

                        val newAddress = _userInfo.value?.address?.map { it ->
                            if (it.isCurrent) {
                                it.copy(isCurrent = false)
                            } else it
                        }
                        locationsCopy.addAll(newAddress ?: emptyList())
                    }
                    locationsCopy.add(address.toAddress())

                    val copyMyInfo = _userInfo.value?.copy(address = locationsCopy.toList())
                    _userInfo.emit(copyMyInfo)


                }

                return null
            }

            is NetworkCallHandler.Error -> {
                return result.data.toString().replace("\"", "")
            }
        }

    }

    suspend fun setCurrentActiveUserAddress(addressId: UUID): String? {
        when (val result = addressRepository.setAddressAsCurrent(addressId)) {
            is NetworkCallHandler.Successful<*> -> {
                if (!_userInfo.value?.address.isNullOrEmpty()) {
                    val addresses = _userInfo.value?.address?.map { address ->
                        if (address.id == addressId) {
                            address.copy(isCurrent = true)
                        } else {
                            address.copy(isCurrent = false)
                        }
                    }
                    val copyMyAddress = _userInfo.value?.copy(address = addresses)
                    _userInfo.emit(copyMyAddress)

                    userPassLocation(true)
                }
                return null
            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data
                return errorMessage.toString()
            }


        }
    }

    suspend fun updateUserAddress(
        addressId: UUID,
        addressTitle: String?,
        longitude: Double?,
        latitude: Double?
    ): String? {
        val result = addressRepository.userUpdateAddress(
            UpdateAddressDto(
                Id = addressId,
                Title = addressTitle,
                Latitude = latitude,
                Longitude = longitude
            )
        )
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val resultData = result.data as AddressDto

                val address = _userInfo.value?.address?.map {
                    it
                    if (it.id == addressId) {
                        resultData.toAddress()
                    } else {
                        it
                    }
                }
                val copyMyAddress = _userInfo.value?.copy(address = address)
                _userInfo.emit(copyMyAddress)


                return null

            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data
                return errorMessage.toString()
            }


        }
    }

    suspend fun deleteUserAddress(addressId: UUID): String? {
        when (val result = addressRepository.deleteUserAddress(addressId)) {
            is NetworkCallHandler.Successful<*> -> {

                val address = _userInfo.value?.address?.filter { it.id != addressId }

                val copyMyAddress = _userInfo.value?.copy(address = address)
                _userInfo.emit(copyMyAddress)

                return null

            }

            is NetworkCallHandler.Error -> {
                val errorMessage = result.data
                return errorMessage.toString()
            }


        }
    }


}