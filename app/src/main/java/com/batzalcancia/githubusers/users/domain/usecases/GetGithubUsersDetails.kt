package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalDetailsUpdate
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class GetGithubUsersDetails @Inject constructor(private val githubUsersRepository: GithubUsersRepository) {
    suspend operator fun invoke(username: String): GithubUserLocal {

        // Check if user is savedLocally
        if (!githubUsersRepository.getLocalGithubUserDetails(username).savedLocally) {
            // if not app will save it locally
            val user = githubUsersRepository.getGithubUserDetails(username)
            githubUsersRepository.updateGithubUser(
                GithubUserLocalDetailsUpdate(
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

        // When saved it will return a savedUser
        return githubUsersRepository.getLocalGithubUserDetails(username)

    }
}

