package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.dto.UpdateMyInfoDto
import com.example.eccomerce_app.util.General
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedAppBar(
    title:String?=null,
    nav: NavHostController?=null,
    action:(@Composable ()->Unit)?=null,
    scrollBehavior: TopAppBarScrollBehavior?=null,
){
    val fontScall = LocalDensity.current.fontScale

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        title = {
            if(title!=null)
            Text(
                title,
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                color = CustomColor.neutralColor950,
                fontSize = (24 / fontScall).sp,
                modifier = Modifier
                    .fillMaxWidth()

            )
        },
        navigationIcon = {
           if(nav!=null)
            IconButton(
                onClick = {
                    nav.popBackStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    "",
                    modifier = Modifier.size(30.dp),
                    tint = CustomColor.neutralColor950
                )
            }
        },

        actions = {
         if(action!=null)
             action()
        },
        scrollBehavior = scrollBehavior,

    )
}