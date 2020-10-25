package com.batzalcancia.githubusers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.batzalcancia.githubusers.core.ConnectionState
import com.batzalcancia.githubusers.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityMainBinding

    @Inject
    lateinit var connectionState: ConnectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        connectionState.register()

        when (android.os.Build.VERSION.SDK_INT) {
            in android.os.Build.VERSION_CODES.R..Int.MAX_VALUE
            -> window.setDecorFitsSystemWindows(false)
            else
            -> viewBinding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    // For UI to go beyond the navigation bar
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    // For UI to go beyond the status bar
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        connectionState.cancelScope()
        connectionState.unregister()
    }
}

