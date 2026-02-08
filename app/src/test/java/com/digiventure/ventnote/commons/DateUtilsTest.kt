package com.digiventure.ventnote.commons

import com.digiventure.utils.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class DateUtilsTest: BaseUnitTest() {
    @Test
    fun formatNoteDateShouldReturnFormattedString() {
        // Set a fixed date: 2023-10-27 10:30 AM
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(2023, Calendar.OCTOBER, 27, 10, 30, 0)
        val date = calendar.time

        // The default formatter uses EEEE, MMMM d h:mm a
        val result = DateUtil.formatNoteDate(date)
        
        // Assert that it contains basic info
        assertTrue(result.contains("October"))
        assertTrue(result.contains("27"))
    }

    @Test
    fun convertDateStringShouldReturnValidFormattedString() {
        val expectedDateString = "Thu, Jan 1"

        assertEquals(expectedDateString, DateUtil.convertDateString(
            "EEE, MMM d",
            "Thu Jan 01 00:00:00 UTC 1970"
        ))
    }

    @Test
    fun convertDateStringShouldReturnEmptyStringWhenError() {
        assertEquals("", DateUtil.convertDateString(
            "EEE, MMM d",
            "Invalid Date String"
        ))
    }
}