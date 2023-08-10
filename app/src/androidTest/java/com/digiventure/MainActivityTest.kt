package com.digiventure

import androidx.test.ext.junit.rules.activityScenarioRule
import com.digiventure.utils.BaseAcceptanceTest
import com.digiventure.ventnote.MainActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.Before
import org.junit.Rule

class MainActivityTest: BaseAcceptanceTest() {
    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @MockK
    lateinit var mockAppUpdateManager: AppUpdateManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { mockAppUpdateManager.registerListener(any()) } just Runs
        every { mockAppUpdateManager.unregisterListener(any()) } just Runs
//        every { mockAppUpdateManager.appUpdateInfo } returns createMockAppUpdateInfo()
//        every { mockAppUpdateManager.isUpdateTypeAllowed(any()) } returns true
//        every { mockAppUpdateManager.startUpdateFlowForResult(any(), any(), any(), any()) } just Runs

        // Replace the real AppUpdateManager with the mock in the tested activity
//        val activity = activityRule.activity
//        activity.appUpdateManager = mockAppUpdateManager
    }
}