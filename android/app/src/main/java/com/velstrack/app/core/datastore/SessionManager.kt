package com.velstrack.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "velstrack_session")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {
    
    companion object {
        private val JWT_TOKEN = stringPreferencesKey("jwt_token")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun saveSession(token: String, role: String, userId: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
            preferences[USER_ROLE] = role
            preferences[USER_ID] = userId
        }
    }

    fun getJwtToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN]
        }
    }

    fun getUserRole(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ROLE]
        }
    }

    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
