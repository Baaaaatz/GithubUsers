package com.batzalcancia.githubusers.users.data.remote

import com.batzalcancia.githubusers.users.data.entities.GithubUser
import com.batzalcancia.githubusers.users.data.entities.GithubUserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GithubUsersRemoteSource {

    suspend fun getGithubUsers(since: Int): List<GithubUser>
    suspend fun getGithubUserDetails(username: String): GithubUserDetails

}