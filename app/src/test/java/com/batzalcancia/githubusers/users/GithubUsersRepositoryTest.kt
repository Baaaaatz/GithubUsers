package com.batzalcancia.githubusers.users

import com.batzalcancia.githubusers.testutils.BaseTest
import com.batzalcancia.githubusers.testutils.users
import com.batzalcancia.githubusers.users.data.GithubUsersRepositoryImpl
import com.batzalcancia.githubusers.users.data.entities.GithubUserDetails
import com.batzalcancia.githubusers.users.data.local.GithubUsersLocalSource
import com.batzalcancia.githubusers.users.data.local.dao.GithubUsersDao
import com.batzalcancia.githubusers.users.data.remote.GithubUsersRemoteSource
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

// Testing for remote api calls
class GithubUsersRepositoryTest : BaseTest() {
    private lateinit var githubUsersRepository: GithubUsersRepository
    lateinit var githubUserRemoteSource: GithubUsersRemoteSource
    lateinit var githubUserLocalSource: GithubUsersLocalSource
    lateinit var githubUsersDao: GithubUsersDao

    @BeforeTest
    fun setup() {
        githubUserLocalSource = mockk()
        githubUserRemoteSource = mockk()
        githubUsersDao = mockk()
        githubUsersRepository = GithubUsersRepositoryImpl(
            githubUserRemoteSource,
            githubUsersDao,
            githubUserLocalSource
        )
    }


    @Test
    fun `getGithub should return list of users`() = runBlocking {
        // When
        coEvery {
            githubUserRemoteSource.getGithubUsers(0)
        } returns users

        val actualUsers = githubUsersRepository.getGithubUsers(0)

        // Then
        assertEquals(30, actualUsers.size)
    }

    @Test
    fun `getGithubUserDetails should return a user`() = runBlocking {
        // Given
        val id = 0
        val username = "username$id"

        // When
        coEvery {
            githubUserRemoteSource.getGithubUserDetails(username)
        } returns GithubUserDetails(
            id = id,
            name = "name",
            company = null,
            blog = null,
            location = null,
            publicRepos = 0,
            publicGists = 0,
            followers = 0,
            following = 0
        )

        val actualUser = githubUsersRepository.getGithubUserDetails(username)

        // Then
        assertEquals(id, actualUser.id)
    }

}
