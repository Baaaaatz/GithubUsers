package com.batzalcancia.githubusers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
object ConnectionStateBus {
    private val isConnectedBus = ConflatedBroadcastChannel<Boolean>()
    suspend fun send(isConnected: Boolean) {
        isConnectedBus.send(isConnected)
    }

    fun on() = isConnectedBus
        .asFlow()
}
