package com.batzalcancia.githubusers.users.domain.usecases

import com.batzalcancia.githubusers.users.data.local.entities.GithubUserNoteLocalUpdate
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import javax.inject.Inject

class UpdateNote @Inject constructor(
    private val githubUsersRepository: GithubUsersRepository
) {
    suspend operator fun invoke(id: Int, note: String) {
        githubUsersRepository.updateNote(GithubUserNoteLocalUpdate(id, note))
    }
}