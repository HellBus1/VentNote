package com.digiventure.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class BaseUnitTest {
    @get:Rule
    var coroutinesTestRule = MainCoroutineScopeRule()

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()
}