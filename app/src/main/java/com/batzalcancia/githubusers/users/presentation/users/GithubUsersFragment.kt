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
import androidx.transition.TransitionInflater
import com.batzalcancia.githubusers.ConnectionStateBus
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.helpers.detectConnection
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentGithubUsersBinding.bind(view)

        viewBinding.shmGithubUsers.startShimmer()

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

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.rcvGithubUsers) { v, insets ->
            v.updatePadding(bottom = 20.px(requireContext()))
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
                    viewModel.editString.offer(it)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewBinding.rcvGithubUsers.prepareReturnTransition(this)

        setupOutputs()
    }

    private fun setupOutputs() {
        fetchGithubUsers()

        //To show loading shimmer on start if loading
        githubUsersAdapter.addLoadStateListener {
            viewBinding.shmGithubUsers.isVisible = it.refresh is LoadState.Loading
            viewBinding.rcvGithubUsers.isVisible =
                it.refresh is LoadState.NotLoading && it.refresh !is LoadState.Error

            viewBinding.viewError.isVisible = it.refresh is LoadState.Error

            if (it.refresh is LoadState.Error) {
                viewBinding.viewError.throwable = (it.refresh as LoadState.Error).error
                viewBinding.viewError.onRetryClicked = {
                    githubUsersAdapter.refresh()
                    fetchGithubUsers()
                }
            }
        }

        viewModel.searchGithubUsers.onEach {
            githubUsersAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.searchGithubUsersState.asLiveData().observe(viewLifecycleOwner, EventObserver {
            viewBinding.shmGithubUsers.isVisible = it is UiState.Loading
            viewBinding.viewError.isVisible = it is UiState.Empty
        })

        viewLifecycleOwner.lifecycleScope.detectConnection({
            Snackbar.make(
                requireView(),
                getString(R.string.connectin_connection_detected),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.label_connect)) {
                githubUsersAdapter.refresh()
                fetchGithubUsers()
            }.show()
        }, {
            viewModel.editString.offer(viewBinding.edtSearch.text.toString())
        })

    }

    private fun fetchGithubUsers() {
        viewModel.githubUsersPagingData.onEach {
            githubUsersAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

}