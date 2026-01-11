package com.example.eccomerce_app.ui.view.account.store

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.example.e_commercompose.R
import com.example.e_commercompose.model.Category
import com.example.e_commercompose.model.SubCategory
import com.example.e_commercompose.model.SubCategoryUpdate
import com.example.e_commercompose.model.enMapType
import com.example.eccomerce_app.ui.component.BannerBage
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.ProductLoading
import com.example.eccomerce_app.ui.component.ProductShape
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.ui.component.SharedAppBar
import com.example.eccomerce_app.util.General
import com.example.eccomerce_app.util.General.reachedBottom
import com.example.eccomerce_app.util.General.toCustomFil
import com.example.eccomerce_app.util.General.toCustomString
import com.example.eccomerce_app.viewModel.BannerViewModel
import com.example.eccomerce_app.viewModel.CategoryViewModel
import com.example.eccomerce_app.viewModel.ProductViewModel
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.SubCategoryViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.vsnappy1.datepicker.DatePicker
import com.example.eccomerce_app.ui.component.datePicker.vsnappy1.timepicker.TimePicker
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.io.File
import java.util.UUID

enum class EnOperation { STORE }

enum class EnBottomSheetType { SupCategory, Banner }
enum class EnDateTimeType { Date, Time }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    nav: NavHostController,
    copyStoreId: String?,
    isFromHome: Boolean?,
    bannerViewModel: BannerViewModel,
    categoryViewModel: CategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel,
    userViewModel: UserViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current


    val coroutine = rememberCoroutineScope()
    val state = rememberPullToRefreshState()
    val sheetState = rememberModalBottomSheetState()
    val dateTimeSheetState = rememberModalBottomSheetState()
    val lazyState = rememberLazyListState()


    val createdStoreInfoHolder = storeViewModel.storeCreateData.collectAsState()
    val myInfo = userViewModel.userInfo.collectAsState()
    val categories = categoryViewModel.categories.collectAsState()
    val banners = bannerViewModel.banners.collectAsState()
    val subcategories = subCategoryViewModel.subCategories.collectAsState()
    val products = productViewModel.products.collectAsState()


    val operationType = remember { mutableStateOf<EnOperation?>(null) }
    val bottomSheetType = remember { mutableStateOf<EnBottomSheetType?>(null) }
    val bottomSheetDateTimeType = remember { mutableStateOf<EnDateTimeType?>(null) }


    val bannerEndDateTime = remember { mutableStateOf<LocalDateTime?>(null) }

    val selectedSubCategoryId = remember { mutableStateOf<UUID?>(null) }
    val selectedSubCategoryIdHolder = remember { mutableStateOf<UUID?>(null) }

    val bannerImage = remember { mutableStateOf<File?>(null) }

    val snackBarHostState = remember { SnackbarHostState() }

    val errorMessage = remember { mutableStateOf("") }

    val page = remember { mutableIntStateOf(1) }

    //this to recognize if there any update data store is update
    val isStoreUpdateData = storeViewModel.isUpdate.collectAsState()

    val isLoadingMore = remember { mutableStateOf(false) }
    val isRefresh = remember { mutableStateOf(false) }
    val isUpdated = remember { mutableStateOf(false) }
    val isDeleted = remember { mutableStateOf(false) }
    val isDateTimeBottomSheetOpen = remember { mutableStateOf(false) }
    val isOpenBottomSheet = remember { mutableStateOf(false) }
    val isChangeSubCategory = remember { mutableStateOf(false) }
    val isExpandedCategory = remember { mutableStateOf(false) }
    val isShownDateDialog = remember { mutableStateOf(false) }
    val isPigImage = remember { mutableStateOf<Boolean?>(null) }
    val isSendingData = remember { mutableStateOf(false) }

    val reachedBottom = remember { derivedStateOf { lazyState.reachedBottom() } }


    val storeId = remember {
        mutableStateOf<UUID?>(
            if (copyStoreId == null) null else UUID.fromString(copyStoreId)
        )
    }


    val myStoreId = myInfo.value?.storeId
    val stores = storeViewModel.stores.collectAsState()
    val storeData = stores.value?.firstOrNull { it.id == (storeId.value ?: myStoreId) }
    val storeBanners = banners.value?.filter { it.storeId == storeId.value }
    val storeSubCategories =
        subcategories.value?.filter { it.storeId == (storeId.value ?: myStoreId) }
    val storeProduct =
        if (products.value != null && myStoreId != null) products.value!!.filter { it.storeId == (myStoreId) }
        else emptyList()
    val productFilterBySubCategory = if (selectedSubCategoryId.value == null) storeProduct
    else storeProduct.filter { it.subcategoryId == selectedSubCategoryId.value }


    //animation
    val animated = animateDpAsState(
        if (isExpandedCategory.value) ((categories.value?.size ?: 1) * 35).dp else 0.dp
    )

    val rotation = animateFloatAsState(
        if (isExpandedCategory.value) 180f else 0f
    )

    //text filed
    val storeName = remember {
        mutableStateOf(
            TextFieldValue(
                createdStoreInfoHolder.value?.name ?: ""
            )
        )
    }
    val categoryName = remember { mutableStateOf(TextFieldValue("")) }
    val subCategoryName = remember { mutableStateOf(TextFieldValue("")) }


    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    fun handlingLocation(mapType: enMapType, currentLocation: Location): LatLng? {
        return when {
            mapType == enMapType.MyStore || isFromHome == true ->
                if (storeData == null) null
                else
                    LatLng(storeData.latitude, storeData.longitude)

            else -> {
                LatLng(currentLocation.latitude, currentLocation.longitude)

            }
        }
    }

    val requestPermissionThenNavigate = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { permissions ->
            val arePermissionsGranted = permissions.values.reduce { acc, next -> acc && next }

            if (arePermissionsGranted) {

                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return@rememberLauncherForActivityResult
                } else locationClient.lastLocation.apply {
                    addOnSuccessListener { location ->
                        location?.toString()
                        if (location != null) {
                            val type =
                                when ((myStoreId == storeId.value || myStoreId == null) && isFromHome == false) {
                                    true -> enMapType.MyStore
                                    else -> enMapType.Store
                                }

                            val locationHolder = handlingLocation(type, location)

                            nav.navigate(
                                Screens.MapScreen(
                                    lognit = locationHolder?.longitude,
                                    latitt = locationHolder?.latitude,
                                    additionLat = if (type == enMapType.Store) location.latitude else null,
                                    additionLong = if (type == enMapType.Store) location.longitude else null,
                                    isFromLogin = false,
                                    title = null,
                                    mapType = type,
                                )
                            )
                        } else coroutine.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.you_should_enable_location_services))
                        }
                    }
                    addOnFailureListener { fail ->
                        Log.d(
                            "contextError", "the current location is null ${fail.stackTrace}"
                        )

                    }
                }


                // Got last known location. In some srare situations this can be null.
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    fun updateConditionValue(
        isLoadingMoreValue: Boolean? = null,
        isRefreshValue: Boolean? = null,
        isUpdatedValue: Boolean? = null,
        isDeletedValue: Boolean? = null,
        isDateTimeBottomSheetOpenValue: Boolean? = null,
        isOpenBottomSheetValue: Boolean? = null,
        isChangeSubCategoryValue: Boolean? = null,
        isExpandedCategoryValue: Boolean? = null,
        isShownDateDialogValue: Boolean? = null,
        isSendingDataValue: Boolean? = null,
        isPigImageValue: Boolean? = null,
        resetIsPigImage: Boolean = false
    ) {
        if (isLoadingMoreValue != null) isLoadingMore.value = isLoadingMoreValue
        if (isRefreshValue != null) isRefresh.value = isRefreshValue
        if (isUpdatedValue != null) isUpdated.value = isUpdatedValue
        if (isDeletedValue != null) isDeleted.value = isDeletedValue
        if (isDateTimeBottomSheetOpenValue != null) isDateTimeBottomSheetOpen.value =
            isDateTimeBottomSheetOpenValue
        if (isOpenBottomSheetValue != null) isOpenBottomSheet.value = isOpenBottomSheetValue
        if (isChangeSubCategoryValue != null) isChangeSubCategory.value = isChangeSubCategoryValue
        if (isExpandedCategoryValue != null) isExpandedCategory.value = isExpandedCategoryValue
        if (isShownDateDialogValue != null) isShownDateDialog.value = isShownDateDialogValue
        if (isSendingDataValue != null) isSendingData.value = isSendingDataValue

        if (resetIsPigImage) {
            isPigImage.value = null
        } else if (isPigImageValue != null) {
            isPigImage.value = isPigImageValue
        }
    }


    val onImageSelection = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            val fileHolder = uri.toCustomFil(context = context)
            if (fileHolder != null) {
                when (isPigImage.value == null || bottomSheetType.value == EnBottomSheetType.Banner) {
                    true -> {
                        bannerImage.value = fileHolder
                        updateConditionValue(isShownDateDialogValue = true)
                    }

                    else -> {
                        when (isPigImage.value) {
                            true -> {
                                storeViewModel.setStoreCreateData(
                                    wallpaperImage = fileHolder,
                                    storeId = storeId.value
                                )
                            }

                            else -> {
                                storeViewModel.setStoreCreateData(
                                    smallImage = fileHolder,
                                    storeId = storeId.value
                                )
                            }
                        }
                    }
                }
                updateConditionValue(resetIsPigImage = true)
            }
        }
    }


    fun creationValidation(): Boolean {
        keyboardController?.hide()
        var errorMessage = ""
        if (createdStoreInfoHolder.value?.wallpaperImage == null) errorMessage =
            context.getString(R.string.you_must_select_the_wallpaper_image)
        else if (createdStoreInfoHolder.value?.smallImage == null) errorMessage =
            context.getString(R.string.you_must_select_the_small_image)
        else if (createdStoreInfoHolder.value?.name.isNullOrEmpty()) errorMessage =
            context.getString(R.string.you_must_write_the_store_name)
        else if (createdStoreInfoHolder.value?.latitude == null) errorMessage =
            context.getString(R.string.you_must_select_the_store_location)

        if (errorMessage.trim().isNotEmpty()) {
            coroutine.launch {
                snackBarHostState.showSnackbar(errorMessage)
            }
            return false
        }
        return true

    }


    fun getStoreInfoByStoreId(
        id: UUID,
        isLoading: Boolean?=null,
        updateLoadingState:((value: Boolean)->Unit)?  = null
    ) {
        
        storeViewModel.getStoreData(storeId = id)
        bannerViewModel.getStoreBanner(id)
        subCategoryViewModel.getStoreSubCategories(id, 1)
        productViewModel.getProducts(
            1, id, isLoading?:false,
            updatePageNumber = {value->page.intValue=value},
            updateLoadingState = {value->updateLoadingState?.invoke(value)}
        )
    }


    fun createOrUpdateStoreInfo() {

        if (myStoreId == null && !creationValidation()) {
            return;
        }

        keyboardController?.hide()
        updateConditionValue(isSendingDataValue = true)
        operationType.value = EnOperation.STORE
        coroutine.launch {
            val result = async {
                if (myInfo.value?.storeId != null) storeViewModel.updateStore(
                    name = storeName.value.text,
                    wallpaperImage = createdStoreInfoHolder.value?.wallpaperImage,
                    smallImage = createdStoreInfoHolder.value?.smallImage,
                    longitude = createdStoreInfoHolder.value?.longitude,
                    latitude = createdStoreInfoHolder.value?.latitude,
                )
                else storeViewModel.createStore(
                    name = createdStoreInfoHolder.value?.name ?: storeName.value.text,
                    wallpaperImage = createdStoreInfoHolder.value!!.wallpaperImage!!,
                    smallImage = createdStoreInfoHolder.value!!.smallImage!!,
                    longitude = createdStoreInfoHolder.value!!.longitude!!,
                    latitude = createdStoreInfoHolder.value!!.latitude!!,
                    sumAdditionalFun = { id ->
                        userViewModel.updateMyStoreId(
                            id
                        )
                        storeId.value = id
                        getStoreInfoByStoreId(id)
                    })
            }.await()

            updateConditionValue(isSendingDataValue = false)
            operationType.value = null

            if (result != null) {
                snackBarHostState.showSnackbar(result)
            } else {
                storeName.value = TextFieldValue("")

            }
        }

    }


    fun createOrUpdateSupCategory() {
        coroutine.launch {
            keyboardController?.hide()
            updateConditionValue(isSendingDataValue = true, isOpenBottomSheetValue = false)

            val result = async {
                if (isUpdated.value) subCategoryViewModel.updateSubCategory(
                    SubCategoryUpdate(
                        name = subCategoryName.value.text,
                        cateogyId = categories.value!!.firstOrNull() { it.name == categoryName.value.text }!!.id,
                        id = selectedSubCategoryId.value!!
                    )
                )
                else subCategoryViewModel.createSubCategory(
                    name = subCategoryName.value.text,
                    categoryId = categories.value!!.firstOrNull() { it.name == categoryName.value.text }!!.id
                )
            }.await()
            updateConditionValue(isSendingDataValue = false, isExpandedCategoryValue = false)

            if (result.isNullOrEmpty()) {
                categoryName.value = TextFieldValue("")
                subCategoryName.value = TextFieldValue("")
                if (isUpdated.value) {
                    updateConditionValue(isUpdatedValue = false)
                }
            } else {
                snackBarHostState.showSnackbar(result)
            }

        }

    }

    fun deleteSupCategory() {
        coroutine.launch {
            updateConditionValue(isSendingDataValue = true, isDeletedValue = true)
            keyboardController?.hide()
            val result = async {
                subCategoryViewModel.deleteSubCategory(
                    id = selectedSubCategoryIdHolder.value!!
                )
            }.await()
            updateConditionValue(isSendingDataValue = false, isDeletedValue = false)
            if (result.isNullOrEmpty()) {
                updateConditionValue(
                    isOpenBottomSheetValue = false,
                    isExpandedCategoryValue = false
                )
                selectedSubCategoryIdHolder.value = null
                categoryName.value = TextFieldValue("")
                subCategoryName.value = TextFieldValue("")
                updateConditionValue(isUpdatedValue = false)
                selectedSubCategoryId.value = null
            } else {
                updateConditionValue(isOpenBottomSheetValue = true)
                errorMessage.value = result
            }

        }

    }

    fun createBanner() {
        coroutine.launch {
            updateConditionValue(isOpenBottomSheetValue = false, isSendingDataValue = true)
            val result = async {
                bannerViewModel.createBanner(
                    bannerEndDateTime.value.toString(),
                    bannerImage.value!!
                )
            }.await()
            isSendingData.value = false
            if (!result.isNullOrEmpty()) {
                snackBarHostState.showSnackbar(result)
                return@launch
            }
            bannerEndDateTime.value = null
            bannerEndDateTime.value = null
        }
    }

    fun openBottonSheetAndUpdateType(supCategory: EnBottomSheetType) {
        bottomSheetType.value = supCategory
        updateConditionValue(isOpenBottomSheetValue = true)

    }

    fun updateDateTime(year: Int, month: Int, day: Int) {
        if (bannerEndDateTime.value != null)
            bannerEndDateTime.value = LocalDateTime(
                year = year,
                month = month + 1,
                day = day,
                hour = bannerEndDateTime.value!!.hour,
                second = bannerEndDateTime.value!!.second,
                minute = 0
            )
        else {
            bannerEndDateTime.value = LocalDateTime(
                year = year,
                month = month + 1,
                day = day,
                hour = 0,
                second = 0,
                minute = 0
            )
        }
        updateConditionValue(isDateTimeBottomSheetOpenValue = false)
    }

    fun updateDateTime(hour: Int, second: Int) {
        if (bannerEndDateTime.value != null)
            bannerEndDateTime.value = LocalDateTime(
                year = bannerEndDateTime.value!!.year,
                month = bannerEndDateTime.value!!.month,
                day = bannerEndDateTime.value!!.day,
                hour = hour,
                second = second,
                minute = 1
            )
        else {
            if (hour > 0) {
                bannerEndDateTime.value = LocalDateTime(
                    year = 1,
                    month = 1,
                    day = 1,
                    hour = hour,
                    second = second,
                    minute = 1
                )
            }
        }

        updateConditionValue(isDateTimeBottomSheetOpenValue = false)

    }

    fun dismissBanner() {
        bannerEndDateTime.value = null
        bannerImage.value = null
    }

    fun dismissSubCategory() {

        updateConditionValue(isExpandedCategoryValue = false)
        categoryName.value = TextFieldValue("")
        subCategoryName.value = TextFieldValue("")
    }

    fun dismissBottonSheet() {
        updateConditionValue(isOpenBottomSheetValue = false)
        when (bottomSheetType.value) {
            EnBottomSheetType.Banner -> {
                dismissBanner()
            }

            EnBottomSheetType.SupCategory -> {
                dismissSubCategory()
            }

            else -> {

            }
        }
    }

    fun updateBottonSheetTimeType(type: EnDateTimeType) {
        keyboardController?.hide()
        bottomSheetDateTimeType.value = type
        updateConditionValue(isDateTimeBottomSheetOpenValue = true)
    }

    fun selectCategory(value: String) {
        updateConditionValue(isExpandedCategoryValue = false)
        categoryName.value =
            TextFieldValue(value)

    }

    fun onRefreshDo() {
        coroutine.launch {
            updateConditionValue(isRefreshValue = true)
            page.intValue = 1;
            delay(100)
            getStoreInfoByStoreId(storeId.value ?: UUID.randomUUID(), ){
                value->isRefresh.value=value
            }
        }
    }


    fun onBigPictureSelect() {
        keyboardController?.hide()
        updateConditionValue(isPigImageValue = true)
        onImageSelection.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    fun onDeleteBanner(id: UUID) {
        updateConditionValue(isSendingDataValue = true)
        coroutine.launch {
            val result = async {
                bannerViewModel.deleteBanner(id)
            }.await()

            updateConditionValue(isSendingDataValue = false)
            var errorMessage = ""
            errorMessage = if (result.isNullOrEmpty()) {
                context.getString(R.string.banner_deleted_successfully)
            } else {
                result
            }
            snackBarHostState.showSnackbar(errorMessage)
        }
    }

    fun requestPermissionLocation() {
        keyboardController?.hide()
        requestPermissionThenNavigate.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    }

    fun updateSelectedSubCategory(id: UUID) {
        coroutine.launch {
            if (selectedSubCategoryId.value != id) {
                isChangeSubCategory.value = true

                selectedSubCategoryId.value = id
                updateConditionValue(isChangeSubCategoryValue = false)
            }

        }
    }

    fun openSubCategoryBottonSheet(subCategory: SubCategory) {
        if (isFromHome == false) {

            val name =
                categories.value?.firstOrNull { it.id == subCategory.categoryId }?.name
                    ?: ""

            selectedSubCategoryIdHolder.value = subCategory.id
            categoryName.value = TextFieldValue(name)
            subCategoryName.value = TextFieldValue(subCategory.name)
            isUpdated.value = true
            updateConditionValue(isUpdatedValue = true)
            updateConditionValue(isOpenBottomSheetValue = true)

        }
    }

    fun deleteProduct(id: UUID) {
        coroutine.launch {
            isSendingData.value = true
            updateConditionValue(isSendingDataValue = true)
            val result =
                productViewModel.deleteProduct(
                    storeId.value!!, id
                )

            updateConditionValue(isSendingDataValue = false)
            var resultMessage = ""
            resultMessage = result
                ?: context.getString(R.string.product_is_deleted_successfully)

            snackBarHostState.showSnackbar(
                resultMessage
            )
        }
    }
    LaunchedEffect(Unit) {
        getStoreInfoByStoreId(storeId.value ?: UUID.randomUUID())
    }

    LaunchedEffect(reachedBottom.value) {
        if (!products.value.isNullOrEmpty() && reachedBottom.value && products.value!!.size > 23) {
            when (selectedSubCategoryId.value == null) {
                true -> {
                    productViewModel.getProducts(
                        page.value,
                        storeId = storeId.value ?: UUID.randomUUID(),
                        isLoadingMore.value,
                        updatePageNumber = {value->page.intValue=value},
                        updateLoadingState = {value->isLoadingMore.value=value}
                    )
                }

                else -> {
                    productViewModel.getProducts(
                        page.intValue,
                        storeId = storeId.value!!,
                        selectedSubCategoryId.value!!,
                        isLoadingMore.value,
                        updatePageNumber = {value->page.intValue=value},
                        updateLoadingState = {value->isLoadingMore.value=value}
                    )
                }
            }

        }

    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState, modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        },

        bottomBar = {
            if (isDateTimeBottomSheetOpen.value)
                ModalBottomSheet(
                    onDismissRequest = { updateConditionValue(isDateTimeBottomSheetOpenValue = false) },
                    sheetState = dateTimeSheetState, containerColor = Color.White
                )
                {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {
                        when (bottomSheetDateTimeType.value) {
                            EnDateTimeType.Date -> {
                                DatePicker(
                                    onDateSelected = { year, month, day ->
                                        updateDateTime(year, month, day)
                                    },

                                    months = listOf(
                                        "يناير",
                                        "فبراير",
                                        "مارس",
                                        "أبريل",
                                        "مايو",
                                        "يونيو",
                                        "يوليو",
                                        "أغسطس",
                                        "سبتمبر",
                                        "أكتوبر",
                                        "نوفمبر",
                                        "ديسمبر"
                                    ),
                                    days = listOf(
                                        "الأحد",    // Sunday
                                        "الاثنين",  // Monday
                                        "الثلاثاء", // Tuesday
                                        "الأربعاء", // Wednesday
                                        "الخميس",   // Thursday
                                        "الجمعة",    // Friday
                                        "السبت"    // Saturday
                                    )
                                )
                            }

                            else -> {
                                TimePicker(
                                    onTimeSelected = { hour, second ->
                                        updateDateTime(hour, second)
                                    },
                                    amPmLocaleList = listOf("صباحا", "مساء")
                                )
                            }
                        }
                    }

                }

            if (isOpenBottomSheet.value)
                ModalBottomSheet(
                    onDismissRequest = { dismissBottonSheet() },
                    sheetState = sheetState,
                    containerColor = Color.White
                )
                {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {
                        when (bottomSheetType.value) {
                            EnBottomSheetType.Banner -> {
                                Text(
                                    stringResource(R.string.banner_image),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )

                                Sizer(20)

                                ConstraintLayout(
                                    modifier = Modifier
                                        .height(150.dp)
                                        .fillMaxWidth()
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
                                            .height(150.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (isFromHome == true) CustomColor.neutralColor100 else CustomColor.neutralColor500,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                color = if (isFromHome == true) CustomColor.primaryColor50
                                                else Color.White,
                                            ), contentAlignment = Alignment.Center) {
                                        when (bannerImage.value == null) {
                                            true -> {

                                            }

                                            else -> {
                                                SubcomposeAsyncImage(
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    model = General.handlingImageForCoil(
                                                        bannerImage.value!!.path.toString(), context
                                                    ),
                                                    contentDescription = "",
                                                    loading = {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
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


                                        IconButton(
                                            onClick = {
                                                keyboardController?.hide()
                                                onImageSelection.launch(
                                                    PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                                    )
                                                )
                                            },
                                            modifier = Modifier.size(30.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = CustomColor.primaryColor500
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

                                Sizer(20)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {

                                    Column {
                                        Text(
                                            stringResource(R.string.banner_end_date),
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (18).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                        )

                                        if (bannerEndDateTime.value != null)
                                            Text(
                                                bannerEndDateTime.value!!.toCustomString(),
                                                color = CustomColor.neutralColor500,
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp
                                            )

                                    }
                                    IconButton(
                                        onClick = {
                                            updateBottonSheetTimeType(EnDateTimeType.Date)
                                        })
                                    {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            "",
                                            modifier = Modifier.size(24.dp),
                                            tint = CustomColor.primaryColor700
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {

                                    Column {
                                        Text(
                                            stringResource(R.string.banner_end_time),
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (18).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                        )

                                        if (bannerEndDateTime.value != null)
                                            Text(
                                                bannerEndDateTime.value!!.toCustomString(true),
                                                color = CustomColor.neutralColor500,
                                                fontFamily = General.satoshiFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = (16).sp
                                            )

                                    }
                                    IconButton(
                                        onClick = {
                                            updateBottonSheetTimeType(EnDateTimeType.Time)

                                        })
                                    {
                                        Icon(
                                            Icons.Default.Timelapse,
                                            "",
                                            modifier = Modifier.size(24.dp),
                                            tint = CustomColor.primaryColor700
                                        )
                                    }
                                }
                                Sizer(10)
                                CustomButton(
                                    isEnable = bannerImage.value != null &&
                                            bannerEndDateTime.value != null &&
                                            bannerEndDateTime.value!!.year != 1 &&
                                            bannerEndDateTime.value!!.hour != 0,
                                    operation = { createBanner() },
                                    buttonTitle = stringResource(R.string.create_banner)
                                )
                            }

                            else -> {

                                Text(
                                    stringResource(R.string.category),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (16).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.End

                                )
                                Sizer(10)

                                //this custom drop down menu
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
                                                updateConditionValue(
                                                    isExpandedCategoryValue = !isExpandedCategory.value
                                                )
                                            }
                                            .clip(RoundedCornerShape(8.dp))
                                            .padding(horizontal = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically) {

                                        Text(
                                            categoryName.value.text.ifEmpty { stringResource(R.string.select_category_name) })
                                        Icon(
                                            Icons.Default.KeyboardArrowDown,
                                            "",
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

                                        ) {

                                        categories.value?.forEach { option: Category ->
                                            Text(
                                                option.name,
                                                modifier = Modifier
                                                    .height(50.dp)
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { selectCategory(option.name) }
                                                    .padding(top = 12.dp, start = 5.dp)

                                            )
                                        }
                                    }
                                }

                                Sizer(10)


                                TextInputWithTitle(
                                    value = subCategoryName,
                                    title = stringResource(R.string.name),
                                    placeHolder = stringResource(R.string.enter_sub_category_name),
                                )

                                CustomButton(
                                    operation = {
                                        createOrUpdateSupCategory()
                                    },
                                    buttonTitle = if (isUpdated.value) stringResource(R.string.update) else stringResource(
                                        R.string.create
                                    ),
                                    color = null,
                                    isEnable = !isDeleted.value &&
                                            (subCategoryName.value.text.isNotEmpty() &&
                                                    categoryName.value.text.isNotEmpty())
                                )

                                if (isUpdated.value) {
                                    Sizer(10)
                                    CustomButton(
                                        isLoading = isDeleted.value && isSendingData.value,
                                        operation = { deleteSupCategory() },
                                        buttonTitle = stringResource(R.string.deleted),
                                        color = CustomColor.alertColor_1_600,
                                        isEnable = !isSendingData.value
                                    )
                                }


                            }
                        }
                    }

                }


        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        topBar = {
            SharedAppBar(
                title =stringResource(R.string.store),
                nav = nav,
                action = {
                    if (isFromHome == false) {
                        TextButton(
                            enabled = !isSendingData.value,
                            onClick = { createOrUpdateStoreInfo() }) {
                            when (isSendingData.value && operationType.value == EnOperation.STORE) {
                                true -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp
                                    )
                                }

                                else -> {
                                    Text(
                                        when {
                                            isStoreUpdateData.value && myInfo.value?.storeId != null -> stringResource(
                                                R.string.update
                                            )

                                            isStoreUpdateData.value && myInfo.value?.storeId == null -> stringResource(
                                                R.string.create
                                            )

                                            else -> ""
                                        },
                                        fontFamily = General.satoshiFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (16).sp,
                                        color = CustomColor.primaryColor700,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }
                    }

                },
                scrollBehavior = scrollBehavior
            )

        },
        floatingActionButton = {
            if (isFromHome == false && storeData != null)

                Column {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .size(50.dp), onClick = {
                            nav.navigate(Screens.DeliveriesList)
                        }, containerColor = CustomColor.alertColor_2_700
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.delivery_icon),
                            "",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            nav.navigate(
                                Screens.CreateProduct(
                                    (storeId.value ?: myInfo.value?.storeId
                                    ?: UUID.randomUUID()).toString(),
                                    null
                                )
                            )
                        }, containerColor = CustomColor.alertColor_2_700
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "",
                            tint = Color.White
                        )
                    }

                }
        }

    ) { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()


        if (isSendingData.value && operationType.value == null) Dialog(
            onDismissRequest = {}) {
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


        PullToRefreshBox(
            isRefreshing = isRefresh.value,
            onRefresh = {
                onRefreshDo()
            },
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
                    .background(Color.White)
                    .padding(paddingValue)
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {

                item {
                    ConstraintLayout(
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                    ) {

                        val (bigImageRef, smallImageRef) = createRefs()

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .constrainAs(bigImageRef) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) CustomColor.neutralColor100 else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(
                                        color = if (isFromHome == true) CustomColor.primaryColor50
                                        else Color.White,
                                    ), contentAlignment = Alignment.Center) {
                                when (createdStoreInfoHolder.value?.wallpaperImage == null) {
                                    true -> {
                                        when (storeData?.pigImage.isNullOrEmpty()) {
                                            true -> {
                                                if (isFromHome == false) Icon(
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
                                                        .fillMaxHeight()
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    model = General.handlingImageForCoil(
                                                        storeData.pigImage.toString(), context
                                                    ),
                                                    contentDescription = "",
                                                    loading = {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
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

                                    else -> {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp)),
                                            model = General.handlingImageForCoil(
                                                createdStoreInfoHolder.value!!.wallpaperImage!!.absolutePath,
                                                context
                                            ),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
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
                            if (isFromHome == false) Box(
                                modifier = Modifier
                                    .padding(end = 5.dp, bottom = 10.dp)
                                    .constrainAs(cameralRef) {
                                        end.linkTo(imageRef.end)
                                        bottom.linkTo(imageRef.bottom)
                                    }


                            ) {

                                IconButton(
                                    onClick = {
                                        onBigPictureSelect()
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor500
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

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-50).dp)
                                .constrainAs(smallImageRef) {
                                    top.linkTo(bigImageRef.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }) {
                            val (imageRef, cameralRef) = createRefs()
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
                                    .clip(RoundedCornerShape(60.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isFromHome == true) CustomColor.neutralColor100 else CustomColor.neutralColor500,
                                        shape = RoundedCornerShape(60.dp)
                                    )
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(
                                        color = if (isFromHome == true && storeData == null) CustomColor.primaryColor50
                                        else Color.White, shape = RoundedCornerShape(60.dp)
                                    ),
                                contentAlignment = Alignment.Center) {
                                when (createdStoreInfoHolder.value?.smallImage == null) {
                                    true -> {
                                        when (storeData?.smallImage.isNullOrEmpty()) {
                                            true -> {
                                                if (isFromHome == false) Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.insert_photo),
                                                    "",
                                                    modifier = Modifier.size(50.dp),
                                                    tint = CustomColor.neutralColor200

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
                                                        storeData.smallImage, context
                                                    ),
                                                    contentDescription = "",
                                                    loading = {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
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

                                    else -> {
                                        SubcomposeAsyncImage(
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(90.dp)
                                                .width(90.dp)
                                                .clip(RoundedCornerShape(50.dp)),
                                            model = General.handlingImageForCoil(
                                                createdStoreInfoHolder.value!!.smallImage?.absolutePath,
                                                context
                                            ),
                                            contentDescription = "",
                                            loading = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
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
                            if (isFromHome == false) Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .constrainAs(cameralRef) {
                                        end.linkTo(imageRef.end)
                                        bottom.linkTo(imageRef.bottom)
                                    }

                            ) {
                                IconButton(
                                    onClick = {
                                        keyboardController?.hide()
                                        updateConditionValue(isPigImageValue = false)
                                        onImageSelection.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.size(30.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = CustomColor.primaryColor500
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
                    }

                    Sizer(20)
                }

                item {
                    when (isFromHome) {
                        true -> {
                            Box(
                                Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                            ) {
                                when (storeData == null) {
                                    true -> {
                                        Box(
                                            modifier = Modifier
                                                .width(150.dp)
                                                .height(30.dp)
                                                .background(
                                                    CustomColor.primaryColor50,
                                                    RoundedCornerShape(8.dp)
                                                )
                                        )
                                    }

                                    else -> {
                                        Text(
                                            storeData.name,
                                            fontFamily = General.satoshiFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (24).sp,
                                            color = CustomColor.neutralColor950,
                                            textAlign = TextAlign.Center,
                                        )
                                    }

                                }
                            }
                        }

                        else -> {
                            Sizer(10)

                            Text(
                                stringResource(R.string.store_name),
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center,
                            )
                            TextInputWithTitle(
                                value = storeName,
                                title = "",
                                placeHolder = storeData?.name
                                    ?: stringResource(R.string.write_your_store_name),
                                isHasError = false,
                                onChange = { it ->
                                    storeViewModel.setStoreCreateData(
                                        storeTitle = it,
                                        storeId = storeId.value
                                    )
                                },
                            )


                        }
                    }

                }

                if (isFromHome == false && myStoreId != null)
                    item {
                        Sizer(10)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Text(
                                stringResource(R.string.store_banner),
                                fontFamily = General.satoshiFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = (18).sp,
                                color = CustomColor.neutralColor950,
                                textAlign = TextAlign.Center,
                            )
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(70.dp)
                                    .background(
                                        CustomColor.primaryColor500, RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { openBottonSheetAndUpdateType(EnBottomSheetType.Banner) },
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                        }
                    }

                item {

                    when (banners.value == null && storeData != null) {
                        true -> {
                            Sizer(10)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(
                                        CustomColor.primaryColor50, RoundedCornerShape(8.dp)
                                    )
                            )
                        }

                        else -> {
                            if (!storeBanners.isNullOrEmpty())
                                BannerBage(
                                    storeBanners,
                                    true,
                                    deleteBanner = if (isFromHome == true) null else { it ->
                                        onDeleteBanner(it)
                                    },
                                    isShowTitle = false
                                )

                        }
                    }

                    Sizer(10)
                }

                item {
                    Sizer(10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {

                        Text(
                            stringResource(R.string.store_location),
                            fontFamily = General.satoshiFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (18).sp,
                            color = CustomColor.neutralColor950,
                            textAlign = TextAlign.Center,
                        )
                        IconButton(
                            onClick = {
                                requestPermissionLocation()
                            })
                        {
                            Icon(
                                ImageVector.vectorResource(R.drawable.location_address_list),
                                "",
                                modifier = Modifier.size(24.dp),
                                tint = CustomColor.primaryColor700
                            )
                        }
                    }
                }

                item {

                    AnimatedVisibility(
                        visible = (storeId.value != null || storeData != null)
                    ) {


                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Sizer(20)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    stringResource(R.string.sub_category),
                                    fontFamily = General.satoshiFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (18).sp,
                                    color = CustomColor.neutralColor950,
                                    textAlign = TextAlign.Center,
                                )
                                if (isFromHome == false && myStoreId != null)
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .width(70.dp)
                                            .background(
                                                CustomColor.primaryColor500,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                openBottonSheetAndUpdateType(
                                                    EnBottomSheetType.SupCategory
                                                )
                                            }, contentAlignment = Alignment.Center

                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            "",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                            }
                            Sizer(5)
                            LazyRow {
                                when ((subcategories.value == null)) {
                                    true -> {
                                        items(20, key = { key -> key }) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .height(40.dp)
                                                    .width(90.dp)
                                                    .background(
                                                        CustomColor.primaryColor50,
                                                        RoundedCornerShape(8.dp)
                                                    ), contentAlignment = Alignment.Center

                                            ) {}
                                        }
                                    }

                                    else -> {

                                        if (!storeSubCategories.isNullOrEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .height(40.dp)
                                                        .width(70.dp)
                                                        .background(
                                                            if (selectedSubCategoryId.value == null) CustomColor.alertColor_3_300 else Color.White,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (selectedSubCategoryId.value == null) Color.White else CustomColor.neutralColor200,
                                                            RoundedCornerShape(8.dp)

                                                        )
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .combinedClickable(
                                                            onClick = {
                                                                if (selectedSubCategoryId.value != null) selectedSubCategoryId.value =
                                                                    null
                                                            },
                                                        )
                                                    //
                                                    , contentAlignment = Alignment.Center

                                                ) {
                                                    Text(
                                                        stringResource(R.string.all),
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (18).sp,
                                                        color = if (selectedSubCategoryId.value == null) Color.White else CustomColor.neutralColor200,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }

                                            }
                                            items(
                                                storeSubCategories,
                                                key = { category -> category.id }) { subCategory ->
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .height(40.dp)
//                                                        .width(70.dp)
                                                        .background(
                                                            if (selectedSubCategoryId.value == subCategory.id) CustomColor.alertColor_3_300 else Color.White,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            color = if (selectedSubCategoryId.value == subCategory.id) Color.White else CustomColor.neutralColor200,
                                                            RoundedCornerShape(8.dp)

                                                        )
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .combinedClickable(
                                                            onClick = {
                                                                updateSelectedSubCategory(
                                                                    subCategory.id
                                                                )
                                                            },
                                                            onLongClick = {
                                                                openSubCategoryBottonSheet(
                                                                    subCategory = subCategory
                                                                )
                                                            })
                                                        .padding(horizontal = 10.dp),
                                                    contentAlignment = Alignment.Center

                                                ) {
                                                    Text(
                                                        subCategory.name,
                                                        fontFamily = General.satoshiFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (18).sp,
                                                        color = if (selectedSubCategoryId.value == subCategory.id) Color.White else CustomColor.neutralColor200,

                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }

                                        }

                                    }
                                }

                            }

                            Sizer(10)
                            when (isChangeSubCategory.value) {
                                true -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(90.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = CustomColor.primaryColor200
                                        )
                                    }
                                }

                                else -> {
                                    if (isLoadingMore.value) {

                                        Box(
                                            modifier = Modifier
                                                .padding(top = 15.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = CustomColor.primaryColor700)
                                        }
                                        Sizer(40)
                                    } else when (products.value == null) {
                                        true -> {
                                            ProductLoading()
                                        }

                                        else -> {
                                            if (productFilterBySubCategory.isNotEmpty()) {
                                                ProductShape(
                                                    isCanNavigateToStore = false,
                                                    product = productFilterBySubCategory,
                                                    nav = nav,
                                                    delFun = if (isFromHome == true) null else { it ->
                                                        deleteProduct(it)
                                                    },
                                                    updFun = if (isFromHome == true) null else { it ->
                                                        nav.navigate(
                                                            Screens.CreateProduct(
                                                                storeId.toString(), it.toString()
                                                            )
                                                        )
                                                    })
                                            }
                                        }
                                    }


                                }
                            }

                        }

                    }
                }
                item {
                    Sizer(90)
                }

            }

        }
    }


}