package com.example.eccomerce_app.ui.view.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.e_commercompose.ui.component.CustomButton
import com.example.eccomerce_app.ui.component.OrderItemShape
import com.example.eccomerce_app.viewModel.OrderViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
//import qrgenerator.QRCodeImage
import java.util.UUID
import com.example.e_commercompose.R
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(orderViewModel: OrderViewModel) {

    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()
    val state = rememberPullToRefreshState()


    val orders = orderViewModel.orders.collectAsState()

    val isSendingData = remember { mutableStateOf(false) }
    val isShowDialog = remember { mutableStateOf(false) }
    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }

    val deletedId = remember { mutableStateOf<UUID?>(null) }

    val qrBitMap = remember { mutableStateOf<Bitmap?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }


    val page = remember { mutableIntStateOf(1) }


    fun openQrDialog(id: UUID) {
        isShowDialog.value = true
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                id.toString(),
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            qrBitMap.value = bitmap
        } catch (e: Exception) {
            isShowDialog.value = false;

        }

    }

    LaunchedEffect(reachedBottom.value) {

        if (!orders.value.isNullOrEmpty() && reachedBottom.value && orders.value!!.size > 23) {
            orderViewModel.getMyOrders(
                page,
                isLoadingMore
            )
        }

    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            SharedAppBar(title = stringResource(R.string.my_order))

        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 65.dp)
                    .offset(x = 16.dp),
            ) {

            }
        }
    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()
        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                coroutine.launch {
                    if (!isRefresh.value) isRefresh.value = true
                    orderViewModel.getMyOrders(mutableIntStateOf(1))
                    if (isRefresh.value) {
                        delay(1000)
                        isRefresh.value = false
                    }

                }
            },
            state = state,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding() + 1.dp)
                        .align(Alignment.TopCenter),
                    isRefreshing = isRefresh.value,
                    containerColor = Color.White,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = state
                )
            },
        )
        {
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    orders.value?.forEach { order ->

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                                .border(
                                    1.dp,
                                    CustomColor.neutralColor200,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 10.dp)

                        ) {
                            order.orderItems.forEach { value ->
                                OrderItemShape(
                                    orderItem = value,
                                    context = context,
                                    screenWidth = screenWidth

                                )
                                Sizer(5)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Text(
                                        stringResource(R.string.total_price),
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "$${order.totalPrice}",

                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Sizer(5)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Text(
                                        stringResource(R.string.deliveryFee_price),
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "$${order.deliveryFee}",

                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Sizer(5)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Text(
                                        stringResource(R.string.order_status),
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        order.status,

                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    Text(
                                        stringResource(R.string.order_qr),
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )

                                    Image(
                                        imageVector = ImageVector.vectorResource(R.drawable.qr_button),
                                        "",
                                        modifier = Modifier.clickable(onClick = {
                                            openQrDialog(order.id)
                                        })
                                    )
                                }
                                Sizer(5)
                                if (!(order.status == "Received" || order.status == "Completed"))
                                    CustomButton(

                                        buttonTitle = stringResource(R.string.cancel_order),
                                        operation = {
                                            deletedId.value = order.id
                                            coroutine.launch {

                                                isSendingData.value = true

                                                val result = async {
                                                    orderViewModel.deleteOrder(order.id)
                                                }.await()

                                                isSendingData.value = false
                                                val message = result
                                                    ?: context.getString(R.string.order_deleted_successfully)

                                                snackBarHostState.showSnackbar(message)
                                            }

                                        },
                                        color = CustomColor.alertColor_1_600,
                                        isLoading = isSendingData.value && deletedId.value == order.id
                                    )
                            }
                        }

                    }
                }

                if (isLoadingMore.value) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
                            CircularProgressIndicator(color = CustomColor.primaryColor700)
                        }
                        Sizer(40)
                    }
                }


                item {
                    Box(modifier = Modifier.height(90.dp))
                }
            }

        }
        if (isSendingData.value)
            Dialog(
                onDismissRequest = {}
            )
            {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                )
                {
                    CircularProgressIndicator(
                        color = CustomColor.primaryColor700,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

        if (isShowDialog.value) {
            Dialog(
                onDismissRequest = { isShowDialog.value = false },
                properties = DialogProperties(), content = {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        if (qrBitMap.value != null)
                            Image(
                                bitmap = qrBitMap.value!!.asImageBitmap(),
                                contentDescription = "",
                                modifier = Modifier
                                    .height(250.dp)
                                    .width(200.dp)
                                    .clip(
                                        RoundedCornerShape(8.dp)
                                    ),

                                )
                    }
                })
        }


    }
}

