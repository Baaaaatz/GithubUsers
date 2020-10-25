package com.batzalcancia.githubusers.users.data.local

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GithubUsersLocalSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val NEXT_KEY = preferencesKey<Int>("NEXT_KEY")

    suspend fun getNextKey() = dataStore.data.map {
        it[NEXT_KEY]
    }.first()

    suspend fun setNextKey(nextKey: Int) {
        dataStore.edit { settings ->
            // don't update next key if stored next key is less than next key
            // so remote mediator wont prematurely end the list
            if (nextKey > getNextKey() ?: 0) {
                settings[NEXT_KEY] = nextKey
            }
        }
    }

}