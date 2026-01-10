package com.example.eccomerce_app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.e_commercompose.model.Address
import com.example.e_commercompose.ui.component.LocationLoadingShape
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.util.General


@Composable
fun HomeAddressComponent(
    address: Address?=null,
    isPassCondition: Boolean = false,
    screenWidth:Int,
    animatedComponentSize:Dp,
    nav: NavHostController
){
    val interactionSource = remember { MutableInteractionSource() }

    when (isPassCondition) {
        true -> {
            LocationLoadingShape((screenWidth))
        }

        else -> {
            if (address!=null)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height((animatedComponentSize))
                ) {
                    Column(
                        modifier = Modifier
                            .width(width = (screenWidth - 30 - 34).dp)
                            .clickable(
                                enabled = true,
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    nav.navigate(Screens.EditeOrAddNewAddress)
                                }
                            )
                    ) {
                        Text(
                            stringResource(R.string.location),
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = CustomColor.neutralColor800,
                            textAlign = TextAlign.Center

                        )
                        Sizer(1)
                        Text(
                            address.title
                                ?: "",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center

                        )
                    }

                    Icon(
                        Icons.Outlined.Notifications,
                        "",
                        tint = CustomColor.neutralColor950,
                        modifier = Modifier.size(30.dp)

                    )
                }
        }
    }

}