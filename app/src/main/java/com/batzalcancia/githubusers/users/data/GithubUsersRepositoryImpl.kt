package com.batzalcancia.githubusers.users.data

import com.batzalcancia.githubusers.users.data.local.GithubUsersLocalSource
import com.batzalcancia.githubusers.users.data.local.dao.GithubUsersDao
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalDetailsUpdate
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserNoteLocalUpdate
import com.batzalcancia.githubusers.users.data.remote.GithubUsersRemoteSource
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GithubUsersRepositoryImpl @Inject constructor(
    private val githubUsersRemoteSource: GithubUsersRemoteSource,
    private val githubUsersDao: GithubUsersDao,
    private val githubUsersLocalSource: GithubUsersLocalSource
) : GithubUsersRepository {

    override suspend fun getGithubUsers(since: Int) = withContext(Dispatchers.IO) {
        githubUsersRemoteSource.getGithubUsers(since)
    }

    override suspend fun getGithubUserDetails(username: String) = withContext(Dispatchers.IO) {
        githubUsersRemoteSource.getGithubUserDetails(username)
    }

    override fun getLocalGithubUsers() = githubUsersDao.getAllGithubUsers()

    override suspend fun getLocalGithubUserDetails(username: String): GithubUserLocal =
        githubUsersDao.getGithubUser(username)

    override suspend fun insertGithubUsers(githubUsers: List<GithubUserLocal>) {
        githubUsersDao.insertAllUsers(githubUsers)
    }

    override suspend fun saveNextKey(nextKey: Int) {
        githubUsersLocalSource.setNextKey(nextKey)
    }

    override suspend fun getNextKey() = githubUsersLocalSource.getNextKey() ?: 0

    override suspend fun updateNote(githubUserNoteLocalUpdate: GithubUserNoteLocalUpdate) =
        withContext(Dispatchers.IO) {
            githubUsersDao.updateNote(githubUserNoteLocalUpdate)
        }

    override suspend fun updateGithubUser(githubUserLocalDetailsUpdate: GithubUserLocalDetailsUpdate) =
        withContext(Dispatchers.IO) {
            githubUsersDao.saveGithubUserDetailsLocally(githubUserLocalDetailsUpdate)
        }

    override suspend fun searchGithubUser(searchString: String) =
        withContext(Dispatchers.IO) {
            githubUsersDao.searchGithubUser(searchString)
        }

    override suspend fun getCachedGithubUser(): List<GithubUserLocal> =
        withContext(Dispatchers.IO) {
            githubUsersDao.getAllCachedGithubUsers()
        }


}