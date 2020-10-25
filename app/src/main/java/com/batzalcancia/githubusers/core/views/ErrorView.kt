package com.batzalcancia.githubusers.core.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.batzalcancia.githubusers.R
import com.batzalcancia.githubusers.core.exceptions.EmptyListException
import com.batzalcancia.githubusers.core.exceptions.NoConnectionException
import com.batzalcancia.githubusers.databinding.ViewErrorBinding

class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var viewBinding: ViewErrorBinding = ViewErrorBinding.inflate(
        LayoutInflater.from(context), this
    )

    var throwable: Throwable? = null
        set(value) {
            field = value
            viewBinding.imgError.load(
                when (value) {
                    is NoConnectionException -> R.drawable.svg_no_connection
                    is EmptyListException -> R.drawable.svg_empty
                    else -> R.drawable.svg_error
                }

            )
            viewBinding.txtErrorMessage.text =
                value?.localizedMessage ?: context.getString(R.string.generic_error_message)
        }

    var onRetryClicked: (() -> Unit)? = null

    init {
        viewBinding.root.setOnClickListener {
            onRetryClicked?.invoke()
        }
    }
}