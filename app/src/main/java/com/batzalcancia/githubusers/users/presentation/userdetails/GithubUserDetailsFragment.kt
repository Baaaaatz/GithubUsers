package com.batzalcancia.githubusers.users.presentation.userdetails

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.batzalcancia.githubusers.ConnectionStateBus
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.helpers.*
import com.batzalcancia.githubusers.core.presentation.enums.UiState
import com.batzalcancia.githubusers.core.utils.EventObserver
import com.batzalcancia.githubusers.databinding.FragmentGithubUserDetailsBinding
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUserDetailsFragment : Fragment(R.layout.fragment_github_user_details) {

    private lateinit var viewBinding: FragmentGithubUserDetailsBinding

    private val viewModel: GithubUserDetailsViewModel by viewModels()

    // Instantiate navArgs from previous fragment
    private val githubUserDetailsFragmentArgs by navArgs<GithubUserDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val githubDetails = githubUserDetailsFragmentArgs.githubUser.fromJson<GithubUserLocal>()
        // Set the sharedElementEnterTransition for sharedElements can have an animation
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
                // Added a listener on the transition to make better effects of
                // Showing loading after the animation
                .addListener(object : Transition.TransitionListener {
                    override fun onTransitionStart(transition: Transition) {
                        startShimmer()
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        viewBinding.grpShimmer.isVisible = true
                        viewBinding.crdDetails.isVisible = true
                        viewBinding.grpShimmer.animate().alpha(1f)
                        viewModel.onLoad(githubDetails.username)
                    }

                    override fun onTransitionCancel(transition: Transition) = Unit
                    override fun onTransitionPause(transition: Transition) = Unit
                    override fun onTransitionResume(transition: Transition) = Unit
                })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val githubDetails = githubUserDetailsFragmentArgs.githubUser.fromJson<GithubUserLocal>()
        viewBinding = FragmentGithubUserDetailsBinding.bind(view)
        // Sets Fragment with navigation controller for backstacks
        viewBinding.tlbMain.setupWithNavController(findNavController())

        // sets imgUserImage's transitionName
        // transitionName is Dynamic because its from a recyclerview
        viewBinding.imgUserImage.setContainerTransition(
            getString(
                R.string.transition_image,
                githubDetails.image
            )
        )

        // sets imgUserImage's transitionName
        // transitionName is Dynamic because its from a recyclerview
        viewBinding.txtUsername.setContainerTransition(
            getString(
                R.string.transition_username,
                githubDetails.username
            )
        )

        viewBinding.edtNote.setText(githubDetails.note)
        viewModel.setOriginalNote(githubDetails.note ?: "")


        // Sets insets for the to use end to end design
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.tlbMain) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }

        val bottom = viewBinding.nsvGithubUserDetails.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.nsvGithubUserDetails) { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom + bottom)
            insets
        }

        viewBinding.imgUserImage.loadImageFromUrl(
            githubDetails.image,
            isCircle = false
        )

        viewBinding.tlbMain.post {
            viewBinding.txtUsername.text = githubDetails.username
        }

        viewBinding.edtNote.textChangesFlow()
            .debounce(300)
            .onEach { viewModel.note.offer(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewBinding.btnSaveNote.setOnClickListener {
            viewModel.onSaveClicked()
        }


        viewBinding.viewError.onRetryClicked = {
            viewModel.onLoad(githubDetails.username)
        }

        setupOutputs()
    }

    private fun setupOutputs() {
        // Observes userDetails from local
        // The data is from local because it is saved to local first before emitting the data
        viewModel.userDetails.onEach {
            viewBinding.txtFollowers.text = getString(R.string.label_followers, it.followers)
            viewBinding.txtFollowing.text = getString(R.string.label_following, it.following)
            val details = listOf(
                getString(R.string.label_name) to it.username,
                getString(R.string.label_company) to it.company,
                getString(R.string.label_blog) to it.blog,
                getString(R.string.label_location) to it.location,
                getString(R.string.label_public_gists) to it.publicGists.toString(),
                getString(R.string.label_public_repos) to it.publicRepos.toString()
            )
            viewBinding.txtDetails.text = details.joinToString("\n") { pairDetails ->
                "${pairDetails.first}: ${pairDetails.second}"
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)


        // Set states for UI
        viewModel.userDetailsState.onEach {
            if (it == UiState.Loading) {
                startShimmer()
            }

            if (it != UiState.Loading) {
                viewBinding.txtFollowers.animate().alpha(1f)
                viewBinding.txtFollowers.animate().alpha(1f)
                viewBinding.txtFollowing.animate().alpha(1f)
                viewBinding.txtDetails.animate().alpha(1f)
                viewBinding.tilNote.animate().alpha(1f)
                viewBinding.btnSaveNote.animate().alpha(1f)

                viewBinding.shmFollowers.stopShimmer()
                viewBinding.shmFollowing.stopShimmer()
                viewBinding.shmDetails.stopShimmer()
            }
            if (it is UiState.Error) {
                viewBinding.viewError.throwable = it.throwable
            }

            // Show or hide ui depending on what state it is
            viewBinding.edtNote.isEnabled = it != UiState.Loading
            viewBinding.tilNote.isVisible = it != UiState.Loading
            viewBinding.btnSaveNote.isVisible = it != UiState.Loading
            viewBinding.viewError.isVisible = (it is UiState.Error)
            viewBinding.grpShimmer.isVisible = it == UiState.Loading
            viewBinding.shmDetails.isVisible = it == UiState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        // observes if the has error if not the save button will enable
        viewModel.noteHasError.onEach {
            viewBinding.btnSaveNote.isEnabled = !it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.updateNoteState.asLiveData().observe(viewLifecycleOwner, EventObserver {
            viewBinding.btnSaveNote.isInvisible = it == UiState.Loading
            viewBinding.prgSaveNote.isVisible = it == UiState.Loading

            if (it == UiState.Complete) {
                // show A snackbar to tell the user that the app saved the note for the user
                Snackbar.make(
                    requireView(),
                    "Note about this user is saved!",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (it is UiState.Error) {
                // show A snackbar to tell the user that the app
                // failed to save the note for the user
                Snackbar.make(
                    requireView(),
                    it.throwable.localizedMessage ?: getString(R.string.generic_error_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        ConnectionStateBus.on().onEach {
            if (it) {
                // Refreshes the page when app detects a connection
                viewModel.onLoad(githubUserDetailsFragmentArgs.githubUser.fromJson<GithubUserLocal>().username)
            } else {
                // show A snackbar to tell the user that the app detects a disconnection
                Snackbar.make(
                    requireView(),
                    "Connection Lost.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(android.R.string.ok)) { }.apply { show() }
            }
        }
    }

    private fun startShimmer() {
        viewBinding.shmDetails.startShimmer()
        viewBinding.shmFollowers.startShimmer()
        viewBinding.shmFollowing.startShimmer()
    }

}