package com.batzalcancia.githubusers.users.presentation.users

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.transition.TransitionInflater
import com.batzalcancia.githubusers.ConnectionStateBus
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.exceptions.EmptyListException
import com.batzalcancia.githubusers.core.exceptions.NoConnectionException
import com.batzalcancia.githubusers.core.helpers.prepareReturnTransition
import com.batzalcancia.githubusers.core.helpers.px
import com.batzalcancia.githubusers.core.helpers.textChangesFlow
import com.batzalcancia.githubusers.core.presentation.enums.UiState
import com.batzalcancia.githubusers.core.utils.EventObserver
import com.batzalcancia.githubusers.databinding.FragmentGithubUsersBinding
import com.batzalcancia.githubusers.databinding.ItemGithubUserBinding
import com.batzalcancia.githubusers.databinding.ItemLoadingBinding
import com.batzalcancia.githubusers.users.presentation.adapters.GithubUsersAdapter
import com.batzalcancia.githubusers.users.presentation.adapters.LoadingStateAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUsersFragment : Fragment(R.layout.fragment_github_users) {

    private lateinit var viewBinding: FragmentGithubUsersBinding

    private val viewModel: GithubUsersViewModel by viewModels()

    private val githubUsersAdapter by lazy {
        GithubUsersAdapter(ItemGithubUserBinding::bind)
    }

    private var isFirst = true

    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the sharedElementEnterTransition for sharedElements can have an animation
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentGithubUsersBinding.bind(view)

        viewBinding.shmGithubUsers.startShimmer()

        // Sets insets for the to use end to end design
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.apbMain) { v, insets ->
            v.updatePadding(top = 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.tlbMain) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }

        val bottom = viewBinding.rcvGithubUsers.bottom
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.rcvGithubUsers) { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom + bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.shmGithubUsers) { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }

        viewBinding.rcvGithubUsers.adapter = githubUsersAdapter.withLoadStateFooter(
            //To show loading on bottom of list while fetching next page
            footer = LoadingStateAdapter(ItemLoadingBinding::bind)
        )

        viewBinding.edtSearch.textChangesFlow()
            .debounce(300)
            //To not refresh popbackstack from details
            .drop(1)
            .onEach {
                //When search string is empty call remote mediator to get data from network
                if (it.isEmpty()) {
                    fetchGithubUsers()
                    githubUsersAdapter.refresh()
                } else {
                    //Offer searchString to viewmodel to search accordingly
                    viewModel.searchString.offer(it)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewBinding.rcvGithubUsers.prepareReturnTransition(this)

        setupOutputs()
    }

    private fun setupOutputs() {
        fetchGithubUsers()

        //State listener for adapter
        githubUsersAdapter.addLoadStateListener {
            // Set states for UI
            viewBinding.shmGithubUsers.isVisible = it.refresh is LoadState.Loading
            viewBinding.rcvGithubUsers.isVisible =
                it.refresh is LoadState.NotLoading && it.refresh !is LoadState.Error

            if (it.refresh is LoadState.Error) {
                if ((it.refresh as LoadState.Error).error is NoConnectionException) {
                    viewModel.executeGetCachedGithubUsers()
                } else {
                    viewBinding.viewError.throwable = (it.refresh as LoadState.Error).error
                    viewBinding.viewError.onRetryClicked = {
                        viewBinding.viewError.isVisible = false
                        // App will retry to fetch users from remote
                        fetchGithubUsers()
                    }
                }
            }
        }

        viewModel.cachedGithubUsersState.asLiveData().observe(viewLifecycleOwner, EventObserver {
            //Set Ui State when getting Cached GithubUsers
            viewBinding.shmGithubUsers.isVisible = it == UiState.Loading
            viewBinding.rcvGithubUsers.isVisible = it == UiState.Complete
            when (it) {
                is UiState.Error -> {
                    // Setup the Error View when State is Error
                    viewBinding.viewError.throwable = it.throwable
                    viewBinding.viewError.onRetryClicked = {
                        viewBinding.viewError.isVisible = false
                        // App will retry to fetch users from remote
                        fetchGithubUsers()
                    }
                }
                is UiState.Empty -> {
                    viewBinding.viewError.throwable = EmptyListException()
                }
            }
        })

        viewModel.cachedGithubUsers.onEach {
            // Transforms the list into PagingData for it to be submitted to PagingDataAdapter
            githubUsersAdapter.submitData(PagingData.from(it))
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchGithubUsers.onEach {
            // Transforms the list into PagingData for it to be submitted to PagingDataAdapter
            githubUsersAdapter.submitData(PagingData.from(it))
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchGithubUsersState.asLiveData().observe(viewLifecycleOwner, EventObserver {
            //Set Ui State when getting search results in Cached GithubUsers
            viewBinding.shmGithubUsers.isVisible = it is UiState.Loading
            viewBinding.viewError.isVisible = it is UiState.Empty
        })

        ConnectionStateBus.on().onEach {
            if (it) {
                // Refreshes list upon detecting a connection
                fetchGithubUsers()
            } else {
                if (::snackbar.isInitialized) snackbar.dismiss()
                snackbar = Snackbar.make(
                    requireView(),
                    getString(R.string.no_connection_detected),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(android.R.string.ok)) { }.apply { show() }
                viewModel.executeGetCachedGithubUsers()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    private fun fetchGithubUsers() {
        //refreshes list on fetch
        githubUsersAdapter.refresh()
        viewModel.githubUsersPagingData.onEach {
            githubUsersAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

}