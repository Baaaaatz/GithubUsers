package com.batzalcancia.githubusers.core.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.batzalcancia.githubusers.databinding.ViewEmptyBinding

class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var viewBinding: ViewEmptyBinding = ViewEmptyBinding.inflate(
        LayoutInflater.from(context), this
    )

    init {
        viewBinding
    }

}