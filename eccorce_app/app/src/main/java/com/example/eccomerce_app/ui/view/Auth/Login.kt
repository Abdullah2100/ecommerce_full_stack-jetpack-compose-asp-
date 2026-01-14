package com.example.eccomerce_app.ui.view.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.SharedAppBar
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    nav: NavHostController, authKoin: AuthViewModel
) {
    val context = LocalContext.current

    val fontScall = LocalDensity.current.fontScale
    val keyboardController = LocalSoftwareKeyboardController.current

    val errorMessage = authKoin.errorMessage.collectAsState()

    val coroutine = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val isSendingData = remember { mutableStateOf(false) }
    val isEmailError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }

    val userNameOrEmail = remember { mutableStateOf(TextFieldValue("ali535@gmail.com")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }


    val errorMessageValidation = remember { mutableStateOf("") }

    fun updateConditionValue(
        isSendingDataValue: Boolean? = null,
        isEmailErrorValue: Boolean? = null,
        isPasswordErrorValue: Boolean? = null
    ) {
        when {
            isSendingDataValue != null -> isSendingData.value = isSendingDataValue
            isEmailErrorValue != null -> isEmailError.value = isEmailErrorValue
            isPasswordErrorValue != null -> isPasswordError.value = isPasswordErrorValue
        }
    }


    fun validateLoginInput(
        username: String, password: String
    ): Boolean {
        updateConditionValue(isEmailErrorValue = false, isPasswordErrorValue = false)
        when {
            username.trim().isEmpty() -> {
                errorMessageValidation.value = context.getString(R.string.email_must_not_be_empty)
                updateConditionValue(isEmailErrorValue = true)
                return false
            }
            password.trim().isEmpty() -> {
                errorMessageValidation.value =
                    context.getString(R.string.password_must_not_be_empty)
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }
            else -> return true
        }
    }


    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null) coroutine.launch {
            snackBarHostState.showSnackbar(errorMessage.value.toString())
            authKoin.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            SharedAppBar(title =  stringResource(R.string.login))
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(it)
                .padding(start =10.dp, end = 10.dp )
                .fillMaxSize()
        )
        {
            val (bottomRef, inputRef) = createRefs()
            Box(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .constrainAs(bottomRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }

            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.don_t_have_any_account_yet),
                        color = CustomColor.neutralColor800,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16 / fontScall).sp
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable {
                                nav.navigate(Screens.Signup)
                            }) {
                        Text(
                            text = stringResource(R.string.signup),
                            color = CustomColor.primaryColor700,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16 / fontScall).sp

                        )

                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .constrainAs(inputRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.Start,
            ) {



                Sizer(heigh = 50)
                TextInputWithTitle(
                    userNameOrEmail,
                    title = stringResource(R.string.email),
                    placeHolder = stringResource(R.string.enter_your_email),
                    errorMessage = errorMessageValidation.value,
                    isHasError = isEmailError.value,
                )
                TextSecureInputWithTitle(
                    password,
                    stringResource(R.string.password),
                    isPasswordError.value,
                    errorMessageValidation.value
                )
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        stringResource(R.string.forget_password),
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        color = CustomColor.neutralColor950,
                        fontSize = (16 / fontScall).sp,
                        modifier = Modifier.clickable {
                            nav.navigate(Screens.ReseatPasswordGraph)
                        })
                }

                Sizer(heigh = 30)


                CustomAuthBottom(
                    isLoading = isSendingData.value,
                    operation = {
                        keyboardController?.hide()
                        if (userNameOrEmail.value.text.isBlank() || password.value.text.isBlank()) {
                            coroutine.launch {
                                snackBarHostState.showSnackbar(context.getString(R.string.user_name_or_password_is_blank))
                            }
                        } else {
                            coroutine.launch {
                                updateConditionValue(isSendingDataValue = true)

                                delay(10)

                                val token =
                                    async { authKoin.generateTokenNotification() }.await()

//                                Pair(
//                                    "fv6pNFrXSsC7o29xq991br:APA91bHiUFcyvxKKxcqWoPZzoIaeWEs6_uN36YI0II5HHpN3HP-dUQap9UbnPiyBB8Fc5xX6GiCYbDQ7HxuBlXZkAE2P0T82-DRQ160EiKCJ9tlPgfgQxa4",
//                                    null
//                                )
                                if (!token.first.isNullOrEmpty()) {
                                    val result = authKoin.loginUser(
                                        userNameOrEmail.value.text,
                                        password = password.value.text,
                                        token = token.first!!,
                                        updateStateLoading ={ value ->
                                            isSendingData.value = value
                                        },
                                    )
                                    if (result.isNullOrEmpty())
                                        nav.navigate(Screens.LocationGraph) {
                                            popUpTo(nav.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    else
                                        snackBarHostState.showSnackbar(result)

                                } else {
                                    updateConditionValue(isSendingDataValue = false)
                                    coroutine.launch {
                                        snackBarHostState.showSnackbar(
                                            token.second
                                                ?: context.getString(R.string.network_must_be_connected_to_complete_operation)
                                        )
                                    }
                                }
                            }

                        }
                    }, buttonTitle = stringResource(R.string.login), validationFun = {
                        true
                        validateLoginInput(
                            username = userNameOrEmail.value.text, password = password.value.text
                        )
                    })
            }
        }
    }
}