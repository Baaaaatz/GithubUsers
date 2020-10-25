package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class SearchGithubUser @Inject constructor(private val githubUsersRepository: GithubUsersRepository) {
    suspend operator fun invoke(searchString: String) =
        githubUsersRepository.searchGithubUser(searchString)

}