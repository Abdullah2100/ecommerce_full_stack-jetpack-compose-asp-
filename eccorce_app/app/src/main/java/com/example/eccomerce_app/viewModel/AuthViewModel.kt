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
import com.example.eccomerce_app.util.GeneralValue
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

//     val isLoading: StateFlow<Boolean> field = MutableStateFlow(false)

    val errorMessage = MutableStateFlow<String?>(null)


    private val _currentScreen = MutableStateFlow<Int?>(null)
    val currentScreen = _currentScreen.asStateFlow()

    suspend fun clearErrorMessage() {
        errorMessage.emit(null)
    }

    private val _coroutineException = CoroutineExceptionHandler { _, message ->
        Log.d("ErrorMessageIs", message.message.toString())
        viewModelScope.launch(Dispatchers.IO) {
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
            Log.d("CurrentLocalization", dbLocale?.name ?: "no data")

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
            if (authData != null)
                GeneralValue.authData = authData
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
        updateIsLoading: (state: Boolean) -> Unit
    ): String? {
        updateIsLoading.invoke(true)
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
                updateIsLoading.invoke(false)

                val authData = result.data as AuthDto

                val authDataHolder = AuthModelEntity(
                    id = 0,
                    token = authData.token,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )

                GeneralValue.authData = authDataHolder
                null;

            }

            is NetworkCallHandler.Error -> {
                updateIsLoading.invoke(false)
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
        updateStateLoading: (state: Boolean) -> Unit

    ): String? {
        updateStateLoading.invoke(true)
        val result = authRepository.login(
            LoginDto(
                Username = username,
                Password = password,
                DeviceToken = token
            )
        )
        return when (result) {
            is NetworkCallHandler.Successful<*> -> {
                updateStateLoading.invoke(false)
                val authData = result.data as AuthDto
                val authDataHolder = AuthModelEntity(
                    id = 0,
                    token = authData.token,
                    refreshToken = authData.refreshToken
                )
                authDao.saveAuthData(
                    authDataHolder
                )
                GeneralValue.authData = authDataHolder
                null
            }

            is NetworkCallHandler.Error -> {
                updateStateLoading.invoke(false)
                val errorResult = (result.data?.replace("\"", ""))

                errorMessage.emit(errorResult)
                errorResult
            }


        }
    }


    suspend fun getOtp(
        email: String,
        updateIsLoading: (state: Boolean) -> Unit
    ): String? {
        updateIsLoading.invoke(true)
        when (val result = authRepository.getOtp(email)) {
            is NetworkCallHandler.Successful<*> -> {
                updateIsLoading.invoke(false)
                return null
            }

            is NetworkCallHandler.Error -> {
                updateIsLoading.invoke(false)
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
        otp: String,
        updateIsLoading: (state: Boolean) -> Unit
    ): String? {
        updateIsLoading.invoke(true)

        when (val result = authRepository.verifyingOtp(email, otp)) {
            is NetworkCallHandler.Successful<*> -> {
                updateIsLoading.invoke(false)
                return null
            }

            is NetworkCallHandler.Error -> {
                updateIsLoading.invoke(false)

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

        when (val result = authRepository.resetPassword(email, otp, newPassword)) {
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
                GeneralValue.authData = authDataHolder

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