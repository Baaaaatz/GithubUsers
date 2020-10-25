package com.batzalcancia.githubusers.testutils

import com.batzalcancia.githubusers.users.data.entities.GithubUser

/**
 * Copyright 2020, White Cloak Technologies, Inc., All rights reserved.
 *
 * @author Batz Alcancia
 * @since 10/25/20
 */

val users = mutableListOf<GithubUser>().apply {
    repeat(30) {
        add(
            GithubUser(
                id = it,
                username = "username$it",
                image = "image$it",
                type = "type$it"
            )
        )
    }
}