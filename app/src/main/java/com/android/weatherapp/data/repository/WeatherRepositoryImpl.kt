package com.android.weatherapp.data.repository

import android.content.SharedPreferences
import com.android.weatherapp.data.network.WeatherService
import com.android.weatherapp.domain.model.WeatherResponse
import com.android.weatherapp.domain.repository.WeatherRepository
import com.android.weatherapp.presentation.utils.constants.AppConstants
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val sharedPreferences: SharedPreferences
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<WeatherResponse> =
                    weatherService.getWeather(latitude, longitude, AppConstants.METRIC_UNIT, AppConstants.API_KEY)
                        .execute()
                if (response.isSuccessful) {
                    val weatherResponse: WeatherResponse? = response.body()
                    weatherResponse?.let {
                        val weatherResponseJson: String = Gson().toJson(it)
                        sharedPreferences.edit().putString(AppConstants.WEATHER_RESPONSE_DATA, weatherResponseJson)
                            .apply()
                    }
                    weatherResponse
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
