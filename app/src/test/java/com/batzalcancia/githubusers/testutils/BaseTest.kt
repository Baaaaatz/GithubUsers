package com.batzalcancia.githubusers.testutils

import org.junit.Rule

open class BaseCommonTest {
    @get:Rule
    val rule = CoroutineTestRule()
}