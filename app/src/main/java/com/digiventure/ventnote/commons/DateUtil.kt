package com.digiventure.ventnote.commons

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    /**
     * Return formatted date string
     * @param format pattern can be see here https://developer.android.com/reference/kotlin/android/icu/text/SimpleDateFormat
     * @param dateString is raw date in string
     * */
    fun convertDateString(format: String, dateString: String): String {
        return try {
            val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
            val inputDate = inputDateFormat.parse(dateString)

            val outputDateFormat = SimpleDateFormat(format, Locale.getDefault())
            val outputDateString = inputDate?.let { outputDateFormat.format(it) }

            (outputDateString?.format(dateString) ?: Date()).toString()
        } catch (e: ParseException) {
            ""
        }
    }
}