package com.batzalcancia.githubusers.core.helpers

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.batzalcancia.githubusers.R
import java.io.File

fun ImageView.loadImageFromUrl(
    url: String,
    errorImage: Int = R.drawable.svg_default_photo,
    fromCache: Boolean = true,
    isCircle: Boolean = true,
    onError: () -> Unit = {},
    onSuccess: () -> Unit = {}
) = load(url) {
    if (!fromCache) {
        diskCachePolicy(CachePolicy.WRITE_ONLY)
        memoryCachePolicy(CachePolicy.WRITE_ONLY)
    }

    val imageLoader = CircularProgressDrawable(context)
    imageLoader.setStyle(CircularProgressDrawable.DEFAULT)
    placeholder(imageLoader)
    error(errorImage)
    if (isCircle) {
        transformations(CircleCropTransformation())
    }

    crossfade(true)
    listener(
        onError = { _, _ ->
            onError()
        },
        onSuccess = { _, _ ->
            onSuccess()
        }
    )
}

fun ImageView.loadImageFromFile(file: File, isCircle: Boolean = true) {
    load(file) {
        val imageLoader = CircularProgressDrawable(context)
        imageLoader.setStyle(CircularProgressDrawable.DEFAULT)
        placeholder(imageLoader)
        if (isCircle) {
            transformations(CircleCropTransformation())
        }
        crossfade(true)
    }
}

fun ImageView.loadImageFromDrawable(drawable: Int, isCircle: Boolean = true) {
    load(drawable) {
        val imageLoader = CircularProgressDrawable(context)
        imageLoader.setStyle(CircularProgressDrawable.DEFAULT)
        placeholder(imageLoader)
//        error(R.drawable.image_error_image)
        if (isCircle) {
            transformations(CircleCropTransformation())
        }
        crossfade(true)
    }
}
