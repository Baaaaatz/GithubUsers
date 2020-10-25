package com.batzalcancia.githubusers.users.data.remote

import com.batzalcancia.githubusers.core.helpers.httpCall
import javax.inject.Inject

class GithubUsersRemoteSourceImpl @Inject constructor(
    private val githubUsersApi: GithubUsersApi
) : GithubUsersRemoteSource {

    override suspend fun getGithubUsers(since: Int) = httpCall {
        githubUsersApi.getGithubUsers(since)
    }

    override suspend fun getGithubUserDetails(username: String) = httpCall {
        githubUsersApi.getGithubUserDetails(username)
    }

}