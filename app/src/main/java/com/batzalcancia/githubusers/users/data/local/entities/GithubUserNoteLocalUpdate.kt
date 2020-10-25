package com.batzalcancia.githubusers.users.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class GithubUserNoteLocalUpdate(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "note")
    val note: String
)