package com.batzalcancia.githubusers.core.helpers

import com.batzalcancia.githubusers.core.exceptions.NoConnectionException
import com.batzalcancia.githubusers.core.exceptions.TimeoutException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> httpCall(action: () -> Call<T>) = suspendCoroutine<T> {
    action().enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            response.body()?.let { resp ->
                it.resume(resp)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            when (t) {
                is UnknownHostException -> it.resumeWithException(NoConnectionException())
                is SocketTimeoutException -> it.resumeWithException(TimeoutException())
                else -> it.resumeWithException(Exception("Oops, something went wrong."))
            }
        }
    })
}