package com.batzalcancia.githubusers.users.presentation.users

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.batzalcancia.githubusers.core.helpers.offerEvent
import com.batzalcancia.githubusers.core.presentation.enums.UiState
import com.batzalcancia.githubusers.core.utils.Event
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.paging.GithubUsersRemoteMediator
import com.batzalcancia.githubusers.users.domain.usecases.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUsersViewModel @ViewModelInject constructor(
    private val getLocalGithubUsers: GetLocalGithubUsers,
    private val searchGithubUser: SearchGithubUser,
    getGithubUsers: GetGithubUsers,
    insertAllGithubUsers: InsertAllGithubUsers,
    getUsersNextKey: GetUsersNextKey,
    setUsersNextKey: SetUsersNextKey,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchGithubUsersState = ConflatedBroadcastChannel<Event<UiState>>()
    val searchGithubUsersState = _searchGithubUsersState.asFlow()

    private val _searchGithubUsers = ConflatedBroadcastChannel<PagingData<GithubUserLocal>>()
    val searchGithubUsers = _searchGithubUsers.asFlow()

    val editString = ConflatedBroadcastChannel<String>()

    init {
        editString
            .asFlow()
            .mapLatest {
                _searchGithubUsersState.offerEvent(UiState.Loading)
                val searchedUsers = searchGithubUser(it)
                _searchGithubUsers.offer(PagingData.from(searchedUsers))
                _searchGithubUsersState.offerEvent(UiState.Complete)
                if (searchedUsers.isEmpty()) _searchGithubUsersState.offerEvent(UiState.Empty)
            }.catch {
                _searchGithubUsersState.offerEvent(UiState.Error(it))
            }.launchIn(viewModelScope)
    }

    private val remoteMediator = GithubUsersRemoteMediator(
        getGithubUsers,
        insertAllGithubUsers,
        getUsersNextKey,
        setUsersNextKey
    )

    val githubUsersPagingData =
        Pager(
            PagingConfig(30, enablePlaceholders = false, initialLoadSize = 30),
            initialKey = 0,
            remoteMediator = remoteMediator,
            pagingSourceFactory = { getLocalGithubUsers() }
        ).flow.cachedIn(viewModelScope)

}