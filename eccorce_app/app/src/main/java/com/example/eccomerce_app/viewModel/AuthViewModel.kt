package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.data.Room.Dao.AuthDao
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.dto.LoginDto
import com.example.eccomerce_app.dto.AuthDto
import com.example.eccomerce_app.dto.SignupDto
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.Room.Dao.LocaleDao
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.util.General.currentLocal
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(
    val authRepository:
    AuthRepository,
    val authDao: AuthDao,
    val localDao: LocaleDao
) : ViewModel() {

    val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val errorMessage = MutableStateFlow<String?>(null)


    val _currentScreen = MutableStateFlow<Int?>(null)
    val currentScreen = _currentScreen.asStateFlow()

    suspend fun clearErrorMessage() {
        errorMessage.emit(null)
    }

    val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(false)
            when (message.message?.contains("java.net.ConnectException")) {
                true -> {
                    errorMessage.update { "لا بد من تفعيل الانترنت لاكمال العملية" }
                }

                else -> {
                    errorMessage.update { "المستخدم غير موجود" }

                }
            }
        }
    }

    init {
        getCurrentLocalization()
        getStartedScreen()
    }

    fun getCurrentLocalization() {
        viewModelScope.launch(Dispatchers.Default) {
            val dbLocale = localDao.getCurrentLocal()
            Log.d("CurrentLocalization",dbLocale?.name?:"no data")

            if (dbLocale == null) {
                return@launch
            }
            currentLocal.emit(dbLocale.name);
        }
    }

    fun getStartedScreen() {
        viewModelScope.launch(Dispatchers.Default) {
            val authData = authDao.getAuthData()
            val isPassOnBoard = authDao.isPassOnBoarding()
            val isLocation = authDao.isPassLocationScreen()
            Log.d("AuthDataIs", isPassOnBoard.toString())
            General.authData.emit(authData)
            when (isPassOnBoard) {
                false -> {
                    _currentScreen.emit(1)
                }

                else -> {
                    when (authData == null) {
                        true -> {
                            _currentScreen.emit(2)
                        }

                        else -> {
                            when (!isLocation) {
                                true -> {
                                    _currentScreen.emit(3)
                                }

                                else -> {
                                    _currentScreen.emit(4)
                                }
                            }
                        }
                    }
                }
            }

        }
    }


    suspend fun generateTokenNotification(): Pair<String?, String?> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Pair(token, null)
        } catch (e: Exception) {
            Pair(null, "Network should be connecting for some functionality")
        }
    }

    suspend fun signUpUser(
        email: String,
        name: String,
        phone: String,
        password: String,
        token: String,
        isLoading: MutableState<Boolean>
    ): String? {
        _isLoading.emit(true)
        val result = authRepository.signup(
            SignupDto(
                Name = name,
                Password = password,
                Phone = phone,
                Email = email,
                DeviceToken = token
            )
        )

        return when (result) {
            is NetworkCallHandler.Successful<*> -> {
                isLoading.value = false;
                val authData = result.data as AuthDto

                val authDataHolder = AuthModelEntity(
                    id = 0,
                    token = authData.token,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )

                General.authData.emit(authDataHolder)
                null;

            }

            is NetworkCallHandler.Error -> {
                isLoading.value = false;

                val errorResult = (result.data.toString())
                if (errorResult.contains(General.BASED_URL)) {
                    errorResult.replace(General.BASED_URL, " Server ")
                }
                errorResult
            }

        }

    }

    suspend fun loginUser(
        username: String,
        password: String,
        token: String,
        isSendingData: MutableState<Boolean>
    ): String? {

        val result = authRepository.login(
            LoginDto(
                Username = username,
                Password = password,
                DeviceToken = token
            )
        )
        return when (result) {
            is NetworkCallHandler.Successful<*> -> {
                isSendingData.value = false
                val authData = result.data as AuthDto
                val authDataHolder = AuthModelEntity(
                    id = 0,
                    token = authData.token,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )
                General.authData.emit(authDataHolder)
                null
            }

            is NetworkCallHandler.Error -> {
                isSendingData.value = false
                val errorResult = (result.data?.replace("\"", ""))

                errorMessage.emit(errorResult)
                errorResult
            }


        }
    }


    suspend fun getOtp(
        email: String,
    ): String? {
        _isLoading.emit(true)

        val result = authRepository.getOtp(email)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
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

    suspend fun otpVerifying(
        email: String,
        otp: String
    ): String? {
        _isLoading.emit(true)

        val result = authRepository.verifyingOtp(email, otp)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
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


    suspend fun reseatPassword(
        email: String,
        otp: String,
        newPassword: String

    ): String? {

        val result = authRepository.resetPassword(email, otp, newPassword)
        when (result) {
            is NetworkCallHandler.Successful<*> -> {
                val authData = result.data as AuthDto
                val authDataHolder = AuthModelEntity(
                    id = 0,
                    token = authData.token,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )
                General.authData.emit(authDataHolder)

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

    fun logout() {
        viewModelScope.launch(Dispatchers.IO + _coroutineException) {
            authDao.nukeAuthTable()
            authDao.nukeIsPassAddressTable()
        }
    }
}