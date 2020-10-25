package com.batzalcancia.githubusers.core.helpers

import com.batzalcancia.githubusers.ConnectionStateBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun CoroutineScope.detectConnection(doWhenConnected: () -> Unit, doWhenDisconnected: () -> Unit = {}) {
    ConnectionStateBus.on().onEach {
        if (it) doWhenConnected()
        else doWhenDisconnected()
    }.launchIn(this)
}