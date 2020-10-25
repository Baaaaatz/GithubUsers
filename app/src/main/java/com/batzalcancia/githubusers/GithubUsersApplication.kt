package com.batzalcancia.githubusers

import android.app.Application
import com.batzalcancia.githubusers.core.helpers.githubUsersContext
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GithubUsersApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        githubUsersContext = this.applicationContext
    }
}