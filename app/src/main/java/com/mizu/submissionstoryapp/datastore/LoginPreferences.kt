package com.mizu.submissionstoryapp.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val SESSION_TOKEN = stringPreferencesKey("session_token")

    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginPreferences {
            return INSTANCE ?: synchronized(this){
                val instance = LoginPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getSessionToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SESSION_TOKEN] ?: ""
        }
    }

    suspend fun saveSessionToken(sessionToken: String){
        dataStore.edit { preferences ->
            preferences[SESSION_TOKEN] = sessionToken
        }
    }

    suspend fun removeSessionToken(){
        dataStore.edit { preferences ->
            preferences[SESSION_TOKEN] = ""
        }
    }


}