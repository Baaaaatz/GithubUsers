package com.batzalcancia.githubusers.testutils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

open class BaseAndroidTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
}