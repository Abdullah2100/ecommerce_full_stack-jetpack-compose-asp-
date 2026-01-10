package com.example.eccomerce_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eccomerce_app.data.NetworkCallHandler
import com.example.eccomerce_app.data.repository.MapRepository
import com.example.eccomerce_app.dto.GooglePlacesInfo
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {

    private val _googlePlaceInfo = MutableStateFlow<List<LatLng>?>(null)
    val googlePlaceInfo = _googlePlaceInfo.asStateFlow()
    fun findPointBetweenTwoDestination(origin: LatLng, destination: LatLng, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = mapRepository.getDistanceBetweenTwoPoint(origin, destination, key)) {
                is NetworkCallHandler.Successful<*> -> {
                    val info = result.data as? GooglePlacesInfo
                    info?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPath ->
                        val decodedPoints = PolyUtil.decode(encodedPath)
                        _googlePlaceInfo.emit(decodedPoints)
                    }

                }

                is NetworkCallHandler.Error -> {

                }
            }

        }
    }

}