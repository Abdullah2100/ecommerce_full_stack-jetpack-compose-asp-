package com.example.eccomerce_app.ui.view.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.e_commercompose.model.Address
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.GeneralSettingViewModel
import com.example.eccomerce_app.viewModel.OrderViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickCurrentAddressFromAddressScreen(
    nav: NavHostController,
    bannerViewModel: BannerViewModel,
    categoryViewModel: CategoryViewModel,
    variantViewModel: VariantViewModel,
    productViewModel: ProductViewModel,
    userViewModel: UserViewModel,
    generalSettingViewModel: GeneralSettingViewModel,
    orderViewModel: OrderViewModel,
) {
    val fontScall = LocalDensity.current.fontScale

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val locations = userViewModel.userInfo.collectAsState()

    val coroutine = rememberCoroutineScope()

    val isLoading = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }


    fun initial() {
        userViewModel.getMyInfo()
        generalSettingViewModel.getGeneral(1)
        categoryViewModel.getCategories(1)
        bannerViewModel.getStoresBanner()
        variantViewModel.getVariants(1)
        productViewModel.getProducts(1)
        orderViewModel.getMyOrders(mutableIntStateOf(1))
    }


    fun choseCurrentUserLocation(address: Address){
        coroutine.launch {
            isLoading.value = true

            val result = async {
                userViewModel.updateUserAddress(
                    address.id ?: UUID.randomUUID(),
                    address.title,
                    address.longitude,
                    address.latitude
                )
            }.await()

            isLoading.value = false
            if (!result.isNullOrEmpty()) {
                snackBarHostState.showSnackbar(result)
                return@launch
            }
            userViewModel.userPassLocation(true)

            initial()
            nav.navigate(Screens.HomeGraph) {
                popUpTo(nav.graph.id) {
                    inclusive = true
                }
            }

        }

    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            SharedAppBar(
                title = stringResource(R.string.enter_your_location),
                nav=nav,
                scrollBehavior=scrollBehavior
            )
        }


    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        when (locations.value?.address.isNullOrEmpty()) {
            true -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = ImageVector
                            .vectorResource(R.drawable.location_arrow), contentDescription = "",
                        tint = CustomColor.primaryColor700,
                        modifier = Modifier.size(40.dp)
                    )

                    Sizer(10)
                    Text(
                        stringResource(R.string.there_is_no_locations_found),
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (20 / fontScall).sp,
                        color = CustomColor.neutralColor500,
                        textAlign = TextAlign.Center

                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(it)
                        .padding(horizontal = 15.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {

                    items(locations.value!!.address!!.size)
                    { index ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    choseCurrentUserLocation(locations.value!!.address!![index])
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 9.dp, start = 4.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector
                                        .vectorResource(R.drawable.location_arrow),
                                    contentDescription = "",
                                    tint = CustomColor.primaryColor700,
                                    modifier = Modifier.size(24.dp)
                                )
                                Sizer(width = 20)
                                Text(
                                    locations.value!!.address!![index].title ?: "",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (16 / fontScall).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center

                                )
                            }
                            Sizer(20)
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .height(1.dp)
                                    .background(CustomColor.neutralColor200)
                            )
                        }
                    }
                }

                if (isLoading.value)
                    Dialog(onDismissRequest = {}) {
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
            }
        }

    }
}