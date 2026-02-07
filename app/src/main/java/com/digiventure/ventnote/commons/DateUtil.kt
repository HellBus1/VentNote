package com.digiventure.ventnote.commons

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateUtil {
    private val defaultFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d h:mm a", Locale.getDefault())
    private val zoneId = ZoneId.systemDefault()

    /**
     * Return formatted date string using modern java.time API
     * @param date the Date object to format
     */
    fun formatNoteDate(date: Date): String {
        return try {
            val instant = date.toInstant()
            val localDateTime = instant.atZone(zoneId).toLocalDateTime()
            defaultFormatter.format(localDateTime)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Legacy support for string-based conversion, optimized with java.time
     */
    fun convertDateString(format: String, dateString: String): String {
        return try {
            // "EEE MMM dd HH:mm:ss zzz yyyy" is the default Date.toString() format
            val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            val dateTime = java.time.ZonedDateTime.parse(dateString, formatter)
            
            val outputFormatter = DateTimeFormatter.ofPattern(format, Locale.getDefault())
            outputFormatter.format(dateTime)
        } catch (e: Exception) {
            ""
        }
    }
}