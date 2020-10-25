package com.batzalcancia.githubusers.users.data.remote

import com.batzalcancia.githubusers.users.data.entities.GithubUser
import com.batzalcancia.githubusers.users.data.entities.GithubUserDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubUsersApi {

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users")
    fun getGithubUsers(@Query("since") since: Int): Call<List<GithubUser>>

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{username}")
    fun getGithubUserDetails(@Path("username") username: String): Call<GithubUserDetails>
}