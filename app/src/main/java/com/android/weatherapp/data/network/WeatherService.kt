package com.android.weatherapp.data.network

import com.android.weatherapp.presentation.utils.AppConstants
import com.android.weatherapp.domain.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET(AppConstants.WEATHER_SERVICE_PATH)
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("appid") appid: String?,
    ): Call<WeatherResponse>

}