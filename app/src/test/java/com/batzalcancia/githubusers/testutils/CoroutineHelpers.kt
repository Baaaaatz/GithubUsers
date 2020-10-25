package com.batzalcancia.githubusers.testutils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

suspend fun <T> Flow<T>.firstForTest() = withTimeout(TIMEOUT) {
    first()
}

suspend fun <T> Flow<T>.firstForTestOrNull() = withTimeoutOrNull(TIMEOUT) {
    first()
}

const val TIMEOUT = 3000L