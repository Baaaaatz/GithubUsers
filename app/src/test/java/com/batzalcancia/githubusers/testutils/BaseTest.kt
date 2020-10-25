package com.batzalcancia.githubusers.testutils

import org.junit.Rule

open class BaseTest  {
    @get:Rule
    val rule = CoroutineTestRule()
}