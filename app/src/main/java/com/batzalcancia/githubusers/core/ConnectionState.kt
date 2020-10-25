package com.batzalcancia.githubusers.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.batzalcancia.githubusers.ConnectionStateBus
import kotlinx.coroutines.*
import javax.inject.Inject


class ConnectionState @Inject constructor(private val context: Context) : NetworkCallback() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun register() {
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        scope.launch {
            ConnectionStateBus.send(true)
        }
    }

    override fun onLost(network: Network) {
        scope.launch {
            ConnectionStateBus.send(false)
        }
    }

    fun cancelScope() {
        scope.cancel()
    }

}