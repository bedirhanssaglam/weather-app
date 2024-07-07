package com.android.weatherapp.domain.usecase

import com.android.weatherapp.data.repository.WeatherRepositoryImpl
import com.android.weatherapp.domain.model.WeatherResponse
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepositoryImpl) {
    suspend operator fun invoke(latitude: Double, longitude: Double): WeatherResponse? {
        return weatherRepository.getWeather(latitude, longitude)
    }
}