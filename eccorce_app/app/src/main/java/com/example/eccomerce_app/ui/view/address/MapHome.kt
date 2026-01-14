package com.example.eccomerce_app.ui.view.address


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.e_commercompose.model.enMapType
import com.example.e_commercompose.ui.component.CustomAuthBottom
import com.example.e_commercompose.ui.component.CustomButton
import com.example.e_commercompose.ui.component.Sizer
import com.example.eccomerce_app.ui.component.TextInputWithTitle
import com.example.e_commercompose.ui.theme.CustomColor
import com.example.eccomerce_app.ui.Screens
import com.example.eccomerce_app.viewModel.StoreViewModel
import com.example.eccomerce_app.viewModel.UserViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.e_commercompose.R
import com.example.eccomerce_app.viewModel.CartViewModel
import com.example.eccomerce_app.viewModel.MapViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapHomeScreen(
    nav: NavHostController,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    mapViewModel: MapViewModel,
    cartViewModel: CartViewModel,
    title: String? = null,
    id: String? = null,
    longitude: Double?,
    latitude: Double?,
    additionLong: Double? = null,
    additionLat: Double? = null,
    mapType: enMapType = enMapType.My,
    isFomLogin: Boolean = true,

    ) {

    val context = LocalContext.current

    val directions = mapViewModel.googlePlaceInfo.collectAsState()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val sheetState = rememberModalBottomSheetState()

    val coroutine = rememberCoroutineScope()


    val isMyLocation = rememberSaveable { mutableStateOf(false) }
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val isHasError = remember { mutableStateOf(false) }
    val isOpenSheet = rememberSaveable { mutableStateOf(false) }
    val isHasTitle = (mapType == enMapType.My)
    val isHasNavigationMap = (mapType == enMapType.Store || mapType == enMapType.TrackOrder)


    val errorMessage = remember { mutableStateOf("") }

    val addressTitle = remember { mutableStateOf(TextFieldValue(title ?: "")) }

    val additionLatLng = remember {
        mutableStateOf(
            if (additionLat == null) LatLng(0.0, 0.0)
            else LatLng(additionLat, additionLong!!)
        )
    }

    val additionLocation = rememberUpdatedMarkerState(
        position = additionLatLng.value
    )

    val mainLocation = rememberUpdatedMarkerState(
        position = LatLng(
            latitude ?: 15.347509735207755,
            longitude ?: 44.20684900134802
        )
    )

    val marker = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mainLocation.position, 15f
        )
    }


    val snackBarHostState = remember { SnackbarHostState() }

    fun updateMainLocation(point: LatLng) {
        mainLocation.position = point
    }

    fun handleMapClick(point: LatLng) {
        when (mapType) {
            enMapType.My -> { updateMainLocation(point) }
            enMapType.MyStore -> { coroutine.launch {
                    storeViewModel.setStoreCreateData(point.longitude, point.latitude)
                    updateMainLocation(point)
                    isMyLocation.value = true
                } }
            else -> {}
        }
    }

    fun validateUserAddressTitle(): Boolean {
        isHasError.value = false;
        errorMessage.value = ""
        when (addressTitle.value.text.isEmpty()) {
            true -> {
                isHasError.value = true;
                errorMessage.value = "Address Title mustn't be empty"
                return false
            }

            else -> {
                return true;
            }
        }
    }

    fun updateCameraToUser() {
        val newCameraPosition = CameraPosition.fromLatLngZoom(
            additionLocation.position,
            20f
        )

        coroutine.launch {
            marker.animate(update = CameraUpdateFactory.newCameraPosition(newCameraPosition))
        }
    }

    //to request the update in location
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100L) // Desired interval: 500ms
            .setMinUpdateIntervalMillis(100L) // Fastest acceptable interval: 100ms
            .setMinUpdateDistanceMeters(1f)
            .setWaitForAccurateLocation(false)
            .build()
    }

    //to track the update in user location
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.d(
                    "ChangeLocation",
                    "${location.latitude.toString()} ${location.longitude.toString()}"
                )
