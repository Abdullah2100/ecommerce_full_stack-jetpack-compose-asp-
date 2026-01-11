package com.example.eccomerce_app.ui.view.account.store

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.e_commercompose.dto.ModelToDto.toListOfProductVarient
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextNumberInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.viewModel.CurrencyViewModel
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.eccomerce_app.viewModel.VariantViewModel
import com.example.hotel_mobile.Util.Validation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    nav: NavHostController,
    storeId: String,
    productId: String? = null,
    subCategoryViewModel: SubCategoryViewModel,
    variantViewModel: VariantViewModel,
    productViewModel: ProductViewModel,
    currencyViewModel: CurrencyViewModel

) {
    val context = LocalContext.current

    val coroutine = rememberCoroutineScope()

    val storeIdHolder = UUID.fromString(storeId)
    val productIdHolder = if (productId == null) null else UUID.fromString(productId)


    val products = productViewModel.products.collectAsState()
    val variants = variantViewModel.variants.collectAsState()
    val currencies = currencyViewModel.currenciesList.collectAsState()

    val subCategory = subCategoryViewModel.subCategories.collectAsState()
    val storeSubCategory = subCategory.value?.filter { it.storeId == storeIdHolder }


    val productData = if (productIdHolder == null) null
    else products.value?.firstOrNull { it.id == productIdHolder }


    val thumbnail = remember { mutableStateOf(productData?.thumbnail) }


    val images = remember { mutableStateOf(productData?.productImages ?: emptyList()) }
    val productVariants = remember {
        mutableStateOf(
            if (productData != null && !productData.productVariants.isNullOrEmpty()) productData.productVariants.toListOfProductVarient()
            else emptyList()
        )
    }
    val deleteImages = remember { mutableStateOf<List<String>>(emptyList()) }
    val deleteProductVariant =
        remember { mutableStateOf<List<ProductVarientSelection>>(emptyList()) }


    val productName = remember { mutableStateOf(TextFieldValue("")) }
    val description = remember { mutableStateOf(TextFieldValue("")) }
    val price = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantName = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantPercentage = remember { mutableStateOf(TextFieldValue("")) }


    val productCurrency = remember { mutableStateOf<String?>(null) }


    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val selectedVariantId = remember { mutableStateOf<UUID?>(null) }


    val isExpandedSubCategory = remember { mutableStateOf(false) }
    val isExpandedCurrency = remember { mutableStateOf(false) }
    val isExpandedVariant = remember { mutableStateOf(false) }
    val isSendingData = remember { mutableStateOf(false) }


    val animated = animateDpAsState(
        if (isExpandedSubCategory.value) ((storeSubCategory?.size ?: 1) * 45).dp else 0.dp
    )

    val currencyAnimated = animateDpAsState(
        if (isExpandedCurrency.value) ((currencies.value?.size ?: 1) * 45).dp else 0.dp
    )
    val rotation = animateFloatAsState(if (isExpandedSubCategory.value) 180f else 0f)
    val currencyRotation = animateFloatAsState(if (isExpandedCurrency.value) 180f else 0f)
    val animatedVariant = animateDpAsState(
        if (isExpandedVariant.value) ((variants.value?.size ?: 1) * 45).dp else 0.dp
    )
    val rotationVariant = animateFloatAsState(if (isExpandedVariant.value) 180f else 0f)

    val snackBarHostState = remember { SnackbarHostState() }


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                if (thumbnail.value != null && !deleteImages.value.contains(thumbnail.value)) {
                    val deleteImageCopy = mutableListOf<String>()
                    deleteImageCopy.add(thumbnail.value!!)
                    deleteImageCopy.addAll(deleteImages.value)
                    deleteImages.value = deleteImageCopy
                }
                thumbnail.value = fileHolder.absolutePath

            }
        }
    }


    val selectMultipleImages = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    )
    { uris ->
        val imagesHolder = mutableListOf<String>()

        if (uris.isNotEmpty()) {
            uris.forEach { productImages ->
                val file = productImages.toCustomFil(context)

                if (file != null) {
                    imagesHolder.add(file.absolutePath)
                }
            }
            if (imagesHolder.isNotEmpty()) {
                imagesHolder.addAll(images.value)
                images.value = imagesHolder
            }
        }
    }

    fun updateConditionValue(
        isExpandedSubCategoryValue: Boolean? = null,
        isExpandedCurrencyValue: Boolean? = null,
        isExpandedVariantValue: Boolean? = null,
        isSendingDataValue: Boolean? = null
    ) {
        when {
            isExpandedSubCategoryValue != null -> isExpandedSubCategory.value =
                isExpandedSubCategoryValue

            isExpandedCurrencyValue != null -> isExpandedCurrency.value = isExpandedCurrencyValue
            isExpandedVariantValue != null -> isExpandedVariant.value = isExpandedVariantValue
            isSendingDataValue != null -> isSendingData.value = isSendingDataValue
        }
    }

    fun validateInput(): Boolean {
        var errorMessage = ""
        if (thumbnail.value == null) {
            errorMessage = context.getString(R.string.product_thumbnail_is_require)
        } else if (images.value.isEmpty())
            errorMessage = context.getString(R.string.you_must_select_least_one_image_for_product)
        else if (productName.value.text.trim().isEmpty())
            errorMessage = context.getString(R.string.product_name_is_require)
        else if (description.value.text.trim().isEmpty())
            errorMessage = context.getString(R.string.product_description_is_required)
        else if (price.value.text.trim().isEmpty())
            errorMessage = context.getString(R.string.product_price_is_required)
        else if (productCurrency.value == null)
            errorMessage = "You must Select Currency"
        else if (selectedSubCategoryId.value == null)
            errorMessage = context.getString(R.string.you_must_select_subcategory)
        if (errorMessage.isNotEmpty()) {
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true
    }

    //this for api product images not for create this only when product is update
    fun removeProductImages(image: String) {
        if (productData != null && productData.productImages.contains(image)
        ) {
            val deleteImageList = mutableListOf<String>()
            deleteImageList.add(image)
            deleteImageList.addAll(deleteImages.value)
            deleteImages.value = deleteImageList
        }
        images.value = images.value.filter { it -> it != image }
    }

    //this for api product product variant not for create this only when product is update
    fun removeProductVariant(value: ProductVarientSelection) {
        if (productData != null && !productData.productVariants.isNullOrEmpty() &&
            productData.productVariants.toListOfProductVarient()
                .contains(value)
        ) {
            val deletedProductVariant =
                mutableListOf<ProductVarientSelection>()
            deletedProductVariant.add(value)
            deletedProductVariant.addAll(deleteProductVariant.value)
            deleteProductVariant.value = deletedProductVariant
        }
        productVariants.value =
            productVariants.value.filter { it.name != value.name }
    }

    fun updateCurrentCurrencySymbol(currencySymbol: String) {
        productCurrency.value = currencySymbol
        updateConditionValue(isExpandedCurrencyValue = false)
    }

    fun updateSubCategory(id: UUID) {
        updateConditionValue(isExpandedSubCategoryValue = false)
        selectedSubCategoryId.value = id
    }

    fun updateSelectedVariant(id: UUID) {
        updateConditionValue(isExpandedVariantValue = false)
        selectedVariantId.value = id
    }

    fun addProductVariant() {
        val selectedVariant = ProductVarientSelection(
            name = productVariantName.value.text,
            percentage = if (productVariantPercentage.value.text.isEmpty()) 1.0 else productVariantPercentage.value.text.toDouble(),
            variantId = selectedVariantId.value!!
        )

        val productVariantHolder = mutableListOf<ProductVarientSelection>()
        productVariantHolder.addAll(productVariants.value)
        productVariantHolder.add(selectedVariant)
        productVariants.value = productVariantHolder

        productVariantName.value = TextFieldValue("")
        productVariantPercentage.value = TextFieldValue("")
        selectedVariantId.value = null
    }

    fun createProduct() {
        val validationResult = validateInput()
        if (validationResult) {
            coroutine.launch {
                updateConditionValue(isSendingDataValue = true)
                val result = async {
                    productViewModel.createProducts(
                        name = productName.value.text,
                        description = description.value.text,
                        thumbnail = File(thumbnail.value!!),
                        subcategoryId = selectedSubCategoryId.value!!,
                        storeId = storeIdHolder!!,
                        price = price.value.text.toDouble(),
                        symbol = productCurrency.value!!,
                        productVariants = productVariants.value,
                        images = images.value.map { it -> File(it) },
                    )
                }.await()
                updateConditionValue(isSendingDataValue = false)
                if (!result.isNullOrEmpty()) {
                    snackBarHostState.showSnackbar(result)
                } else {
                    thumbnail.value = null
                    images.value = emptyList()
                    productCurrency.value = ""
                    productName.value = TextFieldValue("")
                    price.value = TextFieldValue("")
                    description.value = TextFieldValue("")
                    selectedSubCategoryId.value = null
                    productVariants.value = emptyList()
                    snackBarHostState.showSnackbar(context.getString(R.string.product_created_successfully))
                    nav.popBackStack()
                }

            }
        }

    }

    fun updateProduct() {
        val newProductVariant = mutableListOf<ProductVarientSelection>()
        if (productVariants.value.isNotEmpty()) {
            newProductVariant.addAll(productVariants.value)
        }
        if (newProductVariant.isNotEmpty() && (productData != null && !productData.productVariants.isNullOrEmpty())) {
            newProductVariant.removeAll(productData.productVariants.toListOfProductVarient())
        }

        val newImages = mutableListOf<String>()
        if (images.value.isNotEmpty()) {
            newImages.addAll(images.value)
        }
        if (newImages.isNotEmpty() && (productData != null && productData.productImages.isNotEmpty())) {
            newImages.removeAll(productData.productImages)
        }


        coroutine.launch {
            updateConditionValue(isSendingDataValue = true)
            val result = async {
                productViewModel.updateProducts(
                    id = productIdHolder!!,
                    name = productName.value.text.ifEmpty { null },
                    description = description.value.text.ifEmpty { null },
                    thumbnail = if (thumbnail.value != productData?.thumbnail) File(
                        thumbnail.value!!
                    ) else null,
                    subcategoryId = if (selectedSubCategoryId.value == null) null else selectedSubCategoryId.value!!,
                    storeId = storeIdHolder!!,
                    price = if (price.value.text.isEmpty()) null
                    else if (Validation.isValidMoney(price.value.text)) price.value.text.toDouble()
                    else null,
                    symbol = productCurrency.value,
                    productVariants = if (newProductVariant.isEmpty()) null
                    else newProductVariant,
                    images = if (newImages.isEmpty()) null else newImages.map { it ->
                        File(
                            it
                        )
                    }.toList(),
                    deletedImages = deleteImages.value.ifEmpty { null },
                    deletedProductVariants = deleteProductVariant.value.ifEmpty { null }
                )
            }.await()
            updateConditionValue(isSendingDataValue = false)

            if (!result.isNullOrEmpty()) {
                snackBarHostState.showSnackbar(result)
            } else {
                thumbnail.value = null
                images.value = emptyList()
                productName.value = TextFieldValue("")
                price.value = TextFieldValue("")
                description.value = TextFieldValue("")
                selectedSubCategoryId.value = null
                productVariants.value = emptyList()
                snackBarHostState.showSnackbar(context.getString(R.string.product_update_successfully))
                nav.popBackStack()
            }
        }
    }

    fun savedProduct() {
        when (productId) {
            null -> {
                createProduct()
            }

            else -> {
                updateProduct()
            }
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
                title = if (productId == null) stringResource(R.string.create_product) else stringResource(
                    R.string.update_product
                ),
                nav = nav,

                )

        },

        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                CustomButton(
                    isLoading = isSendingData.value,
                    operation = { savedProduct() },
                    buttonTitle = if (productIdHolder != null) context.getString(R.string.update_product)
                    else context.getString(R.string.create_product),
                    isEnable = true,
                )
            }
        }
    ) { scaffoldStatus ->
        scaffoldStatus.calculateTopPadding()
        scaffoldStatus.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(scaffoldStatus)
                .padding(horizontal = 15.dp)
                .padding(top = scaffoldStatus.calculateTopPadding() + 30.dp)
                .verticalScroll(rememberScrollState())
        )
        {

            Text(
                stringResource(R.string.product_thumbnail),
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(15)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                val (imageRef, cameralRef) = createRefs()
                Box(
                    modifier = Modifier
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .height(150.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color.White,
                        ),
                    contentAlignment = Alignment.Center
                )
                {
                    when (thumbnail.value == null) {
                        true -> {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                "",
                                modifier = Modifier.size(80.dp),
                                tint = CustomColor.neutralColor200
                            )
                        }

                        else -> {
                            SubcomposeAsyncImage(
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
//                                                .padding(top = 35.dp)
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                model = General.handlingImageForCoil(
                                    thumbnail.value,
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
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 10.dp)
                        .constrainAs(cameralRef) {
                            end.linkTo(imageRef.end)
                            bottom.linkTo(imageRef.bottom)
                        }


                )
                {

                    IconButton(
                        onClick = {
//                          keyboardController?.hide()
//                          isPigImage.value = true
                            onImageSelection.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .size(30.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = CustomColor.primaryColor200
                        )
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.camera),
                            "",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }

            }
            Sizer(10)
            Text(
                stringResource(R.string.product_images),
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(5)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                val (cameralRef) = createRefs()

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CustomColor.neutralColor500,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(5.dp)

                ) {
                    if (images.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                "",
                                modifier = Modifier.size(80.dp),
                                tint = CustomColor.neutralColor200
                            )
                        }
                    }

                    images.value.forEach { value ->

                        ConstraintLayout {
                            Box(
                                modifier = Modifier

                                    .height(120.dp)
                                    .width(120.dp)
                                    .background(
                                        color = Color.White,
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)),
                                    model = General.handlingImageForCoil(
                                        value,
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

                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp)
                                    .background(
                                        Color.Red,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        removeProductImages(value)
                                    },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Icon(
                                    Icons.Default.Clear, "",
                                    tint = Color.White
                                )
                            }
                        }

                    }
                }


                Box(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 10.dp)
                        .constrainAs(cameralRef) {
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                )
                {

                    IconButton(
                        onClick = {
                            selectMultipleImages.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier
                            .size(30.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = CustomColor.primaryColor200
                        )
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.camera),
                            "",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }

            }

            Sizer(15)

            TextInputWithTitle(
                value = productName,
                title = stringResource(R.string.name),
                placeHolder = productData?.name ?: stringResource(R.string.product_name),
                fontTitle = (18).sp,
                fontWeight = FontWeight.Bold,
            )

            TextNumberInputWithTitle(
                value = price,
                title = stringResource(R.string.price),
                placeHolder = stringResource(R.string.product_price),
                errorMessage = "",
                fontTitle = (18).sp,
                fontWeight = FontWeight.Bold,
            )
            TextInputWithTitle(
                value = description,
                title = stringResource(R.string.description),
                placeHolder = stringResource(R.string.product_price),
                errorMessage = "",
                fontTitle = (18).sp,
                fontWeight = FontWeight.Bold,
                maxLines = 6
            )

            Text(
                "Currency",
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CustomColor.neutralColor400,
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            )
            {
                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()

                        .clickable {
                            updateConditionValue(isExpandedCurrencyValue = !isExpandedCurrency.value)
                        }
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        productData?.symbol ?: (productCurrency.value ?: "Chose Currency")
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, "",
                        modifier = Modifier.rotate(currencyRotation.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(currencyAnimated.value)
                        .border(
                            1.dp,
                            CustomColor.neutralColor200,
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),

                    )
                {
                    currencies.value?.forEach { value ->
                        Text(
                            value.name,
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    updateCurrentCurrencySymbol(value.symbol)
                                }
                                .padding(top = 12.dp, start = 5.dp)

                        )
                    }
                }
            }


            Sizer(15)

            Text(
                stringResource(R.string.subcategory),
                fontFamily = General.satoshiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (18).sp,
                color = CustomColor.neutralColor950,
                textAlign = TextAlign.Center,
            )
            Sizer(5)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CustomColor.neutralColor400,
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            )
            {

                Row(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()

                        .clickable {
                            updateConditionValue(isExpandedSubCategoryValue = !isExpandedSubCategory.value)
                        }
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        if (productData != null && selectedSubCategoryId.value == null)
                            storeSubCategory?.firstOrNull { it.id == productData.subcategoryId }?.name
                                ?: stringResource(R.string.select_subcategory)
                        else if (selectedSubCategoryId.value == null) stringResource(R.string.select_subcategory)
                        else storeSubCategory?.firstOrNull { it.id == selectedSubCategoryId.value }?.name
                            ?: ""
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown, "",
                        modifier = Modifier.rotate(rotation.value)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animated.value)
                        .border(
                            1.dp,
                            CustomColor.neutralColor200,
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),

                    )
                {
                    storeSubCategory?.forEach { value ->
                        Text(
                            value.name,
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    updateSubCategory(value.id)
                                }
                                .padding(top = 12.dp, start = 5.dp)

                        )
                    }
                }
            }

            Sizer(5)

            if (productVariants.value.isNotEmpty()) {
                Sizer(5)
                Text(
                    stringResource(R.string.product_variant),
                    fontFamily = General.satoshiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (18).sp,
                    color = CustomColor.neutralColor950,
                    textAlign = TextAlign.Center,
                )
                Sizer(2)
                if (productVariants.value.isNotEmpty())
                    Sizer(5)
                FlowRow {
                    productVariants.value.forEach { value ->
                        ConstraintLayout(
                            modifier = Modifier.padding(end = 5.dp, bottom = 10.dp)
                        ) {
                            val (iconRef) = createRefs()
                            Column(
                                modifier = Modifier
                                    .background(
                                        CustomColor.alertColor_3_300,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(
                                        end = 25.dp,
                                        start = 5.dp
                                    )
                            ) {
                                Text(
                                    variants.value?.firstOrNull { it.id == value.variantId }?.name
                                        ?: "",
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    value.name,
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(20.dp)
                                    .background(
                                        Color.Red,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { removeProductVariant(value) }
                                    .constrainAs(iconRef) {
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Clear, "",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
                if (productVariants.value.isNotEmpty())
                    Sizer(5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            CustomColor.neutralColor400,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                )
                {

                    Row(
                        modifier = Modifier
                            .height(65.dp)
                            .fillMaxWidth()
                            .clickable {
                                updateConditionValue(isExpandedVariantValue = !isExpandedVariant.value)
                            }
                            .padding(horizontal = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            if (selectedVariantId.value == null) stringResource(R.string.select_variant)
                            else variants.value?.firstOrNull { it.id == selectedVariantId.value }?.name
                                ?: ""
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown, "",
                            modifier = Modifier.rotate(rotationVariant.value)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedVariant.value)
                            .border(
                                1.dp,
                                CustomColor.neutralColor200,
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp,
                                    bottomStart = 8.dp,
                                    bottomEnd = 8.dp
                                )
                            ),

                        ) {
                        if (!variants.value.isNullOrEmpty())
                            variants.value!!.forEach { value ->
                                Text(
                                    value.name,
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            updateSelectedVariant(value.id)
                                        }
                                        .padding(top = 12.dp, start = 5.dp)

                                )
                            }
                    }
                }
                Sizer(5)
                OutlinedTextField(
                    maxLines = 6,
                    value = productVariantName.value,
                    onValueChange = { productVariantName.value = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.variant_name),
                            color = CustomColor.neutralColor500,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black
                    ),
                    textStyle = TextStyle(
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp,
                        color = CustomColor.neutralColor950
                    ),
                    trailingIcon = {
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Sizer(5)

                OutlinedTextField(

                    maxLines = 6,
                    value = productVariantPercentage.value,
                    onValueChange = { productVariantPercentage.value = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.variant_price),
                            color = CustomColor.neutralColor500,
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = (16).sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black
                    ),
                    textStyle = TextStyle(
                        fontFamily = General.satoshiFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = (16).sp,
                        color = CustomColor.neutralColor950
                    ),
                    trailingIcon = {
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Sizer(5)

                CustomButton(
                    isLoading = false,
                    operation = { addProductVariant() },
                    buttonTitle = stringResource(R.string.add_productvariant),
                    isEnable = selectedVariantId.value != null && productVariantName.value.text.isNotEmpty(),
                    color = null
                )
            }

            Box(modifier = Modifier.height(140.dp))
        }
    }
}
