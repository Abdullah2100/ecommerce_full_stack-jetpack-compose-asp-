package com.example.eccomerce_app.ui.view.checkout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.viewModel.CartViewModel
import com.example.e_commercompose.R
import com.example.e_commercompose.model.PaymentMethodModel
import com.example.e_commercompose.ui.component.CustomButton
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    nav: NavHostController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    generalSettingViewModel: GeneralSettingViewModel,
    orderViewModel: OrderViewModel,
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current

    val cartData = cartViewModel.cartItems.collectAsState()
    val myInfo = userViewModel.userInfo.collectAsState()
    val generalSetting = generalSettingViewModel.generalSetting.collectAsState()
    val distanceToUser = cartViewModel.distance.collectAsState()

    val coroutine = rememberCoroutineScope()

    val isSendingData = remember { mutableStateOf(false) }

    fun updateConditionValue(isSendingDataValue: Boolean? = null) {
        if (isSendingDataValue != null) isSendingData.value = isSendingDataValue
    }

    val kiloPrice = generalSetting.value?.firstOrNull { it.name == "one_kilo_price" }?.value

    val currentAddress = myInfo.value?.address?.firstOrNull { it.isCurrent }


    val totalDeliveryPrice = (distanceToUser.value) * (kiloPrice ?: 0.0)


    val selectedPaymentMethod = remember { mutableIntStateOf(0) }


    val listOfPaymentMethod = listOf(PaymentMethodModel("Cach", R.drawable.money, 1))


    val snackBarHostState = remember { SnackbarHostState() }



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            SharedAppBar(
                title =stringResource(R.string.checkout),
                nav = nav
            )
           },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding()
                ) {
                    CustomButton(
                        isEnable = !isSendingData.value,
                        operation = {
                            coroutine.launch {
                                updateConditionValue(isSendingDataValue = true)
                                val result = async {
                                    orderViewModel.submitOrder(
                                        cartItems = cartData.value,
                                        userAddress = currentAddress!!,
                                        clearCartData = { cartViewModel.clearCart() })
                                }.await()
                                updateConditionValue(isSendingDataValue = false)
                                var message = context.getString(R.string.order_submit_successfully)
                                if (!result.isNullOrEmpty()) {
                                    message = result
                                }
                                snackBarHostState.showSnackbar(message)
                                if (result.isNullOrEmpty()) {
                                    nav.navigate(Screens.HomeGraph) {
                                        popUpTo(nav.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        },
                        buttonTitle = "Place Order",
                        isLoading = isSendingData.value
                    )
                }
            }
        }

    )
    {
        it.calculateBottomPadding()
        it.calculateTopPadding()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)


        ) {

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Delivery Address",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center

                            )
                            TextButton(onClick = { nav.navigate(Screens.EditeOrAddNewAddress) }) {
                                Text(
                                    "Change",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = CustomColor.neutralColor900,
                                    textAlign = TextAlign.Center,
                                    textDecoration = TextDecoration.Underline

                                )
                            }

                        }

                        Sizer(1)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.location_address_list),
                                "",
                                tint = CustomColor.neutralColor600
                            )
                            TextButton(onClick = {
                                nav.navigate(Screens.EditeOrAddNewAddress)
                            }) {
                                Text(
                                    currentAddress?.title ?: "",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )
                            }

                        }
                    }


                }
                Sizer(10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CustomColor.neutralColor200)
                )
                Sizer(25)

            }

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            "Payment Method",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center
                        )

                        Sizer(15)

                        LazyRow {
                            items(listOfPaymentMethod.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(((config.screenWidthDp - 30) / listOfPaymentMethod.size).dp)
                                        .border(
                                            width = if (selectedPaymentMethod.intValue == index) 0.dp else 1.dp,
                                            color = CustomColor.neutralColor200,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = if (selectedPaymentMethod.intValue == index) CustomColor.primaryColor700 else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedPaymentMethod.intValue = index
                                        },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        ImageVector.vectorResource(listOfPaymentMethod[index].icon),
                                        contentDescription = "",
                                        tint = if (selectedPaymentMethod.intValue == index) Color.White else Color.Black
                                    )
                                    Sizer(width = 5)
                                    Text(
                                        listOfPaymentMethod[index].name,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (selectedPaymentMethod.intValue == index) Color.White else CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Sizer(15)

                    }


                }
                Sizer(10)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CustomColor.neutralColor200)
                )
                Sizer(15)

            }

            item {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Order Summary",
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center

                        )


                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "\$${cartData.value.totalPrice}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }
                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Delivery Fee",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "$totalDeliveryPrice",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }
                        Sizer(15)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Distance To User In Kilo",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${distanceToUser.value}",
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center
                            )
                        }


                    }


                }

            }


        }


    }

}