//                additionLatLng.value = LatLng(
//                    location.latitude,
//                    location.longitude
//                )
            }
        }
    }

    val requestPermissionThenNavigate = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val arePermissionsGranted = permissions.values.reduce { acc, next -> acc && next }

            if (arePermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@rememberLauncherForActivityResult
                }
                if (isHasNavigationMap)
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (isHasNavigationMap) {
            Log.d("appPassingTheNavigation","Yes\n" +
                    "${directions.value?.toString()}")
            mapViewModel.findPointBetweenTwoDestination(
                mainLocation.position,
                additionLocation.position,
                context.getString(R.string.google_map_token),
            )
        }
    }

    LaunchedEffect(Unit) {
        requestPermissionThenNavigate.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    }

    DisposableEffect(Unit)
    {
        onDispose {
            if (isHasNavigationMap)
                fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButton = {
            if (isHasNavigationMap)
                FloatingActionButton(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    onClick = {
                        updateCameraToUser()
                    },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = Color.White
                )
                {
                    Image(
                        imageVector = ImageVector
                            .vectorResource(id = R.drawable.current_location),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                }

            if (isMyLocation.value)
                FloatingActionButton(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    onClick = {
                        nav.popBackStack()
                    },
                    shape = RoundedCornerShape(8.dp),
                    containerColor = CustomColor.alertColor_1_600
                )
                {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }

        },
        floatingActionButtonPosition = FabPosition.Start,
        bottomBar = {
            if (isOpenSheet.value) ModalBottomSheet(
                onDismissRequest = {
                    isOpenSheet.value = false
                },
                sheetState = sheetState,
                containerColor = Color.White

                )
            {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                )
                {
                    TextInputWithTitle(
                        value = addressTitle,
                        title = stringResource(R.string.address_title),
                        placeHolder = addressTitle.value.text.ifEmpty { stringResource(R.string.write_address_name) },
                        errorMessage = errorMessage.value,
                        isHasError = isHasError.value,

                        )
                    Sizer(10)
                    CustomAuthBottom(
                        operation = {
                            coroutine.launch {
                                isLoading.value = true
                                isOpenSheet.value = false

                                val result = async {
                                    if (id.isNullOrEmpty()) userViewModel.addUserAddress(
                                        longitude = mainLocation.position.longitude,
                                        latitude = mainLocation.position.latitude,
                                        title = addressTitle.value.text
                                    )
                                    else userViewModel.updateUserAddress(
                                        addressId = UUID.fromString(id),
                                        addressTitle = addressTitle.value.text,
                                        longitude = mainLocation.position.longitude,
                                    latitude = mainLocation.position.latitude,

                                        )
                                }.await()

                                cartViewModel.calculateOrderDistanceToUser(
                                    stores = storeViewModel.stores.value,
                                    currentAddress = userViewModel.userInfo.value?.address?.firstOrNull { it -> it.isCurrent }
                                )
                                isLoading.value = false
                                if (!result.isNullOrEmpty()) {
                                    snackBarHostState.showSnackbar(result)
                                    return@launch
                                }

                                snackBarHostState.showSnackbar(
                                    message = if (id.isNullOrEmpty()) context.getString(R.string.address_add_successfully)
                                    else context.getString(R.string.address_updated_successfully)
                                )

                                if (!isFomLogin) {
                                    nav.popBackStack()
                                    return@launch
                                }

                                userViewModel.userPassLocation(true)
                                nav.navigate(Screens.HomeGraph) {
                                    popUpTo(nav.graph.id) {
                                        inclusive = true
                                    }
                                }


                            }

                        },
                        buttonTitle = if (id.isNullOrEmpty()) stringResource(R.string.add) else stringResource(
                            R.string.update
                        ),
                        validationFun = {
                            validateUserAddressTitle()
                        },
                        isLoading = isLoading.value
                    )
                    Sizer(10)
                }
            }
        })
    { paddingValue ->
        paddingValue.calculateTopPadding()
        paddingValue.calculateBottomPadding()

        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            val (bottomRef) = createRefs()
            GoogleMap(
                modifier = Modifier
                    .padding(paddingValue  )
                    .fillMaxHeight(),
                cameraPositionState = marker,
                onMapClick = { latLng ->
                    handleMapClick(latLng)
                },
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    indoorLevelPickerEnabled = true,

                    ),
                properties = MapProperties(isMyLocationEnabled = if (isHasNavigationMap) true else false)
            )
            {
                if (isHasNavigationMap == false)
                    Marker(
                        state = MarkerState(position = mainLocation.position),
                        title = title,
                    )
                else {


                    Marker(
                        state = MarkerState(position = additionLocation.position),
                        title = "My Place",
                    )

                    if (mapType == enMapType.Store)
                        MarkerComposable(
                            state = MarkerState(position = mainLocation.position),

                            title = title,
                            onClick = {
                                true
                            }
                        ) {
                            Image(
                                imageVector = ImageVector
                                    .vectorResource(id = R.drawable.store_icon),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )

                        }
                }

                if (!directions.value.isNullOrEmpty() && isHasNavigationMap)
                    Polyline(
                        directions.value!!,
                        color = Color.Red,
                        pattern = listOf(
                            Dash(15f), Gap(2f)
                        )
                    )
            }




            if (isHasTitle) CustomButton(
                buttonTitle = if (!id.isNullOrEmpty()) stringResource(R.string.edite_current_location) else stringResource(
                    R.string.add_new_address
                ),
                color = CustomColor.primaryColor700,
                isEnable = true,
                customModifier = Modifier
                    .padding(bottom = paddingValue.calculateBottomPadding() + 10.dp)

                    .height(50.dp)
                    .fillMaxWidth(0.9f)
                    .constrainAs(bottomRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },

                isLoading = false,
                operation = {
                    when (isHasTitle) {
                        true -> {
                            isOpenSheet.value = true
                        }

                        else -> {

                        }
                    }
                },
                labelSize = 20
            )

        }
    }

    if (isLoading.value) Dialog(
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
}
