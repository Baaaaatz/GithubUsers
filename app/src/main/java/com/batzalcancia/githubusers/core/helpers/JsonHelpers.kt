package com.batzalcancia.githubusers.core.helpers

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify

@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
inline fun <reified T : Any> String.fromJson() = Json.parse<T>(this)

@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
inline fun <reified T : Any> T.toJson() = Json.stringify(this)