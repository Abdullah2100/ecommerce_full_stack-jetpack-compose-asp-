package com.example.eccomerce_app.ui.view.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.launch
import com.example.e_commercompose.R
import com.example.eccomerce_app.ui.component.SharedAppBar
import kotlinx.coroutines.async


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(
    nav: NavHostController,
    authKoin: AuthViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val fontScall = LocalDensity.current.fontScale


    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()


    val errorMessage = authKoin.errorMessage.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    val name = remember { mutableStateOf(TextFieldValue("slime")) }
    val email = remember { mutableStateOf(TextFieldValue("salime@gmail.com")) }
    val phone = remember { mutableStateOf(TextFieldValue("778537385")) }
    val password = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }
    val confirmPassword = remember { mutableStateOf(TextFieldValue("12AS@#fs")) }


    val isLoading = rememberSaveable { mutableStateOf(false) }
    val isCheckBox = remember { mutableStateOf(false) }
    val isEmailError = remember { mutableStateOf(false) }
    val isNameError = remember { mutableStateOf(false) }
    val isPhoneError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }
    val isPasswordConfirm = remember { mutableStateOf(false) }
    val isTermAndServicesError = remember { mutableStateOf(false) }
    val errorMessageValidation = remember { mutableStateOf("") }

    fun updateConditionValue(
        isLoadingValue: Boolean? = null,
        isCheckBoxValue: Boolean? = null,
        isEmailErrorValue: Boolean? = null,
        isNameErrorValue: Boolean? = null,
        isPhoneErrorValue: Boolean? = null,
        isPasswordErrorValue: Boolean? = null,
        isPasswordConfirmValue: Boolean? = null,
        isTermAndServicesErrorValue: Boolean? = null
    ) {
        when {
            isLoadingValue != null -> isLoading.value = isLoadingValue
            isCheckBoxValue != null -> isCheckBox.value = isCheckBoxValue
            isEmailErrorValue != null -> isEmailError.value = isEmailErrorValue
            isNameErrorValue != null -> isNameError.value = isNameErrorValue
            isPhoneErrorValue != null -> isPhoneError.value = isPhoneErrorValue
            isPasswordErrorValue != null -> isPasswordError.value = isPasswordErrorValue
            isPasswordConfirmValue != null -> isPasswordConfirm.value = isPasswordConfirmValue
            isTermAndServicesErrorValue != null -> isTermAndServicesError.value = isTermAndServicesErrorValue
        }
    }


    fun validateSignupInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        updateConditionValue(
            isNameErrorValue = false,
            isEmailErrorValue = false,
            isPasswordErrorValue = false,
            isPasswordConfirmValue = false
        )

        when {

            name.trim().isEmpty() -> {
                errorMessageValidation.value = context.getString(R.string.name_must_not_be_empty)
                updateConditionValue(isNameErrorValue = true)
                return false
            }

            email.trim().isEmpty() -> {
                errorMessageValidation.value = context.getString(R.string.email_must_not_be_empty)
                updateConditionValue(isEmailErrorValue = true)
                return false
            }

            !Validation.emailValidation(email) -> {
                errorMessageValidation.value = context.getString(R.string.write_valid_email)
                updateConditionValue(isEmailErrorValue = true)
                return false
            }

            phone.value.text.trim().isEmpty() -> {
                errorMessageValidation.value = context.getString(R.string.phone_must_not_empty)
                updateConditionValue(isPhoneErrorValue = true)
                return false
            }

            password.trim().isEmpty() -> {
                errorMessageValidation.value =
                    (context.getString(R.string.password_must_not_be_empty))
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }

            !Validation.passwordSmallValidation(password) -> {
                errorMessageValidation.value =
                    (context.getString(R.string.password_must_not_contain_two_small_letter))
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }

            !Validation.passwordNumberValidation(password) -> {
                errorMessageValidation.value =
                    (context.getString(R.string.password_must_not_contain_two_number))
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }

            !Validation.passwordCapitalValidation(password) -> {
                errorMessageValidation.value =
                    (context.getString(R.string.password_must_not_contain_two_capitalletter))
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }

            !Validation.passwordSpicialCharracterValidation(password) -> {
                errorMessageValidation.value =
                    (context.getString(R.string.password_must_not_contain_two_spical_character))
                updateConditionValue(isPasswordErrorValue = true)
                return false
            }

            confirmPassword.trim().isEmpty() -> {
                errorMessageValidation.value =
                    context.getString(R.string.password_must_not_be_empty)
                updateConditionValue(isPasswordConfirmValue = true)
                return false
            }

            password != confirmPassword -> {
                errorMessageValidation.value =
                    (context.getString(R.string.confirm_password_not_equal_to_password))
                updateConditionValue(isPasswordConfirmValue = true)
                return false
            }

            !isCheckBox.value -> {
                errorMessageValidation.value =
                    context.getString(R.string.term_and_policies_is_required)
                updateConditionValue(isTermAndServicesErrorValue = true)

                return false
            }


            else -> return true
        }

    }

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage.value.toString())
                authKoin.clearErrorMessage()
            }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },
        topBar = {
            SharedAppBar(title = stringResource(R.string.signup))
        },
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(it )
                .padding(start =10.dp, end = 10.dp )

                .fillMaxSize()
        ) {
            val (inputRef) = createRefs()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .constrainAs(inputRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start,
            ) {

                TextInputWithTitle(
                    name,
                    title = stringResource(R.string.name),
                    placeHolder = stringResource(R.string.enter_your_name),
                    errorMessage = errorMessageValidation.value,
                    isHasError = isNameError.value,
                )

                TextInputWithTitle(
                    email,
                    title = stringResource(R.string.email),
                    placeHolder = stringResource(R.string.enter_your_email),
                    errorMessage = errorMessageValidation.value,
                    isHasError = isEmailError.value,
                )
                TextInputWithTitle(
                    phone,
                    title = stringResource(R.string.phone),
                    placeHolder = stringResource(R.string.enter_phone),
                    errorMessage = errorMessageValidation.value,
                    isHasError = isPhoneError.value,
                )
                TextSecureInputWithTitle(
                    password,
                    stringResource(R.string.password),
                    isPasswordError.value,
                    errorMessageValidation.value,
                )
                TextSecureInputWithTitle(
                    confirmPassword,
                    stringResource(R.string.confirm_password),
                    isPasswordConfirm.value,
                    errorMessageValidation.value,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-10).dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Checkbox(
                        checked = isCheckBox.value,
                        onCheckedChange = { updateConditionValue(isCheckBoxValue = !isCheckBox.value) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CustomColor.primaryColor700
                        ),
                        modifier = Modifier.padding()
                    )
                    Text(
                        stringResource(R.string.agree_with),
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        color = if (isTermAndServicesError.value) CustomColor.alertColor_1_400 else CustomColor.neutralColor950,
                        fontSize = (16 / fontScall).sp,
                    )
                    Text(
                        stringResource(R.string.term_condition),
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        color = if (isTermAndServicesError.value) CustomColor.alertColor_1_400 else CustomColor.primaryColor700,
                        fontSize = (16 / fontScall).sp,
                        modifier = Modifier
                            .padding(start = 3.dp)
                            ,
                        textDecoration = TextDecoration.Underline
                    )
                }
                if (isTermAndServicesError.value)
                    Text(
                        errorMessageValidation.value,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier
                            .offset(x = (13).dp, y = (-12).dp)
                    )
//                Sizer(heigh = 10)

                CustomAuthBottom(
                    isLoading = isLoading.value,
                    validationFun = {
                        validateSignupInput(
                            email = email.value.text,
                            name = name.value.text,
                            password = password.value.text,
                            confirmPassword = confirmPassword.value.text
                        )
                    },
                    buttonTitle = stringResource(R.string.signup),
                    operation = {
                        keyboardController?.hide()
                        coroutine.launch {
                            updateConditionValue(isLoadingValue = true)
                            val token =
                                async { authKoin.generateTokenNotification() }.await()
//                             Pair(
//                                "fv6pNFrXSsC7o29xq991br:APA91bHiUFcyvxKKxcqWoPZzoIaeWEs6_uN36YI0II5HHpN3HP-dUQap9UbnPiyBB8Fc5xX6GiCYbDQ7HxuBlXZkAE2P0T82-DRQ160EiKCJ9tlPgfgQxa4",
//                                null
//                            )

                            if (token.first != null) {
                                val result = authKoin.signUpUser(
                                    phone = phone.value.text,
                                    email = email.value.text,
                                    password = password.value.text,
                                    name = name.value.text,
                                    token = token.first!!,
                                    updateIsLoading = {value->isLoading.value = value}
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
                                updateConditionValue(isLoadingValue = false)
                                coroutine.launch {
                                    snackBarHostState.showSnackbar(
                                        token.second
                                            ?: context.getString(R.string.network_must_be_connected_to_complete_operation)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}