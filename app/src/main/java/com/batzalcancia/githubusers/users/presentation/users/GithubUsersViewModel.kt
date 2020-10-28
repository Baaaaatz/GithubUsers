package com.batzalcancia.githubusers.users.presentation.users

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*
import com.batzalcancia.githubusers.core.helpers.offerEvent
import com.batzalcancia.githubusers.core.presentation.enums.UiState
import com.batzalcancia.githubusers.core.utils.Event
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.data.paging.GithubUsersRemoteMediator
import com.batzalcancia.githubusers.users.domain.usecases.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUsersViewModel @ViewModelInject constructor(
    private val getLocalGithubUsers: GetLocalGithubUsers,
    private val searchGithubUser: SearchGithubUser,
    private val getCachedGithubUsers: GetCachedGithubUsers,
    private val getGithubUsers: GetGithubUsers,
    private val insertAllGithubUsers: InsertAllGithubUsers,
    private val getUsersNextKey: GetUsersNextKey,
    private val setUsersNextKey: SetUsersNextKey
) : ViewModel() {

    private val _searchGithubUsersState = ConflatedBroadcastChannel<Event<UiState>>()
    val searchGithubUsersState = _searchGithubUsersState.asFlow()

    private val _searchGithubUsers = ConflatedBroadcastChannel<List<GithubUserLocal>>()
    val searchGithubUsers = _searchGithubUsers.asFlow()

    private val _cachedGithubUsersState = ConflatedBroadcastChannel<Event<UiState>>()
    val cachedGithubUsersState = _cachedGithubUsersState.asFlow()

    private val _cachedGithubUsers = ConflatedBroadcastChannel<List<GithubUserLocal>>()
    val cachedGithubUsers = _cachedGithubUsers.asFlow()

    val searchString = ConflatedBroadcastChannel<String>()

    init {
        // Detects offers to searchString Channel to be observed
        // And offer UiStates accordingly
        searchString
            .asFlow()
            .mapLatest {
                // Sequentially sets UiStates for the ui to observe
                _searchGithubUsersState.offerEvent(UiState.Loading)
                val searchedUsers = searchGithubUser(it)
                // Offers searched to a channel to be observed by the ui
                _searchGithubUsers.offer(searchedUsers)
                _searchGithubUsersState.offerEvent(UiState.Complete)
                if (searchedUsers.isEmpty()) _searchGithubUsersState.offerEvent(UiState.Empty)
            }.catch {
                //Catch an error inside a coroutineScope and offer it to a Channel
                _searchGithubUsersState.offerEvent(UiState.Error(it))
            }.launchIn(viewModelScope)
    }

    fun executeGetCachedGithubUsers() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            //Catch an error inside a coroutineScope and offer it to a Channel
            _cachedGithubUsersState.offerEvent(UiState.Error(throwable))
        }) {
            // Sequentially sets UiStates for the ui to observe
            _cachedGithubUsersState.offerEvent(UiState.Loading)
            val cachedUsers = getCachedGithubUsers()
            // Offers cachedUsers to a channel to be observed by the ui
            _cachedGithubUsers.offer(cachedUsers)
            _cachedGithubUsersState.offerEvent(UiState.Complete)
            if (cachedUsers.isEmpty()) _searchGithubUsersState.offerEvent(UiState.Empty)
        }

    }

    // Remote Mediator orchestrates the data flow of the paging which includes
    // Getting data from local database (room) to present into the UI
    // When local database has no items left the Remote Mediator will
    // Call the remote API to add more items in the local database (room)
    var githubUsersPagingData =
        Pager(
            PagingConfig(30, enablePlaceholders = false, initialLoadSize = 30),
            initialKey = 0,
            remoteMediator = GithubUsersRemoteMediator(
                getGithubUsers,
                insertAllGithubUsers,
                getUsersNextKey,
                setUsersNextKey
            ),
            pagingSourceFactory = { getLocalGithubUsers() }
        ).flow.cachedIn(viewModelScope)

    fun refreshList() {
        githubUsersPagingData =
            Pager(
                PagingConfig(30, enablePlaceholders = false, initialLoadSize = 30),
                initialKey = 0,
                remoteMediator = GithubUsersRemoteMediator(
                    getGithubUsers,
                    insertAllGithubUsers,
                    getUsersNextKey,
                    setUsersNextKey
                ),
                pagingSourceFactory = { getLocalGithubUsers() }
            ).flow.cachedIn(viewModelScope)
    }

}