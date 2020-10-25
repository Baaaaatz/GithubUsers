package com.batzalcancia.githubusers.users.presentation.userdetails

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.batzalcancia.githubusers.core.helpers.offerEvent
import com.batzalcancia.githubusers.core.presentation.enums.UiState
import com.batzalcancia.githubusers.core.utils.Event
import com.batzalcancia.githubusers.users.data.entities.GithubUserDetails
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.domain.usecases.GetGithubUsersDetails
import com.batzalcancia.githubusers.users.domain.usecases.UpdateNote
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUserDetailsViewModel @ViewModelInject constructor(
    private val getGithubUsersDetails: GetGithubUsersDetails,
    private val updateNote: UpdateNote
) : ViewModel() {

    private val _userDetailsState = ConflatedBroadcastChannel<UiState>()
    val userDetailsState = _userDetailsState.asFlow()

    private val _updateNoteState = ConflatedBroadcastChannel<Event<UiState>>()
    val updateNoteState = _updateNoteState.asFlow()

    val note = ConflatedBroadcastChannel<String>()

    private val originalNote = note.valueOrNull

    val noteHasError = note
        .asFlow()
        .mapLatest {
            it == originalNote
        }

    private val _userDetails = ConflatedBroadcastChannel<GithubUserLocal>()
    val userDetails = _userDetails.asFlow()

    fun onLoad(username: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _userDetailsState.offer(UiState.Error(throwable))
        }) {
            _userDetailsState.offer(UiState.Loading)
            _userDetails.offer(getGithubUsersDetails(username))
            _userDetailsState.offer(UiState.Complete)
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _updateNoteState.offerEvent(UiState.Error(throwable))
        }) {
            _updateNoteState.offerEvent(UiState.Loading)
            updateNote(userDetails.first().id, note.value)
            _updateNoteState.offerEvent(UiState.Complete)
        }
    }

}