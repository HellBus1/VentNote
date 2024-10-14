package com.digiventure.ventnote.commons

import com.digiventure.utils.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest: BaseUnitTest() {
    @Test
    fun convertDateStringShouldReturnValidFormattedString() {
        val expectedDateString = "Thu, Jan 1"

        assertEquals(expectedDateString, DateUtil.convertDateString(
            "EEE, MMM d",
            "Thu Jan 01 07:00:00 GMT+07:00 1970"
        ))
    }

    @Test
    fun convertDateStringShouldReturnEmptyStringWhenError() {
        val expectedDateString = ""

        assertEquals(expectedDateString, DateUtil.convertDateString(
            "EEE, MMM d",
            "Thu Jn 01 07:00:00 GMT+07:00 1970"
        ))
    }
}