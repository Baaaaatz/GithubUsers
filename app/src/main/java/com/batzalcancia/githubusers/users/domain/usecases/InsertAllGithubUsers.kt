package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class InsertAllGithubUsers @Inject constructor(private val githubUsersRepository: GithubUsersRepository) {
    suspend operator fun invoke(githubUsers: List<GithubUserLocal>) {
        githubUsersRepository.insertGithubUsers(githubUsers)
    }
}