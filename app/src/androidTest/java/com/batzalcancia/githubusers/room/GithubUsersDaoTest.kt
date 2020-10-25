package com.batzalcancia.githubusers.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.batzalcancia.githubusers.testutils.BaseAndroidTest
import com.batzalcancia.githubusers.testutils.GithubUsersFactory
import com.batzalcancia.githubusers.users.data.local.database.GithubUsersDatabase
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalDetailsUpdate
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserNoteLocalUpdate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//Testing for Room Database
@RunWith(AndroidJUnit4::class)
class GithubUsersDaoTest : BaseAndroidTest() {
    private lateinit var database: GithubUsersDatabase
    val id = 0

    @Before
    fun initDb() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GithubUsersDatabase::class.java
        ).build()
        val users = GithubUsersFactory.makeGithubUserLocalList(30)
        database.getGithubUsersDao().insertAllUsers(users)
    }

    @Test
    fun insertAllUsers_shouldInsertUsersToDatabase() = runBlocking {
        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.isNotEmpty())
    }

    @Test
    fun updateNote_shouldUpdateNoteOfUser() = runBlocking {
        // Given
        val note = "This is a note"
        val githubUserNoteLocalUpdate = GithubUserNoteLocalUpdate(id, note)

        // When
        database.getGithubUsersDao().updateNote(githubUserNoteLocalUpdate)


        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.first {
            it.id == id
        }.note != null)
    }

    @Test
    fun getGithubUser_shouldReturnALocalGithubUser() = runBlocking {
        // Given
        val username = "username$id"

        // When
        val githubUser = database.getGithubUsersDao().getGithubUser(username)

        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.first { it.id == id } == githubUser)
    }

    @Test
    fun saveGithubUserDetailsLocally_shouldUpdateUsersSavedLocallyFlaggedToTrue() = runBlocking {
        // When
        database.getGithubUsersDao().saveGithubUserDetailsLocally(
            GithubUserLocalDetailsUpdate(
                id = id,
                company = "company$id",
                blog = "blog$id",
                location = "location$id",
                publicRepos = id,
                publicGists = id,
                followers = id,
                following = id,
                savedLocally = true
            )
        )

        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.first { it.id == id }.savedLocally)
    }

    @Test
    fun searchGithubUser_shouldReturnGithubUsersBeingSearchedViaUsername() = runBlocking {
        // Given
        val searchString = "username$id"

        // When
        val searchedUser = database.getGithubUsersDao().searchGithubUser(searchString)

        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.filter { it.username == searchString } == searchedUser)
    }

    @Test
    fun searchGithubUser_shouldReturnGithubUsersBeingSearchedViaNotes() = runBlocking {
        // Given
        val searchString = "note$id"

        // When
        val searchedUser = database.getGithubUsersDao().searchGithubUser(searchString)

        //Then
        val githubUsers = database.getGithubUsersDao().getAllCachedGithubUsers()
        assert(githubUsers.filter { it.note == searchString } == searchedUser)
    }


    @After
    fun closeDb() {
        database.close()
    }
}
