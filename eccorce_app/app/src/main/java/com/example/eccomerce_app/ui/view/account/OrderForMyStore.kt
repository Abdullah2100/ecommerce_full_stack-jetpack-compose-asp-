package com.example.eccomerce_app.ui.view.account

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.OrderItemShape
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.viewModel.OrderItemsViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderForMyStoreScreen(
    nav: NavHostController,
    orderItemsViewModel: OrderItemsViewModel
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val orderData = orderItemsViewModel.orderItemForMyStore.collectAsState()


    val lazyState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()
    val state = rememberPullToRefreshState()


    val page = remember { mutableIntStateOf(1) }


    val currentUpdateOrderItemId = remember { mutableStateOf<UUID?>(null) }

    val isSendingData = remember { mutableStateOf(false) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }

    fun updateConditionValue(
        isSendingDataValue: Boolean? = null,
        isLoadingMoreValue: Boolean? = null,
        isRefreshValue: Boolean? = null
    ) {
        when {
            isSendingDataValue != null -> isSendingData.value = isSendingDataValue
            isLoadingMoreValue != null -> isLoadingMore.value = isLoadingMoreValue
            isRefreshValue != null -> isRefresh.value = isRefreshValue
        }
    }

    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    val snackBarHostState = remember { SnackbarHostState() }


    fun refreshOrderFun() {
        coroutine.launch {
            if (!isRefresh.value) updateConditionValue(isRefreshValue = true)
            page.intValue = 1;
            orderItemsViewModel.getMyOrderItemBelongToMyStore(
                page.intValue,
                isLoadingMore.value,
                updatePageNumber = {
                    page.intValue = it
                },
                updateLoadingState = {
                    isLoadingMore.value = it
                }
            )
            if (isRefresh.value) {
                delay(1000)
                updateConditionValue(isRefreshValue = false)
            }

        }

    }

    LaunchedEffect(reachedBottom.value) {
        if (!orderData.value.isNullOrEmpty() && reachedBottom.value && orderData.value!!.size > 23) {
            Log.d("scrollReachToBottom", "true")
            orderItemsViewModel.getMyOrderItemBelongToMyStore(
                page.intValue,
                isLoadingMore.value,
                updatePageNumber = {
                    page.intValue = it
                },
                updateLoadingState = {
                    isLoadingMore.value = it
                }
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
            SharedAppBar(
                title = stringResource(R.string.order_belong_to_my_store),
                nav = nav,
            )
        },
    )
    { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()

        PullToRefreshBox(

            isRefreshing = isRefresh.value,
            onRefresh = {
                refreshOrderFun()
            },
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize(),
            state = state,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(top = paddingValue.calculateTopPadding())
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
                    .padding(paddingValue)
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (orderData.value != null)
                    items(
                        items = orderData.value!!,
                        key = { order -> order.id }) { order ->

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .padding(top = 1.dp)
                                .border(
                                    width = 1.dp,
                                    color = CustomColor.neutralColor100,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 10.dp)
                        )
                        {
                            OrderItemShape(
                                orderItem = order,
                                context = context,
                                screenWidth = screenWidth,
                                isShowOrderStatus = true

                            )
                            Sizer(10)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                when {
                                    order.orderItemStatus == "Cancelled" || order.orderStatusName == "Regected" -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {

                                            CustomButton(
                                                isEnable = true,
                                                operation = {},
                                                buttonTitle = "Cancelled",
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }

                                    }

                                    order.orderItemStatus == "Excepted"
                                            || order.orderStatusName == "Completed"
                                            || order.orderStatusName == "Received"
                                            || order.orderStatusName == "Inway"
                                            || order.orderItemStatus == "ReceivedByDelivery"
                                        -> {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {

                                            CustomButton(
                                                isEnable = true,
                                                operation = {},
                                                buttonTitle = context.getString(R.string.excepted),
                                                color = CustomColor.alertColor_2_600
                                            )
                                        }

                                    }

                                    else -> {
                                        Box(
                                            modifier = Modifier.width(((screenWidth / 2) - 25).dp)
                                        )
                                        {

                                            CustomButton(
                                                isLoading = isSendingData.value && currentUpdateOrderItemId.value == order.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutine.launch {
                                                        currentUpdateOrderItemId.value =
                                                            order.id
                                                        updateConditionValue(isSendingDataValue = true)
                                                        val result = async {
                                                            orderItemsViewModel.updateOrderItemStatusFromStore(
                                                                order.id,
                                                                0
                                                            )
                                                        }.await()
                                                        updateConditionValue(isSendingDataValue = false)
                                                        var message =
                                                            context.getString(R.string.complete_update_orderitem_status)
                                                        if (!result.isNullOrEmpty()) {
                                                            message = result
                                                        }
                                                        snackBarHostState.showSnackbar(message)

                                                    }
                                                },

                                                buttonTitle = context.getString(R.string.except),
                                                color = CustomColor.primaryColor700
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.width(((screenWidth / 2) - 25).dp)
                                        )
                                        {

                                            CustomButton(
                                                isLoading = isSendingData.value && currentUpdateOrderItemId.value == order.id,
                                                isEnable = !isSendingData.value,
                                                operation = {
                                                    coroutine.launch {
                                                        currentUpdateOrderItemId.value =
                                                            order.id
                                                        updateConditionValue(isSendingDataValue = true)
                                                        val result = async {
                                                            orderItemsViewModel.updateOrderItemStatusFromStore(
                                                                order.id,
                                                                1
                                                            )
                                                        }.await()
                                                        updateConditionValue(isSendingDataValue = false)

                                                        var message =
                                                            context.getString(R.string.complete_update_orderitem_status)
                                                        if (!result.isNullOrEmpty()) {
                                                            message = result
                                                        }
                                                        snackBarHostState.showSnackbar(message)

                                                    }
                                                },
                                                buttonTitle = context.getString(R.string.reject),
                                                color = CustomColor.alertColor_1_600
                                            )
                                        }
                                    }
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
                    }
                }


                item {
                    Box(modifier = Modifier.height(50.dp))
                }
            }
        }


    }
}