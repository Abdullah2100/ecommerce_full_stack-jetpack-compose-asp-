package com.example.eccomerce_app.ui.view.account

import android.content.ClipData
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.eccomerce_app.dto.UpdateMyInfoDto
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextNumberInputWithTitle
import com.example.eccomerce_app.ui.component.TextSecureInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.viewModel.UserViewModel
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutine = rememberCoroutineScope()

    val myInfo = userViewModel.userInfo.collectAsState()


    val userId = remember { mutableStateOf(TextFieldValue(myInfo.value?.id.toString())) }
    val fullName = remember { mutableStateOf(TextFieldValue("")) }
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val phone = remember { mutableStateOf(TextFieldValue("")) }
    val oldPassword = remember { mutableStateOf(TextFieldValue("")) }
    val newPassword = remember { mutableStateOf(TextFieldValue("")) }
    val file = remember { mutableStateOf<File?>(null) }


    val snackBarHostState = remember { SnackbarHostState() }


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                file.value = fileHolder
            }
        }
    }

    fun copyUserId() {
        coroutine.launch {
            clipboardManager.setClipEntry(
                ClipEntry(
                    ClipData.newPlainText(
                        userId.value.text,
                        userId.value.text
                    )
                )
            )
        }
    }

    fun validateLoginInput(): Boolean {
        var errorMessage = ""

        if (oldPassword.value.text.isEmpty() && newPassword.value.text.isEmpty() && phone.value.text.isEmpty())
            return true
        else if (phone.value.text.trim().length < 9) {
            errorMessage = context.getString(R.string.write_valid_phone)
        } else if (oldPassword.value.text.isNotEmpty() && !Validation.passwordSmallValidation(
                oldPassword.value.text
            )
        ) {
            errorMessage = (context.getString(R.string.password_must_not_contain_two_small_letter))
        } else if (oldPassword.value.text.isNotEmpty() && !Validation.passwordNumberValidation(
                oldPassword.value.text
            )
        ) {
            errorMessage = (context.getString(R.string.password_must_not_contain_two_number))
        } else if (oldPassword.value.text.isNotEmpty() && !Validation.passwordCapitalValidation(
                oldPassword.value.text
            )
        ) {
            errorMessage = (context.getString(R.string.password_must_not_contain_two_capitalletter))
        } else if (oldPassword.value.text.isNotEmpty() && !Validation.passwordSpicialCharracterValidation(
                oldPassword.value.text
            )
        ) {
            errorMessage =
                (context.getString(R.string.password_must_not_contain_two_spical_character))
        } else if (newPassword.value.text.isNotEmpty() && newPassword.value.text.trim().isEmpty()) {
            errorMessage = (context.getString(R.string.password_must_not_be_empty))
        } else if (oldPassword.value.text.isNotEmpty() && newPassword.value.text.isNotEmpty() && oldPassword.value.text != newPassword.value.text) {
            errorMessage = (context.getString(R.string.confirm_password_not_equal_to_password))
        }

        if (errorMessage.isNotEmpty()) {
            coroutine.launch {

                snackBarHostState.showSnackbar(errorMessage)
            }
            return false
        }


        return errorMessage.isEmpty()
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            SharedAppBar(
                title = stringResource(R.string.my_profile),
                nav = nav,
                action = {
                    if (
                        file.value != null ||
                        (fullName.value.text.isNotEmpty() &&
                                fullName.value.text != myInfo.value?.name) ||
                        newPassword.value.text.isNotEmpty() ||
                        oldPassword.value.text.isNotEmpty() ||
                        phone.value.text.isNotEmpty()

                    )

                        TextButton(
                            onClick = {
                                keyboardController?.hide()
                                val result = validateLoginInput()
                                if (result) {
                                    val data = UpdateMyInfoDto(
                                        name = fullName.value.text.ifEmpty { null },
                                        oldPassword = oldPassword.value.text.ifEmpty { null },
                                        newPassword = newPassword.value.text.ifEmpty { null },
                                        phone = phone.value.text.ifEmpty { null },
                                        thumbnail = file.value,
                                    )
                                    coroutine.launch {
                                        val result = async {
                                            userViewModel.updateMyInfo(data)
                                        }.await()

                                        if (result.isNullOrEmpty()) {
                                            phone.value = TextFieldValue("")
                                            oldPassword.value = TextFieldValue("")
                                            newPassword.value = TextFieldValue("")
                                            fullName.value = TextFieldValue("")
                                            file.value = null
                                        }

                                        val message = result
                                            ?: context.getString(R.string.profile_update_successfully)

                                        coroutine.launch { snackBarHostState.showSnackbar(message) }


                                    }
                                }
                            }
                        ) {
                            Text(
                                stringResource(R.string.save),
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (18).sp,
                                color = CustomColor.primaryColor700,
                                textAlign = TextAlign.Center
                            )
                        }
                }, scrollBehavior = scrollBehavior
            )
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            item {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )
                {
                    val (imageRef, cameralRef) = createRefs()
                    Box(
                        modifier = Modifier
                            .constrainAs(imageRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .height(110.dp)
                            .width(110.dp)
                            .border(
                                width = 1.dp,
                                color = CustomColor.neutralColor500,
                                shape = RoundedCornerShape(60.dp)
                            ),
                        contentAlignment = Alignment.Center
                    )
                    {
                        when (file.value == null) {
                            true -> {
                                when (myInfo.value?.thumbnail.isNullOrEmpty()) {
                                    true -> {

                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.user),
                                            "",
                                            modifier = Modifier.size(80.dp)
                                        )
                                    }

                                    else -> {

                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(90.dp)
                                                .width(90.dp)
                                                .clip(RoundedCornerShape(50.dp)),
                                            model = General.handlingImageForCoil(
                                                myInfo.value!!.thumbnail,
                                                context
                                            ),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = Color.Black,
                                                        modifier = Modifier.size(54.dp) // Adjust the size here
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }

                            else -> {
                                SubcomposeAsyncImage(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(90.dp)
                                        .width(90.dp)
                                        .clip(RoundedCornerShape(50.dp)),
                                    model = General.handlingImageForCoil(
                                        file.value!!.absolutePath,
                                        context
                                    ),
                                    contentDescription = "",
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center // Ensures the loader is centered and doesn't expand
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color.Black,
                                                modifier = Modifier.size(54.dp) // Adjust the size here
                                            )
                                        }
                                    },
                                )
                            }
                        }

                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .constrainAs(cameralRef) {
                                end.linkTo(imageRef.end)
                                bottom.linkTo(imageRef.bottom)
                            }


                    )
                    {

                        IconButton(
                            onClick = {
                                onImageSelection.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier
                                .size(30.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = CustomColor.primaryColor200
                            )
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.camera),
                                "",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                        }
                    }

                }
            }

            item {
                TextInputWithTitle(
                    value = userId,
                    title = stringResource(R.string.user_id),
                    placeHolder = myInfo.value?.name ?: "",
                    errorMessage = "",
                    isEnable = false,
                    trailIcon = {
                        if (myInfo.value != null)
                            IconButton(
                                onClick = {
                                    copyUserId()
                                }
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.copy), "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                    }
                )
            }

            item {
                TextInputWithTitle(
                    value = fullName,
                    title = stringResource(R.string.full_name),
                    placeHolder = myInfo.value?.name ?: "",
                    errorMessage = "",
                )
            }
            item {
                TextInputWithTitle(
                    value = email,
                    title = stringResource(R.string.email),
                    placeHolder = myInfo.value?.email ?: "",
                    errorMessage = "",
                    isEnable = false,
                )
            }

            item {
                TextNumberInputWithTitle(
                    value = phone,
                    title = stringResource(R.string.phone),
                    placeHolder = myInfo.value?.phone ?: "",
                    errorMessage = "",
                )
            }
            item {
                TextSecureInputWithTitle(
                    value = oldPassword,
                    title = stringResource(R.string.current_password),
                    isHasError = false,
                    errMessage = ""
                )
            }

            item {
                TextSecureInputWithTitle(
                    value = newPassword,
                    title = stringResource(R.string.new_password),
                    isHasError = false,
                    errMessage = ""
                )
            }

        }
    }

}