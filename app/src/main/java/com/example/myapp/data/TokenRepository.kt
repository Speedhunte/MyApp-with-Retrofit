package com.example.myapp.data


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class TokenRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun getToken() =  dataStore.data.map { it[AUTH_TOKEN_KEY]?:"" }
        .filter { it.isNotBlank() }
        .first()


    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = token
        }
    }
    suspend fun clearToken() {
        //Log.d("mytag", "deleting token")
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }

}