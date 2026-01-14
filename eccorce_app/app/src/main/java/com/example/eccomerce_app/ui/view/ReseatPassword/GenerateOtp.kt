package com.example.eccomerce_app.ui.view.ReseatPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.CustomButton
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.viewModel.AuthViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.theme.CustomColor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Composable
fun GenerateOtpScreen(
    nav: NavHostController,
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutine = rememberCoroutineScope()

    val email = remember { mutableStateOf(TextFieldValue("")) }
    val isLoading = rememberSaveable { mutableStateOf(false) }



    val snackBarHostState = remember { SnackbarHostState() }



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {

        it.calculateTopPadding()
        it.calculateBottomPadding()


        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 10.dp)
                .padding( it)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Icon(
                    ImageVector.vectorResource(R.drawable.mail_validation),
                    "",
                    tint = CustomColor.primaryColor700,
                    modifier = Modifier.size(200.dp)
                )

                TextInputWithTitle(
                    email,
                    title = "",
                    placeHolder = context.getString(R.string.enter_your_email),
                )




                CustomButton(
                    isLoading = isLoading.value,
                    operation = {
                        keyboardController?.hide()
                        coroutine.launch {
                            val result = async {
                                authViewModel.getOtp(email.value.text, updateIsLoading = {value->
                                    isLoading.value = value
                                })
                            }.await()

                            if (!result.isNullOrEmpty()) {
                                snackBarHostState.showSnackbar(result)
                            } else {
                                nav.navigate(Screens.OtpVerification(email = email.value.text))
                                email.value = TextFieldValue("")
                            }
                        }
                    },
                    isEnable = email.value.text.trim().isNotEmpty(),
                    buttonTitle = stringResource(R.string.check),

                    )

            }
        }


    }

}