package com.batzalcancia.githubusers.core.helpers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Observe textChanges in editText
 **/

@ExperimentalCoroutinesApi
fun EditText.textChangesFlow() = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            // Offers a new string to the scope everytime afterTextChanged is called
            // to be used as flow for it to be observed.
            offer(p0.toString().trim())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    //Adds the listener to the EditText
    addTextChangedListener(listener)

    // Removes the listener to prevent further leak
    awaitClose {
        removeTextChangedListener(listener)
    }
}
