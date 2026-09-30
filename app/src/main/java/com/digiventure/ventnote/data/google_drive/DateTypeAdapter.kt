package com.digiventure.ventnote.data.google_drive

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Robust Date adapter for Gson that supports:
 * - Numeric timestamps (e.g. 1774674000000)
 * - Numeric timestamp strings (e.g. "1774674000000")
 * - ISO-8601 date strings (e.g. "2026-03-28T09:00:00.000Z")
 * - Formatted date strings across standard US and local formats
 * - Graceful fallback to current time if date cannot be parsed (preventing restore crashes)
 */
class DateTypeAdapter : JsonDeserializer<Date>, JsonSerializer<Date> {
    private val dateFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd",
        "MMM dd, yyyy, h:mm:ss a",
        "MMM d, yyyy, h:mm:ss a",
        "MMM dd, yyyy h:mm:ss a",
        "MMM d, yyyy h:mm:ss a",
        "MMM dd, yyyy",
        "MMM d, yyyy"
    )

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return if (src != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            JsonPrimitive(sdf.format(src))
        } else {
            JsonNull.INSTANCE
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
        if (json == null || json.isJsonNull) return null

        if (json.isJsonPrimitive) {
            val primitive = json.asJsonPrimitive
            if (primitive.isNumber) {
                return Date(primitive.asLong)
            }
            if (primitive.isString) {
                val str = primitive.asString.trim()
                // Numeric string timestamp
                str.toLongOrNull()?.let { return Date(it) }

                for (format in dateFormats) {
                    try {
                        val sdf = SimpleDateFormat(format, Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        return sdf.parse(str)
                    } catch (_: Exception) { }
                }

                try {
                    return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault()).parse(str)
                } catch (_: Exception) { }

                try {
                    return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US).parse(str)
                } catch (_: Exception) { }
            }
        }
        return Date(System.currentTimeMillis())
    }
}
