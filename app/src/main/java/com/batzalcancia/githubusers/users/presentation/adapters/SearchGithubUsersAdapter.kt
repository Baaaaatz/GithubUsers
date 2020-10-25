package com.batzalcancia.githubusers.users.presentation.adapters

import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.helpers.loadImageFromUrl
import com.batzalcancia.githubusers.core.helpers.setContainerTransition
import com.batzalcancia.githubusers.core.helpers.toJson
import com.batzalcancia.githubusers.core.utils.recyclerview.ViewBindingViewHolder
import com.batzalcancia.githubusers.databinding.ItemGithubUserBinding
import com.batzalcancia.githubusers.users.data.local.entities.GithubUserLocal
import com.batzalcancia.githubusers.users.presentation.users.GithubUsersFragmentDirections

class SearchGithubUsersAdapter(
    private val setupGithubUserBinding: (View) -> ItemGithubUserBinding
) : ListAdapter<GithubUserLocal, ViewBindingViewHolder<ItemGithubUserBinding>>(
    SearchGithubUsersDiffUtil
) {
    override fun onBindViewHolder(
        holder: ViewBindingViewHolder<ItemGithubUserBinding>,
        position: Int
    ) {

        val negative = floatArrayOf(
            -1.0f, .0f, .0f, .0f, 255.0f,
            .0f, -1.0f, .0f, .0f, 255.0f,
            .0f, .0f, -1.0f, .0f, 255.0f,
            .0f, .0f, .0f, 1.0f, .0f
        )

        val githubUser = getItem(position)

        holder.viewBinding.run {
            githubUser?.let {
                imgUserImage.colorFilter =
                    if ((position + 1) % 4 == 0) ColorMatrixColorFilter(negative) else null
                imgUserImage.loadImageFromUrl(it.image)
                txtUsername.text = it.username
                txtDetails.text = it.type
                imgNote.isVisible = !it.note.isNullOrEmpty()

                imgUserImage.setContainerTransition(
                    imgUserImage.context.getString(
                        R.string.transition_image,
                        it.image
                    )
                )
                txtUsername.setContainerTransition(
                    txtUsername.context.getString(
                        R.string.transition_username,
                        it.username
                    )
                )

                root.setOnClickListener { view ->
                    view.findNavController()
                        .navigate(
                            GithubUsersFragmentDirections.actionGithubUsersToGithubUserDetails(
                                it.toJson()
                            ),
                            FragmentNavigatorExtras(
                                imgUserImage to imgUserImage.transitionName,
                                txtUsername to txtUsername.transitionName
                            )
                        )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingViewHolder<ItemGithubUserBinding> {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_github_user, parent, false)
        return ViewBindingViewHolder(setupGithubUserBinding(view))
    }

}

object SearchGithubUsersDiffUtil : DiffUtil.ItemCallback<GithubUserLocal>() {

    override fun areItemsTheSame(oldItem: GithubUserLocal, newItem: GithubUserLocal) =
        oldItem.username == newItem.username

    override fun areContentsTheSame(oldItem: GithubUserLocal, newItem: GithubUserLocal) =
        oldItem == newItem
}