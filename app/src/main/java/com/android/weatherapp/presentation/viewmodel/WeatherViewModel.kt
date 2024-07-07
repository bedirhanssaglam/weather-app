package com.android.weatherapp.presentation.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.weatherapp.domain.model.WeatherResponse
import com.android.weatherapp.domain.usecase.GetWeatherUseCase
import com.android.weatherapp.presentation.utils.enums.LoadingState
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?> = _weather

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            lastLocation?.let {
                getWeather(it.latitude, it.longitude)
            }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.BUSY
            _weather.value = getWeatherUseCase(latitude, longitude)
            _loadingState.value = LoadingState.IDLE
        }
    }

    fun requestLocationData(context: Context) {
        val locationRequest: LocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 100000L
        ).build()

        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }
}