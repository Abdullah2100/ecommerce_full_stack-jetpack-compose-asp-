package com.example.eccomerce_app.ui.view.checkout

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.*
import androidx.navigation.NavHostController
import com.example.eccomerce_app.viewModel.*
import com.stripe.android.paymentsheet.PaymentSheetResult
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.e_commercompose.model.PaymentMethodModel
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.LabelValueRow
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.Secrets
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import kotlinx.coroutines.*


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    nav: NavHostController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    generalSettingViewModel: GeneralSettingViewModel,
    orderViewModel: OrderViewModel,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current
    val publicKey = rememberSaveable{mutableStateOf(Secrets.getStripKey()) }
    val coroutine = rememberCoroutineScope()
    val cartData = cartViewModel.cartItems.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }


    val config = LocalConfiguration.current
     val myInfo = userViewModel.userInfo.collectAsState()
     val generalSetting = generalSettingViewModel.generalSetting.collectAsState()
     val distanceToUser = cartViewModel.distance.collectAsState()


     val isSendingData = remember { mutableStateOf(false) }

     fun updateConditionValue(isSendingDataValue: Boolean? = null) {
         if (isSendingDataValue != null) isSendingData.value = isSendingDataValue
     }

     val kiloPrice = generalSetting.value?.firstOrNull { it.name == "one_kilo_price" }?.value

     val currentAddress = myInfo.value?.address?.firstOrNull { it.isCurrent }


     val totalDeliveryPrice = (distanceToUser.value) * (kiloPrice ?: 0.0)


     val selectedPaymentMethod = remember { mutableIntStateOf(0) }

     val paymentResultCallback: (PaymentSheetResult) -> Unit = { result: PaymentSheetResult ->
         when (result) {
             is PaymentSheetResult.Completed -> { /* Success! */
                 coroutine.launch {

                     val  orderResult = async {
                         orderViewModel.submitOrder(
                             cartItems = cartData.value,
                             userAddress = currentAddress!!,
                             clearCartData = { cartViewModel.clearCart() })
                     }.await()
                     updateConditionValue(isSendingDataValue = false)

                     var message = context.getString(R.string.order_submit_successfully)
                     if (!orderResult.isNullOrEmpty()) {
                         message =orderResult
                     }
                     snackBarHostState.showSnackbar(message)
                     if (orderResult.isNullOrEmpty()) {
                         nav.navigate(Screens.HomeGraph) {
                             popUpTo(nav.graph.id) {
                                 inclusive = true
                             }
                         }
                     }
                 }
             }

             is PaymentSheetResult.Canceled -> { /* User backed out */
                 coroutine.launch {
                     updateConditionValue(isSendingDataValue = false)
                     snackBarHostState.showSnackbar("Submit Cancel")

                 }
             }

             is PaymentSheetResult.Failed -> { /* Show error: result.error */
                 coroutine.launch {
                     updateConditionValue(isSendingDataValue = false)
                     snackBarHostState.showSnackbar("Submit Cancel")

                 }
             }
         }
     }

     val listOfPaymentMethod = listOf(
         PaymentMethodModel("Cash", R.drawable.money, 1),
         PaymentMethodModel("Card", R.drawable.money, 1),
         PaymentMethodModel("Card", R.drawable.money, 1),
         PaymentMethodModel("Card", R.drawable.money, 1),
         PaymentMethodModel("Card", R.drawable.money, 1),
     )

    val paymentSheet = remember { PaymentSheet.Builder(paymentResultCallback) }.build()


     fun submitOrder(){
         coroutine.launch {

             updateConditionValue(isSendingDataValue = true)

                 val result = async { paymentViewModel.submitOrderToStripe(cartData.value.totalPrice) }.await()
                 if (result == null) {return@launch}

                 Log.d("thisResultFromApi",result)
                 // Guest checkout - create new overload or use Option 1
                 paymentSheet.presentWithPaymentIntent(
                     result,
                     PaymentSheet.Configuration.Builder(merchantDisplayName = "My merchant name")
                         .allowsDelayedPaymentMethods(true)
                         .build()
                 )






         }

     }

    LaunchedEffect(context) {
        PaymentConfiguration.init(context, publicKey.value )
    }

     Scaffold(
         snackbarHost = {
             SnackbarHost(hostState = snackBarHostState)
         },
         topBar = {
             SharedAppBar(
                 title = stringResource(R.string.checkout),
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
                         operation = {submitOrder()},
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
                 .padding(horizontal = 15.dp)


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
                                 .fillMaxWidth(),
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

                 Column(
                     modifier = Modifier
                         .fillMaxWidth()
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

                     FlowRow(
                         horizontalArrangement = Arrangement.SpaceBetween,
                         modifier = Modifier
                             .fillParentMaxWidth(),
                         verticalArrangement = Arrangement.spacedBy(7.dp)

                     ) {
                         repeat(listOfPaymentMethod.size) { index ->
                             Row(
                                 modifier = Modifier
                                     .height(50.dp)
                                     .width(
                                         if (listOfPaymentMethod.size <= 3) ((((config.screenWidthDp - 30) / listOfPaymentMethod.size) - 15).dp)
                                         else ((listOfPaymentMethod[index].name.length * 18) + 20).dp
                                     )
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
                     Column {
                         Text(
                             text = "Order Summary",
                             fontFamily = General.satoshiFamily,
                             fontWeight = FontWeight.Bold,
                             fontSize = 16.sp,
                             color = CustomColor.neutralColor950,
                             textAlign = TextAlign.Center

                         )
                         Sizer(15)
                         LabelValueRow("Total", $$"$$${cartData.value.totalPrice}")
                         Sizer(15)
                         LabelValueRow("Delivery Fee", "$totalDeliveryPrice")
                         Sizer(15)
                         LabelValueRow("Distance To User In Kilo", "${distanceToUser.value}")


                     }


                 }

             }


         }


     }



}

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when(paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            print("Canceled")
        }
        is PaymentSheetResult.Failed -> {
            print("Error: ${paymentSheetResult.error}")
        }
        is PaymentSheetResult.Completed -> {
            // Display for example, an order confirmation screen
            print("Completed")
        }
    }

}