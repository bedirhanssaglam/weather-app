package com.android.weatherapp.domain.repository

import com.android.weatherapp.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse?
}