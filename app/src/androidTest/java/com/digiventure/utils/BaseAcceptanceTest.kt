package com.digiventure.utils

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseAcceptanceTest() {
    @get:Rule(order = 0)
    val composeTestRule = createComposeRule()
}