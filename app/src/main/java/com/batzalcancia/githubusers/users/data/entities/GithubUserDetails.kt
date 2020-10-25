package com.batzalcancia.githubusers.users.data.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubUserDetails(
    val id: Int,
    val name: String,
    val company: String?,
    val blog: String?,
    val location: String?,
    @SerialName("public_repos")
    val publicRepos: Int,
    @SerialName("public_gists")
    val publicGists: Int,
    val followers: Int,
    val following: Int
)