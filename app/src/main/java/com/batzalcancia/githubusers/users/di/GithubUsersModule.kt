package com.batzalcancia.githubusers.users.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.room.Room
import com.batzalcancia.githubusers.users.data.GithubUsersRepositoryImpl
import com.batzalcancia.githubusers.users.data.local.GithubUsersLocalSource
import com.batzalcancia.githubusers.users.data.local.dao.GithubUsersDao
import com.batzalcancia.githubusers.users.data.local.database.GithubUsersDatabase
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.remote.GithubUsersApi
import com.batzalcancia.githubusers.users.data.remote.GithubUsersRemoteSource
import com.batzalcancia.githubusers.users.data.remote.GithubUsersRemoteSourceImpl
import com.batzalcancia.githubusers.users.domain.repositories.GithubUsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object GithubUsersModule {

    @Singleton
    @Provides
    fun providesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.createDataStore(
            name = "Github_Users_Data_Store"
        )

    @Singleton
    @Provides
    fun providesGithubUsersApi(retrofit: Retrofit) = retrofit.create(GithubUsersApi::class.java)

    @Singleton
    @Provides
    fun providesGithubUsersLocalSource(datastore: DataStore<Preferences>) =
        GithubUsersLocalSource(datastore)

    @Singleton
    @Provides
    fun providesGithubUsersRemoteSource(githubUsersApi: GithubUsersApi): GithubUsersRemoteSource =
        GithubUsersRemoteSourceImpl(githubUsersApi)

    @Singleton
    @Provides
    fun providesGithubUsersDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, GithubUsersDatabase::class.java, "github_users").build()

    @Singleton
    @Provides
    fun providesGithubUsersDao(database: GithubUsersDatabase) = database.getGithubUsersDao()

    @Singleton
    @Provides
    fun providesGithubUsersRepository(
        githubUsersRemoteSource: GithubUsersRemoteSource,
        githubUsersDao: GithubUsersDao,
        githubUsersLocalSource: GithubUsersLocalSource
    ): GithubUsersRepository =
        GithubUsersRepositoryImpl(githubUsersRemoteSource, githubUsersDao, githubUsersLocalSource)

}