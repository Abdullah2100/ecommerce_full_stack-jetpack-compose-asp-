package com.example.eccomerce_app.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.Room.Dao.AuthDao
import com.example.eccomerce_app.data.Room.Dao.LocaleDao
import com.example.eccomerce_app.data.Room.Model.AuthModelEntity
import com.example.eccomerce_app.data.repository.AuthRepository
import com.example.eccomerce_app.dto.AuthDto
import com.example.eccomerce_app.dto.LoginDto
import com.example.eccomerce_app.dto.SignupDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val authRepository = mockk<AuthRepository>()
    private val authDao = mockk<AuthDao>(relaxed = true)
    private val localDao = mockk<LocaleDao>(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking the initial calls in init block
        coEvery { localDao.getCurrentLocal() } returns null
        coEvery { authDao.getAuthData() } returns null
        coEvery { authDao.isPassOnBoarding() } returns false
        coEvery { authDao.isPassLocationScreen() } returns false

        viewModel = AuthViewModel(authRepository, authDao, localDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loginUser success updates authDao and returns null`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "password"
        val token = "token"
        val authDto = AuthDto("accessToken", "refreshToken")
        
        coEvery { authRepository.login(any()) } returns NetworkCallHandler.Successful(authDto)
        
        var isLoadingValue = false
        val updateLoading = { state: Boolean -> isLoadingValue = state }

        // Act
        val result = viewModel.loginUser(username, password, token, updateLoading)

        // Assert
        assertNull(result)
        coVerify { authDao.saveAuthData(any()) }
        assertEquals(false, isLoadingValue)
    }

    @Test
    fun `loginUser failure returns error message`() = runTest {
        // Arrange
        val username = "testuser"
        val password = "password"
        val token = "token"
        val errorMessage = "Invalid credentials"
        
        coEvery { authRepository.login(any()) } returns NetworkCallHandler.Error(errorMessage)
        
        var isLoadingValue = false
        val updateLoading = { state: Boolean -> isLoadingValue = state }

        // Act
        val result = viewModel.loginUser(username, password, token, updateLoading)

        // Assert
        assertEquals(errorMessage, result)
        assertEquals(errorMessage, viewModel.errorMessage.value)
        assertEquals(false, isLoadingValue)
    }

    @Test
    fun `signUpUser success updates authDao`() = runTest {
        // Arrange
        val authDto = AuthDto("accessToken", "refreshToken")
        coEvery { authRepository.signup(any()) } returns NetworkCallHandler.Successful(authDto)
        
        // Act
        val result = viewModel.signUpUser("email", "name", "phone", "pass", "token") { }

        // Assert
        assertNull(result)
        coVerify { authDao.saveAuthData(any()) }
    }

    @Test
    fun `getOtp success returns null`() = runTest {
        // Arrange
        coEvery { authRepository.getOtp(any()) } returns NetworkCallHandler.Successful(true)
        
        // Act
        val result = viewModel.getOtp("test@email.com") { }

        // Assert
        assertNull(result)
    }

    @Test
    fun `otpVerifying success returns null`() = runTest {
        // Arrange
        coEvery { authRepository.verifyingOtp(any(), any()) } returns NetworkCallHandler.Successful(true)
        
        // Act
        val result = viewModel.otpVerifying("test@email.com", "1234") { }

        // Assert
        assertNull(result)
    }

    @Test
    fun `logout nukes tables`() = runTest {
        // Act
        viewModel.logout()
        
        // Assert
        coVerify { authDao.nukeAuthTable() }
        coVerify { authDao.nukeIsPassAddressTable() }
    }
}
