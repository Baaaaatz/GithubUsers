package com.batzalcancia.githubusers.core.helpers

import com.batzalcancia.githubusers.core.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel


fun <T> T.toEvent() =
    Event(this)

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> ConflatedBroadcastChannel<Event<T>>.offerEvent(value: T) = offer(value.toEvent())