package com.batzalcancia.githubusers.users.presentation.userdetails

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.exceptions.NoConnectionException
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
import java.net.UnknownHostException

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GithubUserDetailsFragment : Fragment(R.layout.fragment_github_user_details) {

    private lateinit var viewBinding: FragmentGithubUserDetailsBinding

    private val viewModel: GithubUserDetailsViewModel by viewModels()

    private val githubUserDetailsFragmentArgs by navArgs<GithubUserDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val githubDetails = githubUserDetailsFragmentArgs.githubUser.fromJson<GithubUserLocal>()
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
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
        viewBinding.tlbMain.setupWithNavController(findNavController())
        viewBinding.imgUserImage.setContainerTransition(
            getString(
                R.string.transition_image,
                githubDetails.image
            )
        )
        viewBinding.txtUsername.setContainerTransition(
            getString(
                R.string.transition_username,
                githubDetails.username
            )
        )

        viewBinding.edtNote.setText(githubDetails.note)

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.tlbMain) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.nsvGithubUserDetails) { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.ctlGithubUserDetails) { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        viewBinding.imgUserImage.post {
            viewBinding.imgUserImage.loadImageFromUrl(
                githubDetails.image,
                isCircle = false
            )
        }

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

        viewBinding.root.requestApplyInsets()

        setupOutputs()
    }

    private fun setupOutputs() {
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

            viewBinding.edtNote.isEnabled = it != UiState.Loading
            viewBinding.tilNote.isVisible = it != UiState.Loading
            viewBinding.btnSaveNote.isVisible = it != UiState.Loading
            viewBinding.viewError.isVisible = (it is UiState.Error)
            viewBinding.grpShimmer.isVisible = it == UiState.Loading
            viewBinding.shmDetails.isVisible = it == UiState.Loading
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.noteHasError.onEach {
            viewBinding.btnSaveNote.isEnabled = !it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.updateNoteState.asLiveData().observe(viewLifecycleOwner, EventObserver {
            viewBinding.btnSaveNote.isInvisible = it == UiState.Loading
            viewBinding.prgSaveNote.isVisible = it == UiState.Loading

            if (it == UiState.Complete) {
                Snackbar.make(
                    requireView(),
                    "Note about this user is saved!",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (it is UiState.Error) {
                Snackbar.make(
                    requireView(),
                    it.throwable.localizedMessage ?: getString(R.string.generic_error_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        viewLifecycleOwner.lifecycleScope.detectConnection({
            Snackbar.make(
                requireView(),
                getString(R.string.connectin_connection_detected),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.label_connect)) {
                viewModel.onLoad(githubUserDetailsFragmentArgs.githubUser.fromJson<GithubUserLocal>().username)
            }.show()
        }, {
            Snackbar.make(
                requireView(),
                "Connection Lost.",
                Snackbar.LENGTH_LONG
            ).show()
        })
    }

    private fun startShimmer() {
        viewBinding.shmDetails.startShimmer()
        viewBinding.shmFollowers.startShimmer()
        viewBinding.shmFollowing.startShimmer()
    }

}