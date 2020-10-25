package com.batzalcancia.githubusers.users.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocalUpdate
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserNoteLocalUpdate

@Dao
interface GithubUsersDao {

    @Query("Select * FROM githubUsers")
    fun getAllGithubUsers(): PagingSource<Int, GithubUserLocal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllUsers(githubUsers: List<GithubUserLocal>)

    @Update(entity = GithubUserLocal::class)
    fun updateNote(githubUserNoteLocalUpdate: GithubUserNoteLocalUpdate)

    @Query("SELECT * FROM githubUsers WHERE username = :username LIMIT 1")
    suspend fun getGithubUser(username: String): GithubUserLocal

    @Update(entity = GithubUserLocal::class)
    fun updateGithubUser(githubUserLocalUpdate: GithubUserLocalUpdate)

    @Query("SELECT * FROM githubUsers WHERE username LIKE  '%' ||  :searchString || '%' OR note LIKE  '%' || :searchString || '%'")
    fun searchGithubUser(searchString: String): List<GithubUserLocal>
}