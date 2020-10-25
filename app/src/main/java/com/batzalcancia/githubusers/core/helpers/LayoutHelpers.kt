package com.batzalcancia.githubusers.core.helpers

import android.content.Context
import android.view.View

fun Int.dp(context: Context): Int = (this / context.resources.displayMetrics.density).toInt()
fun Int.px(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

fun View.requestApplyInsetsWhenAttached() {
    // https://chris.banes.dev/2019/04/12/insets-listeners-to-layouts/

    if (isAttachedToWindow) {
        // We're already attached, just request as normal

        requestApplyInsets()


    } else {
        // We're not attached to the hierarchy, add a listener to request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}