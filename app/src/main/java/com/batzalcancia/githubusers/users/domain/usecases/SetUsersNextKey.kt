package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.data.local.GithubUsersLocalSource
import javax.inject.Inject

class SetUsersNextKey @Inject constructor(private val githubUsersLocalSource: GithubUsersLocalSource) {
    suspend operator fun invoke(nextKey: Int) {
        githubUsersLocalSource.setNextKey(nextKey)
    }
}