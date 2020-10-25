package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class GetLocalGithubUsers @Inject constructor(private val githubUsersRepository: GithubUsersRepository) {
    operator fun invoke() = githubUsersRepository.getLocalGithubUsers()
}