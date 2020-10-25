package com.batzalcancia.githubusers.core.utils.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewBindingViewHolder<VB : ViewBinding>(
    val viewBinding: VB
) : RecyclerView.ViewHolder(viewBinding.root)