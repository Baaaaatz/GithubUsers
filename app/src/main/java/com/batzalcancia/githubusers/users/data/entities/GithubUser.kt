package com.batzalcancia.githubusers.users.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubUser (
    val id: Int,
    @SerialName("login")
    val username: String,
    @SerialName("avatar_url")
    val image: String,
    val type: String
)