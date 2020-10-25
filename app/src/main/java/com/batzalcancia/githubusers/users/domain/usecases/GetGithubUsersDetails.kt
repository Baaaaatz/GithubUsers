package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalUpdate
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class GetGithubUsersDetails @Inject constructor(private val githubUsersRepository: GithubUsersRepository) {
    suspend operator fun invoke(username: String): GithubUserLocal {

        if (!githubUsersRepository.getLocalGithubUserDetails(username).savedLocally) {
            val user = githubUsersRepository.getGithubUserDetails(username)
            githubUsersRepository.updateGithubUser(
                GithubUserLocalUpdate(
                    user.id,
                    user.company ?: "N/A",
                    user.blog ?: "N/A",
                    user.location ?: "N/A",
                    user.publicRepos,
                    user.publicGists,
                    user.followers,
                    user.following
                )
            )
        }

        return githubUsersRepository.getLocalGithubUserDetails(username)

    }
}

