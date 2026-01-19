package com.example.eccomerce_app.ui.view.account.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.e_commercompose.R
import com.example.eccomerce_app.util.General
import com.example.e_commercompose.dto.ModelToDto.toListOfProductVarient
import com.example.e_commercompose.model.ProductVarientSelection
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.CustomDropDownComponent
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.eccomerce_app.ui.component.TextNumberInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.component.CreateProductImage
import com.example.eccomerce_app.ui.component.ProductVariantCreateComponent
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
        mutableStateOf(if (productData != null && !productData.productVariants.isNullOrEmpty()) productData.productVariants.toListOfProductVarient()
            else emptyList()
        )
    }
    val deleteImages = remember { mutableStateOf<List<String>>(emptyList()) }
    val deleteProductVariant = remember { mutableStateOf<List<ProductVarientSelection>>(emptyList()) }


    val productName = remember { mutableStateOf(TextFieldValue("")) }
    val description = remember { mutableStateOf(TextFieldValue("")) }
    val price = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantName = remember { mutableStateOf(TextFieldValue("")) }
    val productVariantPercentage = remember { mutableStateOf(TextFieldValue("")) }


    val productCurrency = remember { mutableStateOf<String?>(null) }


    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val selectedVariantId = remember { mutableStateOf<UUID?>(null) }


    val isSendingData = remember { mutableStateOf(false) }


    val snackBarHostState = remember { SnackbarHostState() }


    fun updateConditionValue(isSendingDataValue: Boolean) {
          isSendingData.value = isSendingDataValue
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
    fun removeProductImages(deletedImage: List<String>) {
        if(thumbnail.value==deletedImage[0]){
            deleteImages.value +=deletedImage[0]
        }
        else {
            val removedDeletedImages = images.value.filter { !deletedImage.contains(it) }
            images.value = removedDeletedImages
        }
    }

    //this for api product product variant not for create this only when product is update
    fun removeProductVariant(value: ProductVarientSelection) {
        if (productData != null && !productData.productVariants.isNullOrEmpty() &&
            productData.productVariants.toListOfProductVarient().firstOrNull{it==value}!=null
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
    }

    fun updateSubCategory(id: UUID) { selectedSubCategoryId.value = id }

    fun updateSelectedVariant(id: UUID) { selectedVariantId.value = id }

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
                        price = price.value.text.toInt(),
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
                    else if (Validation.isValidMoney(price.value.text)) price.value.text.toInt()
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
    )
    { scaffoldStatus ->
        scaffoldStatus.calculateTopPadding()
        scaffoldStatus.calculateBottomPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(scaffoldStatus)
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState())
        )
        {

            CreateProductImage(
                thumbnail = thumbnail.value,
                images = images.value,
                context = context,
                deleteImages = deleteImages.value,
                onSelectThumbnail = {value->thumbnail.value=value},
                onSelectImages = {value-> images.value += value },
                onRemoveAlreadyProductImage = {value-> removeProductImages(value)}
            )
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
            CustomDropDownComponent(
                value =  productData?.symbol ?: (productCurrency.value ?: "Chose Currency"),
                items =  currencies.value?.map { it.name }?:emptyList(),
                onSelectValue = { value ->
                    updateCurrentCurrencySymbol(value)
                }
            )
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
            CustomDropDownComponent(
               value = if (productData != null && selectedSubCategoryId.value == null)
                   storeSubCategory?.firstOrNull { it.id == productData.subcategoryId }?.name
                       ?: stringResource(R.string.select_subcategory)
               else if (selectedSubCategoryId.value == null) stringResource(R.string.select_subcategory)
               else storeSubCategory?.firstOrNull { it.id == selectedSubCategoryId.value }?.name
                   ?: "",
                items = storeSubCategory?.map { it.name },
                onSelectValue = { value ->
                    updateSubCategory(storeSubCategory?.firstOrNull { it.name == value }?.id ?: UUID.randomUUID())
                }
            )


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

                ProductVariantCreateComponent(
                   productVariants =  productVariants.value,
                   variants = variants.value,
                onRemoveProductVariant =  { value->removeProductVariant(value)}
                )
                 if (productVariants.value.isNotEmpty())
                    Sizer(5)

                CustomDropDownComponent(
                    value = if (selectedVariantId.value == null) stringResource(R.string.select_variant)
                    else variants.value?.firstOrNull { it.id == selectedVariantId.value }?.name
                        ?: "",
                    items = variants.value?.map { it.name },
                    onSelectValue = { value ->
                        updateSelectedVariant(variants.value?.firstOrNull { it.name == value }?.id ?: UUID.randomUUID())
                    }
                )

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
