package com.batzalcancia.githubusers.core.helpers

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.batzalcancia.githubusers.R
import com.google.android.material.transition.MaterialContainerTransform

fun containerTransition() = MaterialContainerTransform().apply {
    duration = 500
    containerColor = Color.WHITE
    interpolator = FastOutSlowInInterpolator()
    fadeMode = MaterialContainerTransform.FADE_MODE_IN
}

fun View.setContainerTransition(identifier: String) {
    ViewCompat.setTransitionName(
        this,
        context.getString(
            R.string.container_transition_name,
            identifier
        )
    )
}

fun View.prepareReturnTransition(fragment: Fragment) {
    fragment.postponeEnterTransition()
    viewTreeObserver.addOnPreDrawListener {
        fragment.startPostponedEnterTransition()
        true
    }
}