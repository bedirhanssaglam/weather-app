package com.android.weatherapp.presentation.utils

import com.android.weatherapp.presentation.utils.constants.AppConstants
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatUnixTime(unixTime: Long): String {
        val date = Date(unixTime * 1000L)
        return SimpleDateFormat(AppConstants.UNIX_DATE_FORMAT_PATTERN, Locale.UK).apply {
            timeZone = TimeZone.getDefault()
        }.format(date)
    }

    fun formatToday(): String {
        val formatter = SimpleDateFormat(AppConstants.TODAY_DATE_FORMAT_PATTERN, Locale.UK)

        return formatter.format(Date())
    }
}