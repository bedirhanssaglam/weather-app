package com.android.weatherapp.presentation.utils

import com.android.weatherapp.R

enum class WeatherIcon(val code: String, val drawableId: Int) {
    SUNNY("01d", R.drawable.sunny),
    CLOUD("02d", R.drawable.cloud),
    RAIN("10d", R.drawable.rain),
    STORM("11d", R.drawable.storm),
    SNOW("13d", R.drawable.snowflake),
    UNKNOWN("", R.drawable.cloud)
}

fun getWeatherIconDrawable(iconCode: String): Int {
    return WeatherIcon.entries.find { it.code == iconCode }?.drawableId ?: WeatherIcon.UNKNOWN.drawableId
}