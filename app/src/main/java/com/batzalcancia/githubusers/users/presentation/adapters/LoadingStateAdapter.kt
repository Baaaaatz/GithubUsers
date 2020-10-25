package com.batzalcancia.githubusers.users.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.utils.recyclerview.ViewBindingViewHolder
import com.batzalcancia.githubusers.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val setupBinding: (View) -> ItemLoadingBinding) :
    LoadStateAdapter<ViewBindingViewHolder<ItemLoadingBinding>>() {
    override fun onBindViewHolder(
        holder: ViewBindingViewHolder<ItemLoadingBinding>,
        loadState: LoadState
    ) {
        holder.viewBinding.prgGithubUsers.isVisible = loadState == LoadState.Loading
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewBindingViewHolder<ItemLoadingBinding> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
        return ViewBindingViewHolder(setupBinding(view))
    }

}