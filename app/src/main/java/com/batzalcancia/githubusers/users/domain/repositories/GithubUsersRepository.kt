package com.batzalcancia.githubusers.users.domain.repositories

import androidx.paging.PagingSource
import com.batzalcancia.githubusers.users.data.entities.GithubUser
import com.batzalcancia.githubusers.users.data.entities.GithubUserDetails
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalDetailsUpdate
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserNoteLocalUpdate

interface GithubUsersRepository {
    suspend fun getGithubUsers(since: Int): List<GithubUser>
    suspend fun getGithubUserDetails(username: String): GithubUserDetails

    fun getLocalGithubUsers(): PagingSource<Int, GithubUserLocal>
    suspend fun getLocalGithubUserDetails(username: String): GithubUserLocal
    suspend fun insertGithubUsers(githubUsers: List<GithubUserLocal>)

    suspend fun saveNextKey(nextKey: Int)
    suspend fun getNextKey(): Int

    suspend fun updateNote(githubUserNoteLocalUpdate: GithubUserNoteLocalUpdate)
    suspend fun updateGithubUser(githubUserLocalDetailsUpdate: GithubUserLocalDetailsUpdate)

    suspend fun searchGithubUser(searchString: String): List<GithubUserLocal>
    suspend fun getCachedGithubUser(): List<GithubUserLocal>
}