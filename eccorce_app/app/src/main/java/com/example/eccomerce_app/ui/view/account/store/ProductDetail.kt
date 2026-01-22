package com.example.eccomerce_app.ui.view.account.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.model.CardProductModel
import com.example.eccomerce_app.model.ProductVariant
import com.example.eccomerce_app.ui.Screens
import com.example.e_commercompose.ui.component.Sizer
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.ProductShape
import com.example.eccomerce_app.ui.component.ProductVariantComponent
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.ui.component.StoreProductQuickInfo
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
        if (id == null) return
        storeViewModel.getStoreData(storeId = id)
        bannerViewModel.getStoreBanner(id, 1)
        subCategoryViewModel.getStoreSubCategories(id, 1)
    }


    fun addProductVariant(value: ProductVariant) {

        val copyVariant = mutableListOf<ProductVariant>()
        copyVariant.add(value)
        copyVariant.addAll(selectedProductVariants.value)
        //to make user can select one productVariant with one variant id
        selectedProductVariants.value = copyVariant.distinctBy { it.variantId }
    }

    fun getStoreData() {
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
                title = stringResource(R.string.product_detail),
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

    ) { scaffoldPadding ->
        scaffoldPadding.calculateTopPadding()
        scaffoldPadding.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(scaffoldPadding)
        ) {


            item {
                if (productData != null)
                    ProductShape(
                        product = productData,
                        context = context,
                        selectedImage = selectedImage.value ?: "",
                        updateSelectIndex = { value -> selectedImage.value = value })
            }

            if (!productData?.productVariants.isNullOrEmpty()) {
                items(productData.productVariants.size) { index ->
                    val title =
                        variants.value?.firstOrNull { it.id == productData.productVariants[index][0].variantId }?.name
                            ?: ""
                    ProductVariantComponent(
                        index = index,
                        productVariants = productData.productVariants,
                        variantName = title,
                        selectedProductVariant = selectedProductVariants.value,
                        selectProductVariant = { value ->
                            addProductVariant(value)
                        }
                    )

                }
            }


            if (storeData != null)
                item {
                    Sizer(10)
                    StoreProductQuickInfo(
                        storeData = storeData,
                        context = context,
                        isCanNavigateToStore = isCanNavigateToStore,
                        getStoreData = {getStoreData()}
                    )
                }
            item {
                Sizer(130)
            }
        }
    }

}