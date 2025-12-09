package com.example.eccomerce_app.ui.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.savedstate.savedState
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.BannerBage
import com.example.e_commercompose.ui.component.CategoryLoadingShape
import com.example.e_commercompose.ui.component.CategoryShape
import com.example.e_commercompose.ui.component.LocationLoadingShape
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.e_commercompose.ui.component.ProductShape
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.e_commercompose.ui.component.BannerLoading
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.CurrencyViewModel
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.HomeViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.isNullOrEmpty

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight", "SuspiciousIndentation")
@Composable
fun HomePage(
    nav: NavHostController,
    bannerViewModel: BannerViewModel,
    categoryViewModel: CategoryViewModel,
    variantViewModel: VariantViewModel,
    productViewModel: ProductViewModel,
    userViewModel: UserViewModel,
    generalSettingViewModel: GeneralSettingViewModel,
    orderViewModel: OrderViewModel,
    homeViewModel: HomeViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val configuration = LocalConfiguration.current
    val lazyState = rememberLazyListState()
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    val myInfo = userViewModel.userInfo.collectAsState()
    val banner = bannerViewModel.bannersRadom.collectAsState()
    val categories = categoryViewModel.categories.collectAsState()
    val products = productViewModel.products.collectAsState()
    val accessHomeScreenCounter = homeViewModel.accessHomeScreenCounter.collectAsState()

    val isClickingSearch = remember { mutableStateOf(false) }
    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val isFirst = savedState { derivedStateOf { accessHomeScreenCounter.value == 1 } }
    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    val page = remember { mutableIntStateOf(1) }

    val sizeAnimation = animateDpAsState(if (!isClickingSearch.value) 80.dp else 0.dp)


    val interactionSource = remember { MutableInteractionSource() }


    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permission ->
        }
    )


    fun initialDataLoad(showRefreshIndicator: Boolean = false) {
        homeViewModel.increaseAccessHomeScreenCounter()
        if (showRefreshIndicator)
            isRefresh.value = true

        scope.launch {
            userViewModel.getMyInfo()
            generalSettingViewModel.getGeneral(1)
            categoryViewModel.getCategories(1)
            bannerViewModel.getStoresBanner()
            variantViewModel.getVariants(1)
            productViewModel.getProducts(mutableIntStateOf(1)) // Pass the reset page
            orderViewModel.getMyOrders(mutableIntStateOf(1)) // Ensure page is managed here too if paginated
            currencyViewModel.getCurrencies(1)
            if (showRefreshIndicator) {
                delay(1000)
                isRefresh.value = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(input = Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(isFirst) {
        initialDataLoad()
    }


    LaunchedEffect(reachedBottom.value) {
        if (!products.value.isNullOrEmpty() && reachedBottom.value && products.value!!.size > 23) {
            productViewModel.getProducts(
                page,
                isLoadingMore
            )
        }

    }

    PullToRefreshBox(
        isRefreshing = isRefresh.value,
        onRefresh = {
            initialDataLoad(true)
        },
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefresh.value,
                containerColor = Color.White,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { scaffoldState ->
            scaffoldState.calculateTopPadding()
            scaffoldState.calculateBottomPadding()



            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 15.dp)
                    .padding(top = 50.dp)

            ) {

                //address info
                item {
                    when (myInfo.value?.address == null && categories.value == null) {
                        true -> {
                            LocationLoadingShape((configuration.screenWidthDp))
                        }

                        else -> {
                            if (!myInfo.value?.address.isNullOrEmpty())
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.height(sizeAnimation.value)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .width(width = (configuration.screenWidthDp - 30 - 34).dp)
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
                                            myInfo.value?.address?.firstOrNull { it.isCurrent }?.title
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

                //this the search box
                if (!products.value.isNullOrEmpty())
                    item {
                        Card(
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 10.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        isClickingSearch.value = !isClickingSearch.value
                                    }
                                ), colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 5.dp,
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 15.dp, bottom = 15.dp, start = 4.dp)

                            ) {

                                Icon(
                                    Icons.Outlined.Search, "",
                                    tint = CustomColor.neutralColor950,
                                    modifier = Modifier.size(24.dp)
                                )
                                Sizer(width = 5)
                                Text(
                                    stringResource(R.string.find_your_favorite_items),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = CustomColor.neutralColor800,
                                    textAlign = TextAlign.Center

                                )

                            }

                        }
                    }


                item {
                    when (categories.value == null) {
                        true -> {
                            CategoryLoadingShape()
                        }

                        else -> {
                            when (categories.value!!.isEmpty()) {
                                true -> {}
                                else -> {

                                    CategoryShape(
                                        categories = categories.value!!.take(4),
                                        productViewModel = productViewModel,
                                        nav = nav
                                    )
                                }
                            }
                        }
                    }
                }

                //banner section
                item {
                    when (banner.value == null) {
                        true -> {
                            BannerLoading()
                        }

                        else -> {
                            if (!banner.value.isNullOrEmpty())
                                BannerBage(
                                    banners = banner.value!!,
                                    isMe = false,
                                    nav = nav
                                )
                        }
                    }


                }


                //product

                item {

                    Sizer(10)
                    when (products.value == null) {
                        true -> {
                            ProductLoading()
                        }

                        else -> {
                            if (products.value!!.isNotEmpty()) {
                                ProductShape(products.value!!, nav = nav)
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
                    Sizer(140)
                }
            }

        }
    }
}