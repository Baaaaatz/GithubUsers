package com.batzalcancia.githubusers.users.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GithubUserLocalDetailsUpdate(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "company")
    val company: String,
    @ColumnInfo(name = "blog")
    val blog: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "public_repos")
    val publicRepos: Int,
    @ColumnInfo(name = "public_gists")
    val publicGists: Int,
    @ColumnInfo(name = "followers")
    val followers: Int,
    @ColumnInfo(name = "following")
    val following: Int,
    @ColumnInfo(name = "saved_locally")
    val savedLocally: Boolean = true
)