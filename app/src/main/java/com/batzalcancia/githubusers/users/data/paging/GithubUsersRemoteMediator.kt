package com.batzalcancia.githubusers.users.data.paging

import android.util.Log
import androidx.paging.*
import com.batzalcancia.githubusers.core.exceptions.EmptyListException
import com.batzalcancia.githubusers.users.data.entities.GithubUser
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.domain.usecases.*
import retrofit2.Response
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class GithubUsersRemoteMediator @Inject constructor(
    private val getGithubUsers: GetGithubUsers,
    private val insertAllGithubUsers: InsertAllGithubUsers,
    private val getUsersNextKey: GetUsersNextKey,
    private val setUsersNextKey: SetUsersNextKey
) : RemoteMediator<Int, GithubUserLocal>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GithubUserLocal>
    ): MediatorResult {
        return try {
            val since = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    getUsersNextKey()
                }
            } ?: 0

            val users = getGithubUsers(since)

            //insert github users from remote to local and update next key
            if (users.isNotEmpty()) {
                setUsersNextKey(
                    users.last().id
                )
                insertAllGithubUsers(users.map {
                    GithubUserLocal(it.id, it.username, it.image, it.type)
                })
            }

            if (state.isEmpty()) {
                MediatorResult.Error(EmptyListException())
            }

            MediatorResult.Success(endOfPaginationReached = users.isNullOrEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

}