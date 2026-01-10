package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.Image
import com.example.e_commercompose.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.theme.CustomColor


@Composable
fun TextInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String,
    placeHolder: String,
    errorMessage: String? = null,
    isEnable: Boolean? = true,
    isHasError: Boolean? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    onChange: ((value: String) -> Unit)? = null,
    fontTitle: TextUnit? = null,
    fontWeight: FontWeight? = null,
    maxLines: Int?=null,
    trailIcon:@Composable (() -> Unit)?=null,

    ) {

    val modifierWithFocus =
        Modifier
            .fillMaxWidth()
            .padding(0.dp)


    val fontScall = LocalDensity.current.fontScale
    Column {
        if (title.trim().isNotEmpty())
            Text(
                title,
                fontFamily = General.satoshiFamily,
                fontWeight = fontWeight ?: FontWeight.Bold,
                color = if (isHasError == true) CustomColor.alertColor_1_400 else CustomColor.neutralColor950,
                fontSize = fontTitle ?: (16 / fontScall).sp
            )
        Sizer(heigh = 5)
        OutlinedTextField(
            singleLine = false,
            enabled = isEnable == true,
            maxLines = maxLines?:1,
            value = value.value,
            onValueChange = {
                value.value = it
                if (onChange != null)
                    onChange(it.text)
            },
            placeholder = {
                Text(
                    placeHolder,
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = modifierWithFocus,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isHasError != true) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (isHasError != true) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError == true && errorMessage != null)
                    Text(
                        errorMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
            trailingIcon = {
                if (isHasError == true) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.allert),
                        contentDescription = "",
                        tint = CustomColor.alertColor_1_400
                    )
                }
                else if (trailIcon!=null)
                {
                    trailIcon()
                }
            },
            keyboardOptions = keyboardOptions,

            )
    }

}

@Composable
fun TextNumberInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String,
    placeHolder: String,
    isHasError: Boolean = false,
    errorMessage: String,
    fontTitle: TextUnit? = null,
    fontWeight: FontWeight? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number
    ),
    maxLines: Int = 1,

    ) {

    val pattern = remember { Regex("^\\d+\$") }
    val fontScall = LocalDensity.current.fontScale

    Column {
        Text(
            title,
            fontFamily = General.satoshiFamily,
            fontWeight = fontWeight ?: FontWeight.Medium,
            color = if (isHasError) CustomColor.alertColor_1_400 else CustomColor.neutralColor950,
            fontSize = fontTitle ?: (16 / fontScall).sp
        )
        Sizer(heigh = 5)
        OutlinedTextField(

            maxLines = maxLines,
            value = value.value,
            onValueChange = {
                if ((it.text.isEmpty() || it.text.matches(pattern)) && it.text.length < 13)
                    value.value = it
            },
            placeholder = {
                Text(
                    placeHolder,
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (!isHasError) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (!isHasError) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError)
                    Text(
                        errorMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
            trailingIcon = {
                if (isHasError) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.allert),
                        contentDescription = "",
                        tint = CustomColor.alertColor_1_400
                    )
                }

            },

            keyboardOptions = keyboardOptions,

            )
    }

}


@Composable
fun TextSecureInputWithTitle(
    value: MutableState<TextFieldValue>,
    title: String = "",
    isHasError: Boolean = false,
    errMessage: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
) {

    val isShowPassword = remember { mutableStateOf(false) }
    val fontScall = LocalDensity.current.fontScale
    Column() {
        Text(
            title,
            fontFamily = General.satoshiFamily,
            fontWeight = FontWeight.Medium,
            color = if (isHasError) CustomColor.alertColor_1_400 else CustomColor.neutralColor950,
            fontSize = (16 / fontScall).sp
        )
        Sizer(heigh = 5)

        OutlinedTextField(
            maxLines = 1,
            value = value.value,
            onValueChange = { value.value = it },
            placeholder = {
                Text(
                    stringResource(R.string.enter_your_password),
                    color = CustomColor.neutralColor500,
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = (16 / fontScall).sp
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (!isHasError) Color.Gray.copy(alpha = 0.46f) else CustomColor.alertColor_1_400,
                focusedBorderColor = if (!isHasError) Color.Black else CustomColor.alertColor_1_400
            ),
            supportingText = {
                if (isHasError)
                    Text(
                        errMessage,
                        color = CustomColor.alertColor_1_400,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = (14 / fontScall).sp,
                        modifier = Modifier.offset(x = -15.dp)
                    )
            },
            keyboardOptions = keyboardOptions,
            textStyle = TextStyle(
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Normal,
                fontSize = (16 / fontScall).sp,
                color = CustomColor.neutralColor950
            ),
            visualTransformation = if (isShowPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon =
                {
                    val iconName = if (!isShowPassword.value) R.drawable.baseline_visibility_24
                    else R.drawable.visibility_off

                    IconButton(onClick = {
                        isShowPassword.value = !isShowPassword.value
                    }) {
                        Image(
                            painterResource(iconName), contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                color = if (isHasError) CustomColor.alertColor_1_400 else Color.Gray.copy(
                                    alpha = 0.46f
                                )
                            )
                        )
                    }
                }

        )
    }

}