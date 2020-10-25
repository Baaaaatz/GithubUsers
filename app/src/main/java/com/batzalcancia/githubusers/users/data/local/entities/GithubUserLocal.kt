package com.batzalcancia.githubusers.users.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "githubUsers")
@Serializable
data class GithubUserLocal(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "company")
    val company: String? = null,
    @ColumnInfo(name = "blog")
    val blog: String? = null,
    @ColumnInfo(name = "location")
    val location: String? = null,
    @ColumnInfo(name = "public_repos")
    val publicRepos: Int? = null,
    @ColumnInfo(name = "public_gists")
    val publicGists: Int? = null,
    @ColumnInfo(name = "followers")
    val followers: Int? = null,
    @ColumnInfo(name = "following")
    val following: Int? = null,
    @ColumnInfo(name = "saved_locally")
    val savedLocally: Boolean = false
)