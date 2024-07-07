package com.android.weatherapp.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatUnixTime(unixTime: Long): String {
        val date = Date(unixTime * 1000L)
        return SimpleDateFormat(AppConstants.DATE_FORMAT_PATTERN, Locale.UK).apply {
            timeZone = TimeZone.getDefault()
        }.format(date)
    }
}