package com.example.eccomerce_app.ui.view.account.store.delivery

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.model.Delivery
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.viewModel.DeliveryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesListScreen(
    nav: NavHostController,
    deliveryViewModel: DeliveryViewModel
) {

    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenHeightDp

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state = rememberPullToRefreshState()
    val coroutine = rememberCoroutineScope()
    val lazyState = rememberLazyListState()

    val deliveries = deliveryViewModel.deliveries.collectAsState()


    val snackBarHostState = remember { SnackbarHostState() }

    val errorMessage = remember { mutableStateOf("") }

    val page = remember { mutableIntStateOf(1) }


    val isRefresh = remember { mutableStateOf(false) }
    val isSendingData = remember { mutableStateOf(false) }
    val isAddingDialog = remember { mutableStateOf(false) }

    val userId = remember { mutableStateOf(TextFieldValue("")) }

    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    fun resetInput() {
        userId.value = TextFieldValue("")
        errorMessage.value = ""
    }

    fun createDeliveryMan() {
        coroutine.launch {
            isSendingData.value = true
            isAddingDialog.value = false
            val result = deliveryViewModel.createDelivery(UUID.fromString(userId.value.text))
            isSendingData.value = false
            if (result.isNullOrEmpty()) {
                resetInput()
                snackBarHostState.showSnackbar(context.getString(R.string.delivery_created_successfully))
                return@launch
            }
            snackBarHostState.showSnackbar(result)

        }
    }

    fun refreshDeliveryList(){
        coroutine.launch {
            if (!isRefresh.value) isRefresh.value = true
            page.intValue = 1
            deliveryViewModel.getDeliveryBelongToStore(page.intValue){value->
                page.intValue=value
            }
            if (isRefresh.value) {
                delay(1000)
                isRefresh.value = false
            }

        }
    }



    LaunchedEffect(reachedBottom.value) {
        if (!deliveries.value.isNullOrEmpty() && reachedBottom.value && deliveries.value!!.size > 23) {
            deliveryViewModel.getDeliveryBelongToStore(page.intValue){value->
                page.intValue=value
            }
        }
    }

    LaunchedEffect(Unit) {
        deliveryViewModel.getDeliveryBelongToStore(1)
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
                title =  stringResource(R.string.deliveries),
                nav = nav,
                scrollBehavior = scrollBehavior
            )

        },
        floatingActionButton = {

            FloatingActionButton(
                onClick = { isAddingDialog.value = true },
                containerColor = CustomColor.primaryColor500
            ) {
                Icon(
                    Icons.Default.Add,
                    "", tint = Color.White
                )
            }

        }

    ) { paddingValues ->
        paddingValues.calculateTopPadding()
        paddingValues.calculateBottomPadding()


        if (isSendingData.value)
            Dialog(
                onDismissRequest = {})
            {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .background(
                            Color.White, RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CustomColor.primaryColor700, modifier = Modifier.size(40.dp)
                    )
                }
            }


        if (isAddingDialog.value)
            Dialog(
                onDismissRequest = {
                    isAddingDialog.value = false
                    resetInput()
                }
            )
            {
                Column(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(7.dp))
                        .padding(horizontal = 10.dp)

                ) {
                    Sizer(10)
                    TextInputWithTitle(
                        userId,
                        title = "", stringResource(R.string.user_id),
                        errorMessage = errorMessage.value,
                        isHasError = errorMessage.value.isNotEmpty(),
                        maxLines = 2
                    )

                    if (errorMessage.value.isNotEmpty())
                        Sizer(heigh = 10)

                    CustomAuthBottom(
                        isLoading = isSendingData.value,
                        operation = {
                            createDeliveryMan()
                        },
                        validationFun = {
                            if (userId.value.text.isEmpty()) {
                                errorMessage.value =
                                    context.getString(R.string.user_id_must_not_be_empty)
                                return@CustomAuthBottom false
                            } else return@CustomAuthBottom true
                        },
                        buttonTitle = stringResource(R.string.create),
                        isHasBottomPadding = false
                    )
                    Sizer(10)
                }
            }

        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                refreshDeliveryList()
            },
            modifier = Modifier
                .background(Color.White)

                .fillMaxSize(),
            state = state,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(top = 5.dp)
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
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .background(Color.White)
                    .fillMaxSize()
            ) {
                if (deliveries.value != null)
                    items(
                        items = deliveries.value as List<Delivery>,
                        key = { delivery -> delivery.id },
                    ) { delivery ->

                        Box(
                            modifier = Modifier
                                .height(140.dp)
                                .fillParentMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(8.dp))
                                .padding(top = 1.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically

                                ) {
                                ConstraintLayout(
                                    modifier = Modifier
                                        .wrapContentSize()
                                )
                                {
                                    val (imageRef) = createRefs()
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

                                        when (delivery.thumbnail.isNullOrEmpty()) {
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
                                                        delivery.thumbnail,
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
                                }

                                Column(
                                    modifier = Modifier
                                        .fillParentMaxHeight()
                                        .width((screenWidth - 130).dp)
                                        .padding(start = 5.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(
                                        delivery.user.name,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (19).sp,
                                        color = CustomColor.neutralColor950,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        delivery.user.phone,
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (19).sp,
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
}