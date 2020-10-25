package com.batzalcancia.githubusers.testutils

import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal

class GithubUsersFactory {
    companion object Factory {
        private fun makeGithubUserLocal(id: Int): GithubUserLocal {
            return GithubUserLocal(
                id = id,
                username = "username$id",
                image = "image$id",
                type = "type$id",
                note = "note$id",
                company = "company$id",
                blog = "blog$id",
                location = "location$id",
                publicRepos = id,
                publicGists = id,
                followers = id,
                following = id,
                savedLocally = false
            )
        }

        fun makeGithubUserLocalList(count: Int): List<GithubUserLocal> {
            val githubUserLocal = mutableListOf<GithubUserLocal>()
            repeat(count) {
                githubUserLocal.add(makeGithubUserLocal(it))
            }
            return githubUserLocal
        }
    }
}



