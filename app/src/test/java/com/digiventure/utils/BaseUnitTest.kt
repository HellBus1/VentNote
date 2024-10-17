package com.digiventure.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class BaseUnitTest {
    @get:Rule
    var coroutinesTestRule = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()
}