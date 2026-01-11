package com.example.eccomerce_app.ui.view.account.store

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.model.CardProductModel
import com.example.e_commercompose.model.ProductVariant
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.util.General.convertPriceToAnotherCurrency
import com.example.eccomerce_app.viewModel.CartViewModel
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CurrencyViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetail(
    nav: NavHostController,
    cartViewModel: CartViewModel,
    productID: String?,
    isFromHome: Boolean,
    variantViewModel: VariantViewModel,
    storeViewModel: StoreViewModel,
    bannerViewModel: BannerViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    productViewModel: ProductViewModel,
    isCanNavigateToStore: Boolean,
    userViewModel: UserViewModel,
    currencyViewModel: CurrencyViewModel,

    ) {

    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    val productId = if (productID == null) null else UUID.fromString(productID)


    val products = productViewModel.products.collectAsState()
    val variants = variantViewModel.variants.collectAsState()
    val stores = storeViewModel.stores.collectAsState()
    val myInfo = userViewModel.userInfo.collectAsState()
    val defaultCurrency = currencyViewModel.selectedCurrency.collectAsState()
    val currencies = currencyViewModel.currenciesList.collectAsState()

    val productData = products.value?.firstOrNull { it.id == productId }
    val storeData = stores.value?.firstOrNull { it.id == productData?.storeId }


    val selectedImage = remember { mutableStateOf(productData?.thumbnail) }

    val selectedProductVariants = remember { mutableStateOf<List<ProductVariant>>(emptyList()) }

    if (selectedProductVariants.value.isEmpty() && productData?.productVariants?.isNotEmpty() == true) {
        productData.productVariants.forEach { it ->
            val firstElement = it.first()
            val copySelectedList = mutableListOf<ProductVariant>()
            copySelectedList.add(
                ProductVariant(
                    id = firstElement.id,
                    name = firstElement.name,
                    percentage = firstElement.percentage,
                    variantId = firstElement.variantId
                )
            )
            if (selectedProductVariants.value.isNotEmpty())
                copySelectedList.addAll(selectedProductVariants.value)
            selectedProductVariants.value = copySelectedList
        }
    }


    val images = remember { mutableStateOf(productData?.productImages) }


    if (productData?.thumbnail != null) {
        if (images.value != null && !images.value!!.contains(productData.thumbnail)) {

            val imageWithThumbnails = mutableListOf<String>()
            imageWithThumbnails.add(productData.thumbnail)

            if (images.value != null) {
                imageWithThumbnails.addAll(images.value!!)
            }

            images.value = imageWithThumbnails

        } else if (images.value == null) {
            images.value = listOf<String>(productData.thumbnail)
        }
    }

    fun getStoreInfoByStoreId(id: UUID? = UUID.randomUUID()) {
        if(id==null) return
         storeViewModel.getStoreData(storeId = id)
        bannerViewModel.getStoreBanner(id,1)
        subCategoryViewModel.getStoreSubCategories(id, 1)
    }


    fun addProductVariant(value: ProductVariant) {

        val copyVariant = mutableListOf<ProductVariant>()
        copyVariant.add(value)
        copyVariant.addAll(selectedProductVariants.value)
        //to make user can select one productVariant with one variant id
        selectedProductVariants.value = copyVariant.distinctBy { it.variantId }
    }

    fun getStoreData(){
        if (productData != null)
            productViewModel
                .getProducts(
                    pageNumber = 1,
                    storeId = productData.storeId
                )
        nav.navigate(
            Screens.Store(
                storeId = storeData?.id.toString(),
                isFromHome = true
            )
        )
    }
    if (!isFromHome) {
        LaunchedEffect(Unit) {
            getStoreInfoByStoreId(
                id = productData?.storeId ?: UUID.randomUUID()
            )
            delay(3000)

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
                title =stringResource(R.string.product_detail),
                nav = nav,
            )
           },
        bottomBar = {
//            if (isFromHome && (myInfo.value == null || (myInfo.value != null && myInfo.value?.storeId != productData?.storeId)))
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    onClick = {
                        cartViewModel.addToCart(
                            product = CardProductModel(
                                id = UUID.randomUUID(),
                                productId = productData!!.id,
                                name = productData.name,
                                thumbnail = productData.thumbnail,
                                price = convertPriceToAnotherCurrency(
                                    productData.price,
                                    productData.symbol,
                                    defaultCurrency.value,
                                    currencies.value
                                ),
                                productVariants = selectedProductVariants.value,
                                storeId = productData.storeId
                            )
                        )
                        coroutine.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.item_added_to_cart))

                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomColor.primaryColor400
                    ),

                    ) {


                    Text(
                        stringResource(R.string.add_to_cart),
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (16).sp
                    )


                }

            }
        }

    ) {
        it.calculateTopPadding()
        it.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
        ) {

            item {
                SubcomposeAsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(),
                    model = General.handlingImageForCoil(
                        selectedImage.value,
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

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start

                ) {
                    if (images.value != null && images.value!!.size >= 2) {
                        Sizer(10)
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            if (!images.value.isNullOrEmpty())
                                items(
                                    items = images.value as List<String>,
                                    key = { image -> image }) { image ->

                                    Box(
                                        modifier = Modifier
                                            .padding(end = 5.dp)
                                            .border(
                                                1.dp,
                                                if (image == selectedImage.value)
                                                    CustomColor.primaryColor700 else CustomColor.neutralColor200,
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(50.dp)
                                                .width(50.dp)
                                                .clip(
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    if (image != selectedImage.value)
                                                        selectedImage.value = image
                                                },
                                            model = General.handlingImageForCoil(
                                                image,
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
                                                        modifier = Modifier.size(25.dp) // Adjust the size here
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                        }

                    }
                    Sizer(10)
                    Text(
                        text = productData?.name ?: "",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (18).sp
                    )
                    Sizer(16)
                    Text(
                        text = "\$${productData?.price}",
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Sizer(16)
                    Text(
                        text = stringResource(R.string.product_details),
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Sizer(10)
                    Text(
                        text = productData?.description ?: "",
                        color = CustomColor.neutralColor800,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                    Sizer(15)
                }
            }

            if (!productData?.productVariants.isNullOrEmpty()) {
                items(productData.productVariants.size) { index ->
                    val title =
                        variants.value?.firstOrNull { it.id == productData.productVariants!![index][0].variantId }?.name
                            ?: ""
                    Text(
                        text = stringResource(R.string.select),
                        color = CustomColor.neutralColor950,
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Sizer(10)
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            color = CustomColor.neutralColor950,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Sizer(width = 10)
                        FlowRow(
                            modifier = Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            repeat(productData.productVariants[index].size) { pvIndex ->

                                val productVariantHolder = ProductVariant(
                                    id = productData.productVariants[index][pvIndex].id,
                                    name = productData.productVariants[index][pvIndex].name,
                                    percentage = productData.productVariants[index][pvIndex].percentage,
                                    variantId = productData.productVariants[index][pvIndex].variantId
                                )


                                when (title == "Color" || title == "color") {
                                    true -> {
                                        val colorValue =
                                            General.convertColorToInt(productData.productVariants[index][pvIndex].name)

                                        if (colorValue != null)
                                            Box(
                                                modifier = Modifier
                                                    .height(24.dp)
                                                    .width(24.dp)
                                                    .border(
                                                        width = if (selectedProductVariants.value.contains(
                                                                productVariantHolder
                                                            )
                                                        ) 1.dp else 0.dp,
                                                        color = if (selectedProductVariants.value.contains(
                                                                productVariantHolder
                                                            )
                                                        ) CustomColor.primaryColor700
                                                        else Color.White,
                                                        shape = RoundedCornerShape(20.dp)
                                                    )
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .clickable { addProductVariant(productVariantHolder) }
                                            )
                                            {
                                                Box(
                                                    Modifier
                                                        .padding(2.dp)
                                                        .height(22.dp)
                                                        .width(22.dp)
                                                        .background(
                                                            colorValue,
                                                            RoundedCornerShape(20.dp)
                                                        )

                                                ) { }
                                            }
                                    }

                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    1.dp,
                                                    if (selectedProductVariants.value.contains(
                                                            productVariantHolder
                                                        )
                                                    ) CustomColor.primaryColor700 else CustomColor.neutralColor200,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 10.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable {  addProductVariant(productVariantHolder)   },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = productData.productVariants[index][pvIndex].name,
                                                color = CustomColor.neutralColor950,
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                // modifier = Modifier.padding(start = 15.dp)
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }


            if (storeData != null)
                item {
                    Sizer(10)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        SubcomposeAsyncImage(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            model = General.handlingImageForCoil(
                                storeData.smallImage,
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
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.padding(start = 5.dp)
                        ) {

                            Text(
                                storeData.name,
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (isCanNavigateToStore)
                                Text(
                                    stringResource(R.string.visit_store),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (16).sp,
                                    color = CustomColor.primaryColor700,
                                    modifier = Modifier
                                        .clickable {
                                        getStoreData()
                                    }
                                )


                        }
                    }
                }
            item {
                Sizer(130)
            }
        }
    }

}