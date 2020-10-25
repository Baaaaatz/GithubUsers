package com.batzalcancia.githubusers.users.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.batzalcancia.githubusers.users.data.local.dao.GithubUsersDao
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal

@Database(entities = [GithubUserLocal::class], version = 1)
abstract class GithubUsersDatabase : RoomDatabase(){
    abstract fun getGithubUsersDao(): GithubUsersDao
}