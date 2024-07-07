package com.android.weatherapp.domain.model

import java.io.Serializable

data class Sys(
    val type: Int,
    val message: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
) : Serializable